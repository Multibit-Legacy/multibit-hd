package org.multibit.hd.ui.views.wizards.sign_message;

import org.multibit.hd.ui.views.components.enter_pin.EnterPinModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send Bitcoin Trezor" wizard:</p>
 * <ul>
 * <li>Storage of state for the "enter pin" panel</li>
 * </ul>
 *
 * @since 0.0.8
 *  
 */
public class SignMessageEnterPinPanelModel extends AbstractWizardPanelModel {

  private final EnterPinModel enterPinModel;

  /**
   * @param panelName          The panel name
   * @param enterPinModel The "enter pin" component model
   */
  public SignMessageEnterPinPanelModel(
    String panelName,
    EnterPinModel enterPinModel
  ) {
    super(panelName);
    this.enterPinModel = enterPinModel;
  }

  /**
   * @return The "enter pin" model
   */
  public EnterPinModel getEnterPinModel() {
    return enterPinModel;
  }
}
