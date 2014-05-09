package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Wallet;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.dto.SendRequestSummary;
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
   * The "confirm" panel model
   */
  private EmptyWalletConfirmPanelModel confirmPanelModel;

  /**
   * The "report" panel model
   */
  private EmptyWalletReportPanelModel reportPanelModel;

  /**
   * Default transaction fee
   */
  private final BigInteger transactionFee = Satoshis.fromPlainAmount("0.0001"); // TODO needs to be displayed from a wallet.completeTx SendRequest.fee

  /**
   * The FeeService used to calculate the FeeState
   */
  private FeeService feeService;

  private final NetworkParameters networkParameters = BitcoinNetwork.current().get();

  /**
   * @param state     The state object
   */
  public EmptyWalletWizardModel(EmptyWalletState state) {
    super(state);
    CoreServices.uiEventBus.register(this);
  }

  @Override
  public void showNext() {

    switch (state) {
      case EMPTY_WALLET_ENTER_DETAILS:
        state = EMPTY_WALLET_CONFIRM;
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
   * @param enterDetailsPanelModel The "enter details" panel model
   */
  void setEnterDetailsPanelModel(EmptyWalletEnterDetailsPanelModel enterDetailsPanelModel) {
    this.enterDetailsPanelModel = enterDetailsPanelModel;
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param confirmPanelModel The "confirm" panel model
   */
  void setConfirmPanelModel(EmptyWalletConfirmPanelModel confirmPanelModel) {
    this.confirmPanelModel = confirmPanelModel;
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
   * @return The transaction fee (a.k.a "miner's fee") in satoshis
   */
  public BigInteger getTransactionFee() {
    return transactionFee;
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

    // Append miner's fee info
    BigInteger minerFeePaid = transactionCreationEvent.getFeePaid();
    if (minerFeePaid == null) {
      transactionInfo.setMinerFee(Optional.<BigInteger>absent());
    } else {
      transactionInfo.setMinerFee(Optional.of(minerFeePaid));
    }
    FiatPayment fiatPayment = new FiatPayment();
    // A send is denoted with a negative fiat amount
    // TODO Consider if the header is sufficient for sourcing this
    //fiatPayment.setAmount(getLocalAmount().negated());
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
        log.debug("Wallet file after to calculateFeeState is " + walletFileOptional.get().length() + " bytes");
      }

      return feeStateOptional;
    } else {
      return Optional.absent();
    }
  }

  private void emptyWallet() {

    // Actually send the bitcoin
    Preconditions.checkNotNull(enterDetailsPanelModel);
    Preconditions.checkNotNull(confirmPanelModel);

    BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    Preconditions.checkState(bitcoinNetworkService.isStartedOk(), "'bitcoinNetworkService' should be started");

    Address changeAddress = bitcoinNetworkService.getNextChangeAddress();

    // TODO Get the satoshis from somewhere
    //BigInteger satoshis = enterDetailsPanelModel.getEnterAmountModel().getSatoshis();
    BigInteger satoshis = BigInteger.ZERO;
    Address bitcoinAddress = enterDetailsPanelModel
      .getEnterRecipientModel()
      .getRecipient()
      .get()
      .getBitcoinAddress();
    String password = confirmPanelModel.getPasswordModel().getValue();

    Optional<FeeState> feeState = calculateBRITFeeState();

    // Send the bitcoins
    final SendRequestSummary sendRequestSummary = new SendRequestSummary(
      bitcoinAddress,
      satoshis,
      changeAddress,
      BitcoinNetworkService.DEFAULT_FEE_PER_KB,
      password,
      feeState,
      true);

    log.debug("Just about to send bitcoin: {}", sendRequestSummary);
    bitcoinNetworkService.send(sendRequestSummary);

    // The send throws TransactionCreationEvents and BitcoinSentEvents to which you subscribe to to work out success and failure.

  }

}
