package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.multibit.hd.core.api.Recipient;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;

import java.math.BigDecimal;

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

  /**
   * The current state
   */
  private SendBitcoinState state = ENTER_AMOUNT;

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
  public void update(Optional panelModel) {

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
  public void next() {

    switch (state) {
      case ENTER_AMOUNT:
        state = CONFIRM_AMOUNT;
        // Determine any events
        ViewEvents.fireWizardModelChangedEvent(SendBitcoinState.CONFIRM_AMOUNT.name());
        break;
      case CONFIRM_AMOUNT:
        state = SEND_BITCOIN_REPORT;
        break;
    }

  }

  @Override
  public void previous() {

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
    return confirmPanelModel
      .getPassword();
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

    return !Strings.isNullOrEmpty(confirmPanelModel.getPassword());
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
