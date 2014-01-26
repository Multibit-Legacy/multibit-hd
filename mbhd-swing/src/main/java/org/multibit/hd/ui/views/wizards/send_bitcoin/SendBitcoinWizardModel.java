package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.api.Recipient;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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
  private BigDecimal transactionFee = new BigDecimal("0.0001");

  /**
   * Default developer fee
   */
  private BigDecimal developerFee = new BigDecimal("0.00005");

  /**
   * @param state The state object
   */
  public SendBitcoinWizardModel(SendBitcoinState state) {
    super(state);
  }

  @Override
  public void showNext() {

    switch (state) {
      case ENTER_AMOUNT:
        state = CONFIRM_AMOUNT;
        break;
      case CONFIRM_AMOUNT:
        // The user has confirmed the send details and pressed the next button

        // TODO - check the password is correct

        // TODO - the transaction construction should be done BEFORE the confirm Bitcoin screen as the transaction fee should be shown
        //        this needs the separation of the completeTx and signing as the password is not known at tx completion time

        // TODO - the developer fee needs calculating and showing - should be provided by WalletManager
        // TODO - only allow a send once the blockchain is fully synchronised

        sendBitcoin();

        state = SEND_BITCOIN_REPORT;
        break;
    }

  }

  @Override
  public void showPrevious() {

    switch (state) {
      case ENTER_AMOUNT:
        state = ENTER_AMOUNT;
        break;
      case CONFIRM_AMOUNT:
        state = ENTER_AMOUNT;
        break;
    }

  }

  private void sendBitcoin() {
    // Actually send the bitcoin
    Preconditions.checkNotNull(enterAmountPanelModel);
    Preconditions.checkNotNull(confirmPanelModel);

    BitcoinNetworkService bitcoinNetworkService = MultiBitHD.getBitcoinNetworkService();

    String changeAddress = bitcoinNetworkService.getNextChangeAddress();

    BigDecimal amountBTC = enterAmountPanelModel.getEnterAmountModel().getPlainBitcoinAmount();
    // Convert to satoshi
    // TODO - it's a bad idea to have different amount formats in different parts of the code
    BigInteger amountBTCBigInteger = amountBTC.multiply(BigDecimal.valueOf(100000000)).toBigInteger();
    String bitcoinAddress = enterAmountPanelModel.getEnterRecipientModel().getRecipient().getBitcoinAddress();
    String password = confirmPanelModel.getPasswordModel().getValue();
    log.debug("Just about to send bitcoin : amount = '" + amountBTCBigInteger
            + "', address = '" + bitcoinAddress + "', changeAddress = '" + changeAddress + "', password = '" + password + "'.");
    bitcoinNetworkService.send(bitcoinAddress, amountBTCBigInteger, changeAddress, BitcoinNetworkService.DEFAULT_FEE_PER_KB, password);

    // The send throws BitcoinSentEvents to which you subscribe to to work out success and failure.

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
      .getRecipient();
  }

  /**
   * @return The Bitcoin amount without symbolic multiplier
   */
  public BigDecimal getPlainBitcoinAmount() {
    return enterAmountPanelModel
      .getEnterAmountModel()
      .getPlainBitcoinAmount();
  }

  /**
   * @return The local amount
   */
  public BigDecimal getLocalAmount() {
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
    return confirmPanelModel
      .getNotes();
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
   * @return The transaction fee (a.k.a "miner's fee") without symbolic multiplier
   */
  public BigDecimal getPlainTransactionFee() {
    return transactionFee;
  }

  /**
   * @return The developer fee without symbolic multiplier
   */
  public BigDecimal getPlainDeveloperFee() {
    return developerFee;
  }
}
