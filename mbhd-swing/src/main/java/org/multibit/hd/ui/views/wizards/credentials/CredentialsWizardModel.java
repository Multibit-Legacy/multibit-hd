package org.multibit.hd.ui.views.wizards.credentials;

import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;

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
public class CredentialsWizardModel extends AbstractHardwareWalletWizardModel<CredentialsState> {

  /**
   * The "enter password" panel model
   */
  private CredentialsEnterPasswordPanelModel enterPasswordPanelModel;

  /**
    * The "enter pin" panel model
    */
   private CredentialsEnterPinPanelModel enterPinPanelModel;

   /**
   * The type of credentials being requested password/ Trezor PIN / no Trezor PIN
   */
  private final CredentialsRequestType credentialsRequestType;


  public CredentialsWizardModel(CredentialsState credentialsState, CredentialsRequestType credentialsRequestType) {
    super(credentialsState);
    this.credentialsRequestType = credentialsRequestType;
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @return The credentials the user entered
   */
  public String getCredentials() {
    switch (credentialsRequestType) {
      case PASSWORD :
        return enterPasswordPanelModel.getEnterPasswordModel().getValue();
      case TREZOR_PIN:
         return enterPinPanelModel.getEnterPinModel().getValue();
      case NO_TREZOR_PIN:
      default:
          return "";
    }
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param enterPasswordPanelModel The "enter credentials" panel model
   */
  void setEnterPasswordPanelModel(CredentialsEnterPasswordPanelModel enterPasswordPanelModel) {
    this.enterPasswordPanelModel = enterPasswordPanelModel;
  }

  public CredentialsRequestType getCredentialsRequestType() {
    return credentialsRequestType;
  }

  public CredentialsEnterPinPanelModel getEnterPinPanelModel() {
    return enterPinPanelModel;
  }

  public void setEnterPinPanelModel(CredentialsEnterPinPanelModel enterPinPanelModel) {
    this.enterPinPanelModel = enterPinPanelModel;
  }
}
