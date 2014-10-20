package org.multibit.hd.ui.views.wizards.use_trezor;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "repair wallet report" wizard:</p>
 * <ul>
 * <li>Storage of state for the "repair wallet report " panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class UseTrezorWipeDevicePanelModel extends AbstractWizardPanelModel {

  /**
   * @param panelName The panel name
   */
  public UseTrezorWipeDevicePanelModel(String panelName) {
    super(panelName);
  }

}
