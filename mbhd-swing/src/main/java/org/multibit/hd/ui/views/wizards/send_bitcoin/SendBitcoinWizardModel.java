package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.uri.BitcoinURI;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.*;
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
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;

import static org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState.*;

/**
 * <p>Model object to provide the following to "send bitcoin wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendBitcoinWizardModel extends AbstractHardwareWalletWizardModel<SendBitcoinState> {

  private static final Logger log = LoggerFactory.getLogger(SendBitcoinWizardModel.class);

  /**
   * The "enter amount" panel model
   */
  private SendBitcoinEnterAmountPanelModel enterAmountPanelModel;

  /**
   * The "confirm" panel model
   */
  private SendBitcoinConfirmPanelModel confirmPanelModel;

  /**
   * Keep track of which transaction output is being signed
   * Start with -1 to allow for initial increment
   */
  private int txOutputIndex = -1;

  private final Optional<BitcoinURI> bitcoinURI;

  /**
   * The SendRequestSummary that initially contains all the tx details, and then is signed prior to sending
   */
  private SendRequestSummary sendRequestSummary;
  private SendBitcoinConfirmTrezorPanelView sendBitcoinConfirmTrezorPanelView;

  /**
   * @param state     The state object
   * @param parameter The "send bitcoin" parameter object
   */
  public SendBitcoinWizardModel(SendBitcoinState state, SendBitcoinParameter parameter) {
    super(state);

    this.bitcoinURI = parameter.getBitcoinURI();

  }

  @Override
  public void showNext() {

    switch (state) {
      case SEND_ENTER_AMOUNT:

        // The user has entered the send details so the tx can be prepared
        // If the transaction was prepared OK this returns true, otherwise false
        // If there is insufficient money in the wallet a TransactionCreationEvent
        // with the details will be thrown

        if (prepareTransaction()) {
          state = SEND_CONFIRM_AMOUNT;
        } else {
          // Transaction did not prepare correctly
          state = SEND_REPORT;
        }
        break;
      case SEND_CONFIRM_AMOUNT:

        // The user has confirmed the send details and pressed the next button
        // For a non-Trezor wallet navigate directly to the send screen
        // Get the current wallet
        Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
        if (currentWalletSummary.isPresent()) {
          if (WalletType.TREZOR_HARD_WALLET.equals(currentWalletSummary.get().getWalletType())) {
            log.debug("Sending using a Trezor hard wallet");
            state = SEND_CONFIRM_TREZOR;
            sendBitcoin();
          } else {
            log.debug("Not sending from a Trezor hard wallet - send directly");
            sendBitcoin();

            state = SEND_REPORT;
          }
        } else {
          log.debug("No wallet summary - cannot send");
        }

        break;

      case SEND_CONFIRM_TREZOR:
        // Move to send report
        state = SEND_REPORT;
    }
  }

  @Override
  public void showPrevious() {

    switch (state) {
      case SEND_ENTER_AMOUNT:
        state = SEND_ENTER_AMOUNT;
        break;
      case SEND_CONFIRM_AMOUNT:
        state = SEND_ENTER_AMOUNT;
        break;
      case SEND_CONFIRM_TREZOR:
        state = SEND_CONFIRM_AMOUNT;
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
    return enterAmountPanelModel
      .getEnterRecipientModel()
      .getRecipient().get();
  }

  /**
   * @return The Bitcoin amount without symbolic multiplier
   */
  public Coin getCoinAmount() {
    return enterAmountPanelModel
      .getEnterAmountModel()
      .getCoinAmount();
  }

  /**
   * @return The local amount
   */
  public Optional<BigDecimal> getLocalAmount() {
    return enterAmountPanelModel
      .getEnterAmountModel()
      .getLocalAmount();
  }

  /**
   * @return The credentials the user entered
   */
  public String getPassword() {
    return confirmPanelModel.getPasswordModel().getValue();
  }

  /**
   * @return The notes the user entered
   */
  public String getNotes() {
    return confirmPanelModel.getNotes();
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param enterAmountPanelModel The "enter amount" panel model
   */
  void setEnterAmountPanelModel(SendBitcoinEnterAmountPanelModel enterAmountPanelModel) {
    this.enterAmountPanelModel = enterAmountPanelModel;
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param confirmPanelModel The "confirm" panel model
   */
  void setConfirmPanelModel(SendBitcoinConfirmPanelModel confirmPanelModel) {
    this.confirmPanelModel = confirmPanelModel;
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param sendBitcoinConfirmTrezorPanelView The "confirm Trezor sign" panel view
   */
  void setSendBitcoinConfirmTrezorPanelView(SendBitcoinConfirmTrezorPanelView sendBitcoinConfirmTrezorPanelView) {
    this.sendBitcoinConfirmTrezorPanelView = sendBitcoinConfirmTrezorPanelView;
  }

  /**
   * @return Any Bitcoin URI used to initiate this wizard
   */
  public Optional<BitcoinURI> getBitcoinURI() {
    return bitcoinURI;
  }

  /**
   * @return the SendRequestSummary that includes all the tx details
   */
  public SendRequestSummary getSendRequestSummary() {
    return sendRequestSummary;
  }

  /**
   * Prepare the Bitcoin transaction that will be sent after user confirmation
   *
   * @return True if the transaction was prepared OK
   */
  private boolean prepareTransaction() {

    Preconditions.checkNotNull(enterAmountPanelModel);
    Preconditions.checkNotNull(confirmPanelModel);

    // Ensure Bitcoin network service is started
    BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    Preconditions.checkState(bitcoinNetworkService.isStartedOk(), "'bitcoinNetworkService' should be started");

    Address changeAddress = bitcoinNetworkService.getNextChangeAddress();

    Coin coin = enterAmountPanelModel.getEnterAmountModel().getCoinAmount();
    Address bitcoinAddress = enterAmountPanelModel
      .getEnterRecipientModel()
      .getRecipient()
      .get()
      .getBitcoinAddress();

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

    // Prepare the transaction i.e work out the fee sizes (not empty wallet)
    sendRequestSummary = new SendRequestSummary(
      bitcoinAddress,
      coin,
      fiatPayment,
      changeAddress,
      BitcoinNetworkService.DEFAULT_FEE_PER_KB,
      null,
      feeState,
      false);

    log.debug("Just about to prepare transaction for sendRequestSummary: {}", sendRequestSummary);
    return bitcoinNetworkService.prepareTransaction(sendRequestSummary);
  }

  private void sendBitcoin() {

    // Actually send the bitcoin by signing using the credentials, committing to the wallet and broadcasting to the Bitcoin network
    Preconditions.checkNotNull(confirmPanelModel);

    // Copy the note into the sendRequestSummary
    if (confirmPanelModel.getNotes() != null) {
      sendRequestSummary.setNotes(Optional.of(confirmPanelModel.getNotes()));
    } else {
      sendRequestSummary.setNotes(Optional.<String>absent());
    }

    // Copy the credentials into the sendRequestSummary
    sendRequestSummary.setPassword(confirmPanelModel.getPasswordModel().getValue());

    BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    Preconditions.checkState(bitcoinNetworkService.isStartedOk(), "'bitcoinNetworkService' should be started");

    log.debug("Just about to send bitcoin: {}", sendRequestSummary);
    bitcoinNetworkService.send(sendRequestSummary);

    // The send throws TransactionCreationEvents and BitcoinSentEvents to which you subscribe to to work out success and failure.
  }

  /**
   * Populate the panel model with the Bitcoin URI details
   */
  void handleBitcoinURI() {

    if (!bitcoinURI.isPresent()) {
      return;
    }

    BitcoinURI uri = bitcoinURI.get();
    Optional<Address> address = Optional.fromNullable(uri.getAddress());
    Optional<Coin> amount = Optional.fromNullable(uri.getAmount());

    if (address.isPresent()) {

      final Optional<Recipient> recipient;

      // Get the current wallet
      Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();

      if (currentWalletSummary.isPresent()) {

        // Attempt to locate a contact with the address in the Bitcoin URI to reassure user
        List<Contact> contacts = CoreServices
          .getOrCreateContactService(currentWalletSummary.get().getWalletId())
          .filterContactsByBitcoinAddress(address.get());

        if (!contacts.isEmpty()) {
          // Offer the first contact with the matching address (already null checked)
          Address bitcoinAddress = contacts.get(0).getBitcoinAddress().get();
          recipient = Optional.of(new Recipient(bitcoinAddress));
          recipient.get().setContact(contacts.get(0));
        } else {
          // No matching contact so make an anonymous Recipient
          recipient = Optional.of(new Recipient(address.get()));
        }

      } else {
        // No current wallet so make an anonymous Recipient
        recipient = Optional.of(new Recipient(address.get()));
      }

      // Must have a valid address and therefore recipient to be here
      enterAmountPanelModel
        .getEnterRecipientModel()
        .setValue(recipient.get());

      // Add in any amount or treat as zero
      enterAmountPanelModel
        .getEnterAmountModel()
        .setCoinAmount(amount.or(Coin.ZERO));
    }
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
                confirmingOutput=Optional.of(currentTransaction.getOutput(txOutputIndex));
                break;
              }
            }  while (txOutputIndex < currentTransaction.getOutputs().size());

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

    sendBitcoinConfirmTrezorPanelView.setDisplayText(key, values);

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

            sendBitcoinConfirmTrezorPanelView.setOperationText(MessageKey.TREZOR_TRANSACTION_CREATED_OPERATION);
            sendBitcoinConfirmTrezorPanelView.setRecoveryText(MessageKey.CLICK_NEXT_TO_CONTINUE);
            sendBitcoinConfirmTrezorPanelView.setDisplayVisible(false);

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

      case SEND_CONFIRM_TREZOR:
        state = SendBitcoinState.SEND_REPORT;
        setReportMessageKey(MessageKey.TREZOR_SIGN_FAILURE);
        setReportMessageStatus(false);
        break;
      default:
        throw new IllegalStateException("Should not reach here from " + state.name());
    }

  }

}
