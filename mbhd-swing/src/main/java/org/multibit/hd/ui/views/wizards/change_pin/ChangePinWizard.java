package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "change PIN" wizard:</p>
 * <ol>
 * <li>Enter and confirm new PIN</li>
 * </ol>
 *
 * @since 0.0.5
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
      ChangePinState.REQUEST_ADD_PIN.name(),
      new ChangePinRequestAddPinPanelView(this, ChangePinState.REQUEST_ADD_PIN.name()));

    wizardViewMap.put(
      ChangePinState.CONFIRM_ADD_PIN.name(),
      new ChangePinConfirmAddPinPanelView(this, ChangePinState.CONFIRM_ADD_PIN.name()));

    wizardViewMap.put(
      ChangePinState.REQUEST_CHANGE_PIN.name(),
      new ChangePinRequestChangePinPanelView(this, ChangePinState.REQUEST_CHANGE_PIN.name()));

    wizardViewMap.put(
      ChangePinState.CONFIRM_CHANGE_PIN.name(),
      new ChangePinConfirmChangePinPanelView(this, ChangePinState.CONFIRM_CHANGE_PIN.name()));

    wizardViewMap.put(
      ChangePinState.REQUEST_REMOVE_PIN.name(),
      new ChangePinRequestRemovePinPanelView(this, ChangePinState.REQUEST_REMOVE_PIN.name()));

    wizardViewMap.put(
      ChangePinState.CONFIRM_REMOVE_PIN.name(),
      new ChangePinConfirmRemovePinPanelView(this, ChangePinState.CONFIRM_REMOVE_PIN.name()));

    wizardViewMap.put(
      ChangePinState.ENTER_CURRENT_PIN.name(),
      new ChangePinEnterCurrentPinPanelView(this, ChangePinState.ENTER_CURRENT_PIN.name()));

    wizardViewMap.put(
      ChangePinState.ENTER_NEW_PIN.name(),
      new ChangePinEnterNewPinPanelView(this, ChangePinState.ENTER_NEW_PIN.name()));

    wizardViewMap.put(
      ChangePinState.CONFIRM_NEW_PIN.name(),
      new ChangePinConfirmNewPinPanelView(this, ChangePinState.CONFIRM_NEW_PIN.name()));

    wizardViewMap.put(
      ChangePinState.SHOW_REPORT.name(),
      new ChangePinReportPanelView(this, ChangePinState.SHOW_REPORT.name()));
  }

  @Override
  public <P> Action getNextAction(final AbstractWizardPanelView<ChangePinWizardModel, P> wizardPanelView) {

    // Change the Next button handling for PIN entry screens to avoid transitions

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Ensure the panel updates its model (the button is outside of the panel itself)
        wizardPanelView.updateFromComponentModels(Optional.absent());

        switch (getWizardModel().getState()) {

          case ENTER_CURRENT_PIN:
          case ENTER_NEW_PIN:
          case CONFIRM_NEW_PIN:
            // Treat as a PIN entry
            getWizardModel().providePin(getWizardModel().getMostRecentPin());
            break;
          default:
            // Treat as a Next

            // Move to the next state
            getWizardModel().showNext();

            // Show the panel based on the state
            show(getWizardModel().getPanelName());

            break;
        }
      }

    };

  }

}
