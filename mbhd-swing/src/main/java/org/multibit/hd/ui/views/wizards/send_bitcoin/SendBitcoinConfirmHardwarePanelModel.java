package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "Confirm Trezor" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendBitcoinConfirmHardwarePanelModel extends AbstractWizardPanelModel {

  /**
   * @param panelName          The panel name
   */
  public SendBitcoinConfirmHardwarePanelModel(
    String panelName

  ) {
    super(panelName);
  }

}
