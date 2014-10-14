package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "credentials" wizard:</p>
 * <ol>
 * <li>Enter credentials. These could be a password, a Trezor PIN or no Trezor PIN if no credentials are required</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class CredentialsWizard extends AbstractWizard<CredentialsWizardModel> {

  public CredentialsWizard(CredentialsWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      CredentialsState.CREDENTIALS_ENTER_PASSWORD.name(),
      new CredentialsEnterPasswordPanelView(this, CredentialsState.CREDENTIALS_ENTER_PASSWORD.name()));

    wizardViewMap.put(
       CredentialsState.CREDENTIALS_ENTER_PIN.name(),
       new CredentialsEnterPinPanelView(this, CredentialsState.CREDENTIALS_ENTER_PIN.name()));

    // TODO - no Trezor PIN panel

     // Transition panel that is never shown
    wizardViewMap.put(
      CredentialsState.CREDENTIALS_RESTORE.name(),
      new CredentialsRestorePanelView(this, CredentialsState.CREDENTIALS_RESTORE.name()));
  }


  // TODO Ensure that restore buttons have somewhere to transition to before the Welcome wizard starts up
//  @Override
//   public void showNext() {
//
//     switch (getModel().) {
//       case CREDENTIALS_ENTER_PASSWORD:
//         state = CREDENTIALS_RESTORE;
//         break;
//       case CREDENTIALS_ENTER_PIN:
//         state = CREDENTIALS_RESTORE;
//         break;
//       default:
//         throw new IllegalStateException("Unknown state: " + state.name());
//     }
//
//   }

}
