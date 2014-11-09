package org.multibit.hd.ui.views.wizards.change_pin;

import org.multibit.hd.ui.views.components.enter_pin.EnterPinModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "change PIN" wizard:</p>
 * <ul>
 * <li>Storage of state for the "change PIN" panel</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ChangePinEnterPinPanelModel extends AbstractWizardPanelModel {

  private final EnterPinModel enterPinModel;

  /**
   * @param panelName     The panel name
   * @param enterPinModel The "enter PIN" component model
   */
  public ChangePinEnterPinPanelModel(
    String panelName,
    EnterPinModel enterPinModel) {
    super(panelName);
    this.enterPinModel = enterPinModel;
  }

  /**
   * @return The "enter PIN" model
   */
  public EnterPinModel getEnterPinModel() {
    return enterPinModel;
  }

}
