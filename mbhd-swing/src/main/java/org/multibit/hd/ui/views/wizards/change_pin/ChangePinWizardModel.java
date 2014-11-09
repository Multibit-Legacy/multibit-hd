package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Model object to provide the following to "change PIN wizard":</p>
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
   * The "change PIN" panel model
   */
  private ChangePinEnterPinPanelModel changePinPanelModel;

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
   * @return The PIN the user entered (must be able to unlock the current device)
   */
  public String getCurrentPin() {
    return changePinPanelModel.getEnterPinModel().getValue();
  }

  /**
   * @return The new PIN
   */
  public String getNewPin() {
    return changePinPanelModel.getEnterPinModel().getValue();
  }

  /**
   * @return The confirmed PIN
   */
  public String getConfirmedPin() {
    return changePinPanelModel.getEnterPinModel().getValue();
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param changePinPanelModel The "enter PIN" panel model
   */
  void setChangePinPanelModel(ChangePinEnterPinPanelModel changePinPanelModel) {
    this.changePinPanelModel = changePinPanelModel;
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
        state = ChangePinState.CHANGE_PIN_ENTER_NEW_PIN;
        break;
      case CHANGE_PIN_REPORT:
         state = ChangePinState.CHANGE_PIN_REPORT;
         break;
       default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }
}
