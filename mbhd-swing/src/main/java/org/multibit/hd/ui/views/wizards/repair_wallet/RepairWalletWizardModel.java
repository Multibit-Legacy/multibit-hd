package org.multibit.hd.ui.views.wizards.repair_wallet;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "repair wallet" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RepairWalletWizardModel extends AbstractWizardModel<RepairWalletState> {

  /**
   * @param state The state object
   */
  public RepairWalletWizardModel(RepairWalletState state) {
    super(state);
  }
}
