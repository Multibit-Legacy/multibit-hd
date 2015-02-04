package org.multibit.hd.ui.views.wizards.credentials;

import org.multibit.hd.ui.views.components.enter_pin.EnterPinModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "credentials" wizard:</p>
 * <ul>
 * <li>Storage of state for the "confirm cipher key" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CredentialsConfirmCipherKeyPanelModel extends AbstractWizardPanelModel {

  /**
   * @param panelName     The panel name
   * @param enterPinModel The "enter pin" component model
   */
  public CredentialsConfirmCipherKeyPanelModel(
    String panelName,
    EnterPinModel enterPinModel
  ) {
    super(panelName);
  }

}
