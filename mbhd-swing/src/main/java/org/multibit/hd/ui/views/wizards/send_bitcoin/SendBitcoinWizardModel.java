package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.multibit.hd.core.api.Recipient;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
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

  @SuppressWarnings("unchecked")
  @Override
  public void updateFromPanelModel(Optional panelModel) {

    // No state transitions occur in this method

    // TODO Consider migrating state into dedicated objects

    switch (state) {
      case ENTER_AMOUNT:

        enterAmountPanelModel = (SendBitcoinEnterAmountPanelModel) panelModel.get();

        // Determine any events
        ViewEvents.fireWizardButtonEnabledEvent(
          SendBitcoinState.ENTER_AMOUNT.name(),
          WizardButton.NEXT,
          isEnterAmountNextEnabled()
        );
        break;
      case CONFIRM_AMOUNT:

        confirmPanelModel = (SendBitcoinConfirmPanelModel) panelModel.get();

        // Determine any events
        ViewEvents.fireWizardButtonEnabledEvent(
          SendBitcoinState.CONFIRM_AMOUNT.name(),
          WizardButton.NEXT,
          isConfirmNextEnabled()
        );
        break;
      case SEND_BITCOIN_REPORT:
        break;
    }

  }

  @Override
  public void showNext() {

    switch (state) {
      case ENTER_AMOUNT:
        state = CONFIRM_AMOUNT;
        // Determine any events
        ViewEvents.fireWizardModelChangedEvent(SendBitcoinState.CONFIRM_AMOUNT.name());
        break;
      case CONFIRM_AMOUNT:
        // The user has confirmed the send details and pressed the next button

        // TODO - check the password is correct

        // TODO - the transaction construction should be done BEFORE the confirm Bitcoin screen as the transaction fee should be shown
        //        this needs the separation of the completeTx and signing as the password is not known at tx completion time

        // TODO - the developer fee needs calculating and showing - should be provided by WalletManager
        // TODO - only allow a send once the blockchain is fully synchronised

        // Actually send the bitcoin
        Preconditions.checkNotNull(enterAmountPanelModel);
        Preconditions.checkNotNull(confirmPanelModel);

        BitcoinNetworkService bitcoinNetworkService = MultiBitHD.getBitcoinNetworkService();

        String changeAddress = bitcoinNetworkService.getNextChangeAddress();

        BigDecimal amountBTC= enterAmountPanelModel.getEnterAmountModel().getRawBitcoinAmount();
        // Convert to satoshi
        // TODO - it's a bad idea to have different amount formats in different parts of the code
        BigInteger amountBTCBigInteger = amountBTC.multiply(BigDecimal.valueOf(100000000)).toBigInteger();
        String bitcoinAddress = enterAmountPanelModel.getEnterRecipientModel().getRecipient().getBitcoinAddress();
        String password = confirmPanelModel.getPasswordModel().getValue();
        log.debug("Just about to send bitcoin : amount = '" + amountBTCBigInteger
                + "', address = '" + bitcoinAddress + "', changeAddress = '" +changeAddress + "', password = '" + password + "'.");
        bitcoinNetworkService.send(bitcoinAddress, amountBTCBigInteger, changeAddress, BitcoinNetworkService.DEFAULT_FEE_PER_KB, password);

        // The send throws BitcoinSendEvents to which you subscribe to to work out success and failure.

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
  public BigDecimal getRawBitcoinAmount() {
    return enterAmountPanelModel
      .getEnterAmountModel()
      .getRawBitcoinAmount();
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
   * @return True if the "enter amount" panel next button should be enabled
   */
  private boolean isEnterAmountNextEnabled() {

    boolean bitcoinAmountOK = !enterAmountPanelModel
      .getEnterAmountModel()
      .getRawBitcoinAmount()
      .equals(BigDecimal.ZERO);

    boolean recipientOK = enterAmountPanelModel
      .getEnterRecipientModel()
      .getRecipient() != null;

    return bitcoinAmountOK && recipientOK;

  }

  /**
   * @return True if the "confirm" panel next button should be enabled
   */
  private boolean isConfirmNextEnabled() {

    return !Strings.isNullOrEmpty(getPassword());
  }

  /**
   * @return The transaction fee (a.k.a "miner's fee") without symbolic multiplier
   */
  public BigDecimal getRawTransactionFee() {
    return transactionFee;
  }

  /**
   * @return The developer fee without symbolic multiplier
   */
  public BigDecimal getRawDeveloperFee() {
    return developerFee;
  }
}
