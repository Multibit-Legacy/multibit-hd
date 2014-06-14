package org.multibit.hd.ui.views.wizards.password;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "password" wizard:</p>
 * <ol>
 * <li>Enter password</li>
 * <li>Enter seed phrase</li>
 * <li>Report progress</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class PasswordWizard extends AbstractWizard<PasswordWizardModel> {

  public PasswordWizard(PasswordWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      PasswordState.PASSWORD_ENTER_PASSWORD.name(),
      new PasswordEnterPasswordPanelView(this, PasswordState.PASSWORD_ENTER_PASSWORD.name()));

    // Transition panel that is never shown
    wizardViewMap.put(
      PasswordState.PASSWORD_RESTORE.name(),
      new PasswordRestorePanelView(this, PasswordState.PASSWORD_RESTORE.name()));

  }

}
