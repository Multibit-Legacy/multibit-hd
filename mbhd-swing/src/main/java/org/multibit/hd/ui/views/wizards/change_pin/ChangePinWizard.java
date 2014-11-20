package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "change PIN" wizard:</p>
 * <ol>
 * <li>Enter and confirm new PIN</li>
 * </ol>
 *
 * @since 0.0.5
 *
 */
public class ChangePinWizard extends AbstractHardwareWalletWizard<ChangePinWizardModel> {

  public ChangePinWizard(ChangePinWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      ChangePinState.SELECT_OPTION.name(),
      new ChangePinSelectOptionPanelView(this, ChangePinState.SELECT_OPTION.name()));

    wizardViewMap.put(
      ChangePinState.REQUEST_CHANGE_PIN.name(),
      new ChangePinRequestChangePinPanelView(this, ChangePinState.REQUEST_CHANGE_PIN.name()));

    wizardViewMap.put(
      ChangePinState.REQUEST_REMOVE_PIN.name(),
      new ChangePinRequestRemovePinPanelView(this, ChangePinState.REQUEST_REMOVE_PIN.name()));

    wizardViewMap.put(
      ChangePinState.ENTER_CURRENT_PIN.name(),
      new ChangePinEnterPinPanelView(this, ChangePinState.ENTER_CURRENT_PIN.name()));

    wizardViewMap.put(
      ChangePinState.ENTER_NEW_PIN.name(),
      new ChangePinEnterPinPanelView(this, ChangePinState.ENTER_NEW_PIN.name()));

    wizardViewMap.put(
      ChangePinState.CONFIRM_NEW_PIN.name(),
      new ChangePinEnterPinPanelView(this, ChangePinState.CONFIRM_NEW_PIN.name()));

    wizardViewMap.put(
        ChangePinState.SHOW_REPORT.name(),
        new ChangePinReportPanelView(this, ChangePinState.SHOW_REPORT.name()));
  }
}
