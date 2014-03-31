package org.multibit.hd.ui.views.wizards.change_password;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Model object to provide the following to "password wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ChangePasswordWizardModel extends AbstractWizardModel<ChangePasswordState> {

  private static final Logger log = LoggerFactory.getLogger(ChangePasswordWizardModel.class);

  /**
   * The "change password" panel model
   */
  private ChangePasswordPanelModel changePasswordPanelModel;

  /**
   * @param state The state object
   */
  public ChangePasswordWizardModel(ChangePasswordState state) {
    super(state);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @return The password the user entered (must be able to unlock the current wallet)
   */
  public String getEnteredPassword() {
    return changePasswordPanelModel.getEnterPasswordModel().getValue();
  }

  /**
   * @return The confirmed password (use this to lock up the current wallet)
   */
  public String getConfirmedPassword() {
    return changePasswordPanelModel.getConfirmPasswordModel().getValue();
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param changePasswordPanelModel The "enter password" panel model
   */
  void setChangePasswordPanelModel(ChangePasswordPanelModel changePasswordPanelModel) {
    this.changePasswordPanelModel = changePasswordPanelModel;
  }

  @Subscribe
  public void onVerificationStatusChangedEvent(VerificationStatusChangedEvent event) {

    if (ChangePasswordState.CHANGE_PASSWORD_ENTER_PASSWORD.name().equals(event.getPanelName())) {
      ViewEvents.fireWizardButtonEnabledEvent(event.getPanelName(), WizardButton.FINISH, event.isOK());
    }

  }

}
