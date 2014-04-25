package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "empty wallet":</p>
 * <ol>
 * <li>Show progress report</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class EmptyWalletWizard extends AbstractWizard<EmptyWalletWizardModel> {

  public EmptyWalletWizard(EmptyWalletWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(EmptyWalletState.EMPTY_WALLET.name(), new EmptyWalletPanelView(this, EmptyWalletState.EMPTY_WALLET.name()));

  }

}
