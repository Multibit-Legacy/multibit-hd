package org.multibit.hd.ui.views.wizards.verify_network;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "change credentials" wizard:</p>
 * <ol>
 * <li>Enter and confirm credentials</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class VerifyNetworkWizard extends AbstractWizard<VerifyNetworkWizardModel> {

  public VerifyNetworkWizard(VerifyNetworkWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      VerifyNetworkState.VERIFY_NETWORK_SHOW_REPORT.name(),
      new VerifyNetworkPanelView(this, VerifyNetworkState.VERIFY_NETWORK_SHOW_REPORT.name()));

  }

}
