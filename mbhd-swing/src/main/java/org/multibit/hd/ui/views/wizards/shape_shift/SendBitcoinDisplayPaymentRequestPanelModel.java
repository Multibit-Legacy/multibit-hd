package org.multibit.hd.ui.views.wizards.shape_shift;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "send bitcoin" payment request panel</li>
 * </ul>
 *
 * @since 0.0.8
 */
public class SendBitcoinDisplayPaymentRequestPanelModel extends AbstractWizardPanelModel {

  /**
   * @param panelName                  The panel name
   */
  public SendBitcoinDisplayPaymentRequestPanelModel(  String panelName ) {
    super(panelName);
  }

}
