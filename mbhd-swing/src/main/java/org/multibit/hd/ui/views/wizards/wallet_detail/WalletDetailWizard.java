package org.multibit.hd.ui.views.wizards.wallet_detail;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "wallet detail":</p>
 * <ol>
 * <li>Confirm choice</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class WalletDetailWizard extends AbstractWizard<WalletDetailWizardModel> {

  public WalletDetailWizard(WalletDetailWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(WalletDetailState.WALLET_DETAIL.name(), new WalletDetailPanelView(this, WalletDetailState.WALLET_DETAIL.name()));

  }

}
