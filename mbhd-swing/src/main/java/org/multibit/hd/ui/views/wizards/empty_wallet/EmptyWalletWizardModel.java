package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.dto.SendRequestSummary;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.messages.ButtonRequest;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.math.BigDecimal;

import static org.multibit.hd.ui.views.wizards.empty_wallet.EmptyWalletState.*;


/**
 * <p>Model object to provide the following to "empty wallet wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * <p>This is very similar to the <code>SendBitcoinWizardModel</code> but there are subtle
 * differences that mean that they cannot share much code.</p>
 *
 * @since 0.0.1
 */
public class EmptyWalletWizardModel extends AbstractHardwareWalletWizardModel<EmptyWalletState> {

  private static final Logger log = LoggerFactory.getLogger(EmptyWalletWizardModel.class);

  /**
   * The "enter details" panel model
   */
  private EmptyWalletEnterDetailsPanelModel enterDetailsPanelModel;

  /**
   * Keep track of which transaction output is being signed
   * Start with -1 to allow for initial increment
   */
  private int txOutputIndex = -1;

  /**
   * The current wallet balance in coins
   */
  private final Optional<Coin> coinAmount;


  private BitcoinNetworkService bitcoinNetworkService;

  /**
   * The prepared tx
   */
  private SendRequestSummary sendRequestSummary;
  private EmptyWalletConfirmTrezorPanelView emptyWalletConfirmTrezorPanelView;

  /**
   * @param state The state object
   */
  public EmptyWalletWizardModel(EmptyWalletState state) {
    super(state);

    coinAmount = WalletManager.INSTANCE.getCurrentWalletBalance();

  }

  @Override
  public void showNext() {

    switch (state) {
      case EMPTY_WALLET_ENTER_DETAILS:

        // See if the user has entered a recipient that is in the current wallet
        Optional<Recipient> recipientOptional = enterDetailsPanelModel.getEnterRecipientModel().getRecipient();
        if (recipientOptional.isPresent()) {
          boolean isAddressMine = WalletManager.INSTANCE.isAddressMine(recipientOptional.get().getBitcoinAddress());

          // Update model so that status note is shown
          enterDetailsPanelModel.setAddressMine(isAddressMine);
          if (isAddressMine) {
            log.debug("The address being emptied to is in the wallet !");
            // Do not traverse to next page

            state = EmptyWalletState.EMPTY_WALLET_ENTER_DETAILS;
          }
        }

        if (prepareTransaction()) {
          state = EMPTY_WALLET_CONFIRM;
        } else {
          // Transaction did not prepare correctly
          state = EMPTY_WALLET_REPORT;
        }
        break;
      case EMPTY_WALLET_CONFIRM:

        // The user has confirmed the send details and pressed the next button
        emptyWallet();

        state = EmptyWalletState.EMPTY_WALLET_REPORT;
        break;
    }
  }

  @Override
  public void showPrevious() {

    switch (state) {
      case EMPTY_WALLET_ENTER_DETAILS:
        state = EMPTY_WALLET_ENTER_DETAILS;
        break;
      case EMPTY_WALLET_CONFIRM:
        state = EMPTY_WALLET_ENTER_DETAILS;
        break;
    }

  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @return The recipient the user identified
   */
  public Recipient getRecipient() {
    return enterDetailsPanelModel
      .getEnterRecipientModel()
      .getRecipient().get();
  }

  /**
   * @return The credentials the user entered
   */
  public String getPassword() {
    return enterDetailsPanelModel.getEnterPasswordModel().getValue();
  }

  /**
   * @return the SendRequestSummary with the payment info in it
   */
  public SendRequestSummary getSendRequestSummary() {
    return sendRequestSummary;
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param enterDetailsPanelModel The "enter details" panel model
   */
  void setEnterDetailsPanelModel(EmptyWalletEnterDetailsPanelModel enterDetailsPanelModel) {
    this.enterDetailsPanelModel = enterDetailsPanelModel;
  }

  /**
   * @return The current wallet balance in coins
   */
  public Optional<Coin> getCoinAmount() {
    return coinAmount;
  }

  /**
   * Prepare the transaction for sending - this does everything but sign the tx
   */
  private boolean prepareTransaction() {

    // Prepare the transaction for sending
    Preconditions.checkNotNull(enterDetailsPanelModel);

    // Ensure Bitcoin network service is started
    bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    Preconditions.checkState(bitcoinNetworkService.isStartedOk(), "'bitcoinNetworkService' should be started");

    Address changeAddress = bitcoinNetworkService.getNextChangeAddress();

    Address bitcoinAddress = enterDetailsPanelModel
      .getEnterRecipientModel()
      .getRecipient()
      .get()
      .getBitcoinAddress();

    String password = enterDetailsPanelModel.getEnterPasswordModel().getValue();

    Optional<FeeState> feeState = WalletManager.INSTANCE.calculateBRITFeeState(true);

    // Create the fiat payment - note that the fiat amount is not populated, only the exchange rate data.
    // This is because the client and transaction fee is only worked out at point of sending, and the fiat equivalent is computed from that
    Optional<FiatPayment> fiatPayment;
    Optional<ExchangeRateChangedEvent> exchangeRateChangedEvent = CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent();
    if (exchangeRateChangedEvent.isPresent()) {
      fiatPayment = Optional.of(new FiatPayment());
      fiatPayment.get().setRate(Optional.of(exchangeRateChangedEvent.get().getRate().toString()));
      // A send is denoted with a negative fiat amount
      fiatPayment.get().setAmount(Optional.<BigDecimal>absent());
      fiatPayment.get().setCurrency(Optional.of(exchangeRateChangedEvent.get().getCurrency()));
      fiatPayment.get().setExchangeName(Optional.of(ExchangeKey.current().getExchangeName()));
    } else {
      fiatPayment = Optional.absent();
    }

    // Configure for an empty wallet send request
    sendRequestSummary = new SendRequestSummary(
      bitcoinAddress,
      coinAmount.or(Coin.ZERO),
      fiatPayment,
      changeAddress,
      BitcoinNetworkService.DEFAULT_FEE_PER_KB,
      password,
      feeState,
      true);

    sendRequestSummary.setNotes(Optional.of(Languages.safeText(MessageKey.EMPTY_WALLET_TITLE)));

    // Work out if a client fee is being paid now
    if (feeState.isPresent()) {
      // With an empty wallet you always pay the client fee now (if above the dust level)
      if (feeState.get().getFeeOwed().compareTo(Transaction.MIN_NONDUST_OUTPUT) > 0) {
        // The fee is due now
        sendRequestSummary.setClientFeeAdded(Optional.of(feeState.get().getFeeOwed()));
      }
    }

    log.debug("Just about to prepare empty wallet transaction for sendRequestSummary: {}", sendRequestSummary);
    return bitcoinNetworkService.prepareTransaction(sendRequestSummary);

  }

  /**
   * Actually send the transaction
   */
  private void emptyWallet() {

    log.debug("Emptying wallet with: {}", sendRequestSummary);
    Preconditions.checkState(bitcoinNetworkService.isStartedOk(), "'bitcoinNetworkService' should be started");
    bitcoinNetworkService.send(sendRequestSummary);

    // The send throws TransactionCreationEvents and BitcoinSentEvents to which you subscribe to to work out success and failure.
  }

  public void setEmptyWalletConfirmTrezorPanelView(EmptyWalletConfirmTrezorPanelView emptyWalletConfirmTrezorPanelView) {
    this.emptyWalletConfirmTrezorPanelView = emptyWalletConfirmTrezorPanelView;
  }

  @Override
  public void showButtonPress(HardwareWalletEvent event) {

    log.debug("Received hardware event: '{}'.{}", event.getEventType().name(), event.getMessage());

    BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();

    // Update label with descriptive text matching what the Trezor is showing
    ButtonRequest buttonRequest = (ButtonRequest) event.getMessage().get();

    // General message is nothing
    MessageKey key = null;
    Object[] values = null;

    if (bitcoinNetworkService.getLastSendRequestSummaryOptional().isPresent() && bitcoinNetworkService.getLastWalletOptional().isPresent()) {

      // We have a send request and a wallet

      Wallet wallet = bitcoinNetworkService.getLastWalletOptional().get();

      Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage();

      Optional<Transaction> currentTransactionOptional = CoreServices.getOrCreateHardwareWalletService().get().getContext().getTransaction();
      if (currentTransactionOptional.isPresent()) {

        Transaction currentTransaction = currentTransactionOptional.get();
        // Substitute mBTC for MICON
        String bitcoinSymbolText = bitcoinConfiguration.getBitcoinSymbol();
        if (BitcoinSymbol.MICON.toString().equals(bitcoinSymbolText)) {
          bitcoinSymbolText = BitcoinSymbol.MBTC.getSymbol();
        }

        switch (buttonRequest.getButtonRequestType()) {
          case CONFIRM_OUTPUT:

            // Work out which output we're confirming (will be in same order as tx but wallet addresses will be ignored)

            Optional<TransactionOutput> confirmingOutput = Optional.absent();
            do {
              // Always increment from starting position (first button request is then 0 index)
              txOutputIndex++;

              if (!currentTransaction.getOutput(txOutputIndex).isMine(wallet)) {
                // Not owned by us so Trezor will show it on the display
                confirmingOutput = Optional.of(currentTransaction.getOutput(txOutputIndex));
                break;
              }
            } while (txOutputIndex < currentTransaction.getOutputs().size());

            if (confirmingOutput.isPresent()) {

              // Trezor will be displaying this output
              TransactionOutput output = confirmingOutput.get();

              String[] transactionOutputAmount = Formats.formatCoinAsSymbolic(output.getValue(), languageConfiguration, bitcoinConfiguration);

              Address transactionOutputAddress = output.getAddressFromP2PKHScript(MainNetParams.get());
              key = MessageKey.TREZOR_TRANSACTION_OUTPUT_CONFIRM_DISPLAY;
              values = new String[]{
                transactionOutputAmount[0] + transactionOutputAmount[1] + " " + bitcoinSymbolText,
                transactionOutputAddress == null ? "" : transactionOutputAddress.toString()
              };

            } else {
              throw new IllegalStateException("Trezor is confirming an output outside of the transaction. Have change addresses been ignored?");
            }
            break;
          case SIGN_TX:
            // Transaction#getValue() provides the net amount leaving the wallet which includes the fee
            // Trezor displays the sum of all external outputs and the fee separately
            // Thus we perform the calculation below to arrive at the same figure the transaction amount to reassure the user all is well
            Coin transactionAmount = currentTransaction.getValue(wallet).negate().subtract(currentTransaction.getFee());
            String[] transactionAmountFormatted = Formats.formatCoinAsSymbolic(transactionAmount, languageConfiguration, bitcoinConfiguration);

            String[] feeAmount = Formats.formatCoinAsSymbolic(currentTransaction.getFee(), languageConfiguration, bitcoinConfiguration);

            key = MessageKey.TREZOR_SIGN_CONFIRM_DISPLAY;
            values = new String[]{
              transactionAmountFormatted[0] + transactionAmountFormatted[1] + " " + bitcoinSymbolText,
              feeAmount[0] + feeAmount[1] + " " + bitcoinSymbolText
            };
            break;
          default:

        }
      }
    }

    emptyWalletConfirmTrezorPanelView.setDisplayText(key, values);

  }

  @Override
  public void showOperationSucceeded(HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Enable next button
          ViewEvents.fireWizardButtonEnabledEvent(
            getPanelName(),
            WizardButton.NEXT,
            true
          );

          // The tx is now complete so commit and broadcast it
          // Trezor will provide a signed serialized transaction
          byte[] deviceTxPayload = CoreServices.getOrCreateHardwareWalletService().get().getContext().getSerializedTx().toByteArray();

          log.info("DeviceTx payload:\n{}", Utils.HEX.encode(deviceTxPayload));

          // Load deviceTx
          Transaction deviceTx = new Transaction(MainNetParams.get(), deviceTxPayload);

          log.info("deviceTx:\n{}", deviceTx.toString());

          // Check the signatures are canonical
          for (TransactionInput txInput : deviceTx.getInputs()) {
            byte[] signature = txInput.getScriptSig().getChunks().get(0).data;
            if (signature != null) {
              log.debug(
                "Is signature canonical test result '{}' for txInput '{}', signature '{}'",
                TransactionSignature.isEncodingCanonical(signature),
                txInput.toString(),
                Utils.HEX.encode(signature));
            } else {
              log.warn("No signature data");
            }
          }

          log.debug("Committing and broadcasting the last tx");

          BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();

          if (bitcoinNetworkService.getLastSendRequestSummaryOptional().isPresent() && bitcoinNetworkService.getLastWalletOptional().isPresent()) {

            SendRequestSummary sendRequestSummary = bitcoinNetworkService.getLastSendRequestSummaryOptional().get();

            // Substitute the signed tx from the trezor
            log.debug("Substituting the Trezor signed tx '{}' for the unsigned version {}", deviceTx.toString(), sendRequestSummary.getSendRequest().get().tx.toString());
            sendRequestSummary.getSendRequest().get().tx = deviceTx;
            log.debug("The transaction fee was {}", sendRequestSummary.getSendRequest().get().fee);

            // Get the last wallet
            Wallet wallet = bitcoinNetworkService.getLastWalletOptional().get();

            // Clear the previous remembered tx so that it is not committed twice
            bitcoinNetworkService.setLastSendRequestSummaryOptional(Optional.<SendRequestSummary>absent());
            bitcoinNetworkService.setLastWalletOptional(Optional.<Wallet>absent());

            emptyWalletConfirmTrezorPanelView.setOperationText(MessageKey.TREZOR_TRANSACTION_CREATED_OPERATION);
            emptyWalletConfirmTrezorPanelView.setRecoveryText(MessageKey.CLICK_NEXT_TO_CONTINUE);
            emptyWalletConfirmTrezorPanelView.setDisplayVisible(false);

            bitcoinNetworkService.commitAndBroadcast(sendRequestSummary, wallet);

          } else {
            log.debug("Cannot commit and broadcast the last send as it is not present in bitcoinNetworkService");
          }

        }
      });

  }

  @Override
  public void showOperationFailed(HardwareWalletEvent event) {

    switch (state) {

      case EMPTY_WALLET_CONFIRM_TREZOR:
        state = EMPTY_WALLET_REPORT;
        setReportMessageKey(MessageKey.TREZOR_SIGN_FAILURE);
        setReportMessageStatus(false);
        break;
      default:
        throw new IllegalStateException("Should not reach here from " + state.name());
    }

  }

}
