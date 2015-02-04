package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "send bitcoin" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SendBitcoinEnterAmountPanelModel extends AbstractWizardPanelModel {

  private final EnterRecipientModel enterRecipientModel;
  private final EnterAmountModel enterAmountModel;

  /**
   * @param panelName           The panel name
   * @param enterRecipientModel The "enter recipient" component model
   * @param enterAmountModel    The "enter amount" component model
   */
  public SendBitcoinEnterAmountPanelModel(
    String panelName,
    EnterRecipientModel enterRecipientModel,
    EnterAmountModel enterAmountModel
  ) {
    super(panelName);
    this.enterRecipientModel = enterRecipientModel;
    this.enterAmountModel = enterAmountModel;
  }

  /**
   * @return The recipient model
   */
  public EnterRecipientModel getEnterRecipientModel() {
    return enterRecipientModel;
  }

  /**
   * @return The amount model
   */
  public EnterAmountModel getEnterAmountModel() {
    return enterAmountModel;
  }

}
