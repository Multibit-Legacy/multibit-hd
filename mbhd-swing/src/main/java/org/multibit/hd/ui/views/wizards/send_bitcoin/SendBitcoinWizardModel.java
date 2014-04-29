package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Wallet;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.joda.money.BigMoney;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.events.TransactionCreationEvent;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exceptions.PaymentsSaveException;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.store.TransactionInfo;
import org.multibit.hd.core.utils.Satoshis;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigInteger;

import static org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState.*;

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
public class SendBitcoinWizardModel extends AbstractWizardModel<SendBitcoinState> {

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
   * The "report" panel model
   */
  private SendBitcoinReportPanelModel reportPanelModel;

  /**
   * Default transaction fee
   */
  private BigInteger transactionFee = Satoshis.fromPlainAmount("0.0001"); // TODO needs to be displayed from a wallet.completeTx SendRequest.fee

  /**
   * The FeeService used to calculate the FeeState
   */
  private FeeService feeService;

  /**
   * @param state The state object
   */
  public SendBitcoinWizardModel(SendBitcoinState state) {
    super(state);
    CoreServices.uiEventBus.register(this);
  }

  @Override
  public void showNext() {

    switch (state) {
      case SEND_ENTER_AMOUNT:
        state = SEND_CONFIRM_AMOUNT;
        break;
      case SEND_CONFIRM_AMOUNT:
        // The user has confirmed the send details and pressed the next button

        // TODO - check the password is correct in the UI

        // TODO - the transaction construction should be done BEFORE the confirm Bitcoin screen as the transaction fee should be shown
        //        this needs the separation of the completeTx and signing as the password is not known at tx completion time

        // TODO - the developer fee needs calculating and showing - should be provided by WalletManager
        // TODO - only allow a send once the blockchain is fully synchronised

        sendBitcoin();

        state = SEND_REPORT;
        break;
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
    }

  }

  private void sendBitcoin() {

    // Actually send the bitcoin
    Preconditions.checkNotNull(enterAmountPanelModel);
    Preconditions.checkNotNull(confirmPanelModel);

    BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    Preconditions.checkState(bitcoinNetworkService.isStartedOk(), "'bitcoinNetworkService' should be started");

    Address changeAddress = bitcoinNetworkService.getNextChangeAddress();

    BigInteger satoshis = enterAmountPanelModel.getEnterAmountModel().getSatoshis();
    Address bitcoinAddress = enterAmountPanelModel
      .getEnterRecipientModel()
      .getRecipient()
      .get()
      .getBitcoinAddress();
    String password = confirmPanelModel.getPasswordModel().getValue();

    Optional<FeeState> feeState = calculateBRITFeeState();
    log.debug("Just about to send bitcoin : amount = '{}', address = '{}', changeAddress = '{}'. feeState = {}",
            satoshis,
            bitcoinAddress,
            changeAddress,
            feeState
    );

    bitcoinNetworkService.send(
      bitcoinAddress,
      satoshis,
      changeAddress,
      BitcoinNetworkService.DEFAULT_FEE_PER_KB,
      password,
      feeState
    );

    // The send throws TransactionCreationEvents and BitcoinSentEvents to which you subscribe to to work out success and failure.

  }

  @Subscribe
  public void onTransactionCreationEvent(TransactionCreationEvent transactionCreationEvent) {
    // Only store successful transactions
    if (!transactionCreationEvent.isTransactionCreationWasSuccessful()) {
      return;
    }
    // Create a transactionInfo to match the event created
    TransactionInfo transactionInfo = new TransactionInfo();
    transactionInfo.setHash(transactionCreationEvent.getTransactionId());
    String note = getNotes();
    if (note == null) {
      note = "";
    }
    transactionInfo.setNote(note);
    BigInteger minerFeePaid = transactionCreationEvent.getFeePaid();
    if (minerFeePaid == null) {
      transactionInfo.setMinerFee(Optional.<BigInteger>absent());
    } else {
      transactionInfo.setMinerFee(Optional.of(minerFeePaid));
    }
    FiatPayment fiatPayment = new FiatPayment();
    // A send is denoted with a negative fiat amount
    fiatPayment.setAmount(getLocalAmount().negated());
    fiatPayment.setExchange(ExchangeKey.current().getExchangeName());

    Optional<ExchangeRateChangedEvent> exchangeRateChangedEvent = CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent();
    if (exchangeRateChangedEvent.isPresent()) {
      fiatPayment.setRate(exchangeRateChangedEvent.get().getRate().toString());
    } else {
      fiatPayment.setRate("");
    }
    transactionInfo.setAmountFiat(fiatPayment);

    WalletService walletService = CoreServices.getCurrentWalletService();
    walletService.addTransactionInfo(transactionInfo);
    try {
      walletService.writePayments();
    } catch (PaymentsSaveException pse) {
      ExceptionHandler.handleThrowable(pse);
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
  public BigInteger getSatoshis() {
    return enterAmountPanelModel
            .getEnterAmountModel()
            .getSatoshis();
  }

  /**
   * @return The local amount
   */
  public BigMoney getLocalAmount() {
    return enterAmountPanelModel
            .getEnterAmountModel()
            .getLocalAmount();
  }

  /**
   * @return The password the user entered
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
   * @param reportPanelModel The "confirm" panel model
   */
  void setReportPanelModel(SendBitcoinReportPanelModel reportPanelModel) {
    this.reportPanelModel = reportPanelModel;
  }

  /**
   * @return The transaction fee (a.k.a "miner's fee") in satoshis
   */
  public BigInteger getTransactionFee() {
    return transactionFee;
  }

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
      Optional<FeeState> feeStateOptional = Optional.of(feeService.calculateFeeState(wallet));
      if (walletFileOptional.isPresent()) {
        log.debug("Wallet file after to calculateFeeState is " +walletFileOptional.get().length() + " bytes");
      }

      return feeStateOptional;
    } else {
      return Optional.absent();
    }
  }
}
