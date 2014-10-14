package org.multibit.hd.ui.views.wizards.verify_network;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "verify network" wizard:</p>
 * <ul>
 * <li>Storage of state for the "verify network" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class VerifyNetworkPanelModel extends AbstractWizardPanelModel {

  /**
   * @param panelName          The panel name
   */
  public VerifyNetworkPanelModel(
    String panelName
  ) {
    super(panelName);
  }

}
