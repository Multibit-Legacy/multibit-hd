package org.multibit.hd.ui.views.wizards.wallet_details;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "wallet details":</p>
 * <ol>
 * <li>Wallet details</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class WalletDetailsWizard extends AbstractWizard<WalletDetailsWizardModel> {

  public WalletDetailsWizard(WalletDetailsWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(WalletDetailsState.WALLET_DETAILS.name(), new WalletDetailsPanelView(this, WalletDetailsState.WALLET_DETAILS.name()));

  }

}
