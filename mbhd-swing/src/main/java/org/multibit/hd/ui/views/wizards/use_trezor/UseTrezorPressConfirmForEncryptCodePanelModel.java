package org.multibit.hd.ui.views.wizards.use_trezor;

import org.multibit.hd.ui.views.components.enter_pin.EnterPinModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "use Trezor" wizard:</p>
 * <ul>
 * <li>Storage of state for the "enter pin" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class UseTrezorPressConfirmForEncryptCodePanelModel extends AbstractWizardPanelModel {

  private final EnterPinModel enterPinModel;

  /**
   * @param panelName          The panel name
   * @param enterPinModel The "enter pin" component model
   */
  public UseTrezorPressConfirmForEncryptCodePanelModel(
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
