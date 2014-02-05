package org.multibit.hd.ui.views.wizards.receive_bitcoin;

import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressModel;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
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
public class ReceiveBitcoinEnterAmountPanelModel extends AbstractWizardPanelModel {

  private final EnterAmountModel enterAmountModel;
  private final DisplayBitcoinAddressModel displayBitcoinAddressModel;

  /**
   * @param panelName                  The panel name
   * @param enterAmountModel           The "enter amount" component model
   * @param displayBitcoinAddressModel The "display Bitcoin address" component model
   */
  public ReceiveBitcoinEnterAmountPanelModel(
    String panelName,
    EnterAmountModel enterAmountModel,
    DisplayBitcoinAddressModel displayBitcoinAddressModel
  ) {
    super(panelName);
    this.enterAmountModel = enterAmountModel;
    this.displayBitcoinAddressModel = displayBitcoinAddressModel;
  }

  /**
   * @return The amount model
   */
  public EnterAmountModel getEnterAmountModel() {
    return enterAmountModel;
  }

  /**
   * @return The display model
   */
  public DisplayBitcoinAddressModel getDisplayBitcoinAddressModel() {
    return displayBitcoinAddressModel;
  }

}
