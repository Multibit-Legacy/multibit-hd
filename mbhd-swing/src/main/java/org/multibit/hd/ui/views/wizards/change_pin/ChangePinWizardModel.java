package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Model object to provide the following to "credentials wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ChangePinWizardModel extends AbstractWizardModel<ChangePinState> {

  private static final Logger log = LoggerFactory.getLogger(ChangePinWizardModel.class);

  /**
   * The "change credentials" panel model
   */
  private ChangePinEnterPinPanelModel changePasswordPanelModel;

  /**
   * @param state The state object
   */
  public ChangePinWizardModel(ChangePinState state) {
    super(state);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @return The credentials the user entered (must be able to unlock the current wallet)
   */
  public String getEnteredPassword() {
    return changePasswordPanelModel.getEnterPasswordModel().getValue();
  }

  /**
   * @return The confirmed credentials (use this to lock up the current wallet)
   */
  public String getConfirmedPassword() {
    return changePasswordPanelModel.getConfirmPasswordModel().getValue();
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param changePasswordPanelModel The "enter credentials" panel model
   */
  void setChangePasswordPanelModel(ChangePinEnterPinPanelModel changePasswordPanelModel) {
    this.changePasswordPanelModel = changePasswordPanelModel;
  }

  @Subscribe
  public void onVerificationStatusChangedEvent(VerificationStatusChangedEvent event) {

    if (ChangePinState.CHANGE_PIN_ENTER_CURRENT_PIN.name().equals(event.getPanelName())) {
      ViewEvents.fireWizardButtonEnabledEvent(event.getPanelName(), WizardButton.NEXT, event.isOK());
    }

  }

  @Override
  public void showNext() {

    switch (state) {
      case CHANGE_PIN_ENTER_CURRENT_PIN:
        state = ChangePinState.CHANGE_PIN_REPORT;
        break;
      case CHANGE_PIN_REPORT:
         state = ChangePinState.CHANGE_PIN_REPORT;
         break;
       default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }
}
