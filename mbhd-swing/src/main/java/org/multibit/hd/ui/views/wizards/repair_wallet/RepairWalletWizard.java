package org.multibit.hd.ui.views.wizards.repair_wallet;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "repair wallet":</p>
 * <ol>
 * <li>Show progress report</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class RepairWalletWizard extends AbstractWizard<RepairWalletWizardModel> {

  public RepairWalletWizard(RepairWalletWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(RepairWalletState.REPAIR_WALLET.name(), new RepairWalletPanelView(this, RepairWalletState.REPAIR_WALLET.name()));

  }

}
