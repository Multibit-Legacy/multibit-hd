package org.multibit.hd.ui.views.wizards.edit_wallet;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "edit wallet":</p>
 * <ol>
 * <li>Enter details</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class EditWalletWizard extends AbstractWizard<EditWalletWizardModel> {

  public EditWalletWizard(EditWalletWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(EditWalletState.EDIT_WALLET.name(), new EditWalletPanelView(this, EditWalletState.EDIT_WALLET.name()));

  }

}
