package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Coin;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.dto.SendRequestSummary;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Coins;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
   * Default transaction fee
   */
  private final Coin transactionFee = Coins.fromPlainAmount("0.0001"); // TODO needs to be displayed from a wallet.completeTx SendRequest.fee

  /**
   * The FeeService used to calculate the FeeState
   */
  private FeeService feeService;

  /**
   * The current wallet balance in coins
   */
  private final Coin coinAmount;

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

    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    if (currentWalletSummary.isPresent()) {
      // Use the real wallet data
      this.coinAmount = currentWalletSummary.get().getWallet().getBalance();
    } else {
      // Unknown at this time
      this.coinAmount = Coin.ZERO;
    }

  }

  @Override
  public void showNext() {

    switch (state) {
      case EMPTY_WALLET_ENTER_DETAILS:
        state = EMPTY_WALLET_CONFIRM;
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
   * @return The transaction fee (a.k.a "miner's fee") in coins
   */
  public Coin getTransactionFee() {
    return transactionFee;
  }

  /**
   * @return The current wallet balance in coins
   */
  public Coin getCoinAmount() {
    return coinAmount;
  }

//  @Subscribe
//  public void onTransactionCreationEvent(TransactionCreationEvent transactionCreationEvent) {
//
//    // Only store successful transactions
//    if (!transactionCreationEvent.isTransactionCreationWasSuccessful()) {
//      return;
//    }
//
//    // Create a transactionInfo to match the event created
//    TransactionInfo transactionInfo = new TransactionInfo();
//    transactionInfo.setHash(transactionCreationEvent.getTransactionId());
//    transactionInfo.setNote(Languages.safeText(MessageKey.EMPTY_WALLET_TITLE));
//
//    // Append miner's fee info
//    transactionInfo.setMinerFee(transactionCreationEvent.getMiningFeePaid());
//
//    // Append client fee info
//    transactionInfo.setClientFee(transactionCreationEvent.getClientFeePaid());
//
//    // Set the fiat payment amount
//    transactionInfo.setAmountFiat(transactionCreationEvent.getFiatPayment().orNull());
//
//    WalletService walletService = CoreServices.getCurrentWalletService();
//    walletService.addTransactionInfo(transactionInfo);
//    try {
//      walletService.writePayments();
//    } catch (PaymentsSaveException pse) {
//      ExceptionHandler.handleThrowable(pse);
//    }
//  }

  /**
   * @return The BRIT fee state for the current wallet
   */
  public Optional<FeeState> calculateBRITFeeState() {

    if (feeService == null) {
      feeService = CoreServices.createFeeService();
    }

    if (WalletManager.INSTANCE.getCurrentWalletSummary() != null &&
      WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();

      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
      Optional<File> walletFileOptional = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory);
      if (walletFileOptional.isPresent()) {
        log.debug("Wallet file prior to calculateFeeState is " + walletFileOptional.get().length() + " bytes");
      }
      Optional<FeeState> feeState = Optional.of(feeService.calculateFeeState(wallet, true));
      if (walletFileOptional.isPresent()) {
        log.debug("Wallet file after to calculateFeeState is " + walletFileOptional.get().length() + " bytes");
      }

      return feeState;
    } else {
      return Optional.absent();
    }
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

    Optional<FeeState> feeState = calculateBRITFeeState();

    sendRequestSummary = new SendRequestSummary(
            bitcoinAddress,
            coinAmount,
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
