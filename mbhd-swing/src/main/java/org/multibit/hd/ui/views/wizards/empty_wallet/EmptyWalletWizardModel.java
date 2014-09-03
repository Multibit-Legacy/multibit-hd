package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Coin;
import com.google.bitcoin.core.Transaction;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.dto.SendRequestSummary;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.multibit.hd.ui.views.wizards.empty_wallet.EmptyWalletState.EMPTY_WALLET_CONFIRM;
import static org.multibit.hd.ui.views.wizards.empty_wallet.EmptyWalletState.EMPTY_WALLET_ENTER_DETAILS;


/**
 * <p>Model object to provide the following to "send bitcoin wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EmptyWalletWizardModel extends AbstractWizardModel<EmptyWalletState> {

  private static final Logger log = LoggerFactory.getLogger(EmptyWalletWizardModel.class);

  /**
   * The "enter details" panel model
   */
  private EmptyWalletEnterDetailsPanelModel enterDetailsPanelModel;

  /**
   * The "report" panel model
   */
  private EmptyWalletReportPanelModel reportPanelModel;

  /**
   * The current wallet balance in coins
   */
  private final Optional<Coin> coinAmount;


  private BitcoinNetworkService bitcoinNetworkService;

  /**
   * The prepared tx
   */
  private SendRequestSummary sendRequestSummary;

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
        state = EMPTY_WALLET_CONFIRM;

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
            break;
          }
        }
        prepareTransaction();
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
   * @return The password the user entered
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
   * <p>Reduced visibility for panel models only</p>
   *
   * @param reportPanelModel The "confirm" panel model
   */
  void setReportPanelModel(EmptyWalletReportPanelModel reportPanelModel) {
    this.reportPanelModel = reportPanelModel;
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
  private void prepareTransaction() {

    // Prepare the transaction for sending
    Preconditions.checkNotNull(enterDetailsPanelModel);

    bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    Preconditions.checkState(bitcoinNetworkService.isStartedOk(), "'bitcoinNetworkService' should be started");

    Address changeAddress = bitcoinNetworkService.getNextChangeAddress();

    Address bitcoinAddress = enterDetailsPanelModel
            .getEnterRecipientModel()
            .getRecipient()
            .get()
            .getBitcoinAddress();

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

    String password = enterDetailsPanelModel.getEnterPasswordModel().getValue();

    Optional<FeeState> feeState = WalletManager.INSTANCE.calculateBRITFeeState();

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

    // Prepare the transaction - this works out the fee etc
    bitcoinNetworkService.prepareTransaction(sendRequestSummary);

    // Work out if a client fee is being paid now
    if (feeState.isPresent()) {
      // With an empty wallet you always pay the client fee now (if above the dust level)
      if (feeState.get().getFeeOwed().compareTo(Transaction.MIN_NONDUST_OUTPUT) > 0) {
        // The fee is due now
        sendRequestSummary.setClientFeeAdded(Optional.of(feeState.get().getFeeOwed()));
      }
    }
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
}
