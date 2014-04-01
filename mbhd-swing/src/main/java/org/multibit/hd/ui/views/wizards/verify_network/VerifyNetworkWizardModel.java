package org.multibit.hd.ui.views.wizards.verify_network;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;

/**
 * <p>Model object to provide the following to "verify network" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class VerifyNetworkWizardModel extends AbstractWizardModel<VerifyNetworkState> {

  /**
   * @param state The state object
   */
  public VerifyNetworkWizardModel(VerifyNetworkState state) {
    super(state);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  @Subscribe
  public void onVerificationStatusChangedEvent(VerificationStatusChangedEvent event) {

    if (VerifyNetworkState.VERIFY_NETWORK_SHOW_REPORT.name().equals(event.getPanelName())) {
      ViewEvents.fireWizardButtonEnabledEvent(event.getPanelName(), WizardButton.FINISH, event.isOK());
    }

  }

}
