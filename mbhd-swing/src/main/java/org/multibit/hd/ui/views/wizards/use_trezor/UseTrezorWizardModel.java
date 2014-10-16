package org.multibit.hd.ui.views.wizards.use_trezor;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "use Trezor wizard":</p>
 * <ul>
 * <li>Storage of PIN entered</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class UseTrezorWizardModel extends AbstractWizardModel<UseTrezorState> {

 /**
    * The "enter pin" panel model
    */
  private UseTrezorEnterPinPanelModel enterPinPanelModel;

  public UseTrezorWizardModel(UseTrezorState useTrezorState) {
    super(useTrezorState);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  public UseTrezorEnterPinPanelModel getEnterPinPanelModel() {
    return enterPinPanelModel;
  }

  public void setEnterPinPanelModel(UseTrezorEnterPinPanelModel enterPinPanelModel) {
    this.enterPinPanelModel = enterPinPanelModel;
  }

  @Override
   public void showNext() {

     switch (state) {
       case ENTER_PIN:
         state = UseTrezorState.PRESS_OK_FOR_UNLOCK;
         break;
       case NO_PIN_REQUIRED:
         state = UseTrezorState.PRESS_OK_FOR_UNLOCK;
         break;
       case PRESS_OK_FOR_UNLOCK:
         // Should be catered for by finish
         break;
       default:
         throw new IllegalStateException("Unknown state: " + state.name());
     }
   }
}
