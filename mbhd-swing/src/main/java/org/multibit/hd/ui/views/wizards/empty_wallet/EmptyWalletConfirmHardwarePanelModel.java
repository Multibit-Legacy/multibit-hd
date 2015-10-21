package org.multibit.hd.ui.views.wizards.empty_wallet;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "empty wallet" wizard:</p>
 * <ul>
 * <li>Storage of state for the "Confirm Trezor" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EmptyWalletConfirmHardwarePanelModel extends AbstractWizardPanelModel {

  /**
   * @param panelName The panel name
   */
  public EmptyWalletConfirmHardwarePanelModel(
    String panelName

  ) {
    super(panelName);
  }

}
