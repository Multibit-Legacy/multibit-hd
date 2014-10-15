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
}
