package org.multibit.hd.ui.views.wizards.change_password;

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
public class ChangePasswordWizard extends AbstractWizard<ChangePasswordWizardModel> {

  public ChangePasswordWizard(ChangePasswordWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      ChangePasswordState.CHANGE_PASSWORD_ENTER_PASSWORD.name(),
      new ChangePasswordPanelView(this, ChangePasswordState.CHANGE_PASSWORD_ENTER_PASSWORD.name()));
    wizardViewMap.put(
        ChangePasswordState.CHANGE_PASSWORD_REPORT.name(),
        new ChangePasswordReportPanelView(this, ChangePasswordState.CHANGE_PASSWORD_REPORT.name()));
  }
}
