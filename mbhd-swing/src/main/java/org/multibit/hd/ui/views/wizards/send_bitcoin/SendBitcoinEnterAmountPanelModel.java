package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "send bitcoin" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendBitcoinEnterAmountPanelModel {

  private final EnterRecipientModel enterRecipientModel;
  private final EnterAmountModel enterAmountModel;

  public SendBitcoinEnterAmountPanelModel(
    EnterRecipientModel enterRecipientModel, EnterAmountModel enterAmountModel) {
    this.enterAmountModel = enterAmountModel;
    this.enterRecipientModel = enterRecipientModel;
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
