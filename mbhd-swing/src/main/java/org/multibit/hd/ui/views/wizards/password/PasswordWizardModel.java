package org.multibit.hd.ui.views.wizards.password;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.multibit.hd.ui.views.wizards.password.PasswordState.*;

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
public class PasswordWizardModel extends AbstractWizardModel<PasswordState> {

  private static final Logger log = LoggerFactory.getLogger(PasswordWizardModel.class);

  /**
   * The "enter password" panel model
   */
  private PasswordEnterPasswordPanelModel enterPasswordPanelModel;

  /**
   * The "enter seed phrase" panel model
   */
  private PasswordEnterSeedPhrasePanelModel enterSeedPhrasePanelModel;

  /**
   * The "report" panel model
   */
  private PasswordReportPanelModel reportPanelModel;

  /**
   * @param state The state object
   */
  public PasswordWizardModel(PasswordState state) {
    super(state);
  }

  @Override
  public void showNext() {

    switch (state) {
      case PASSWORD_ENTER_PASSWORD:
        state = PASSWORD_ENTER_SEED_PHRASE;
        break;
      case PASSWORD_ENTER_SEED_PHRASE:
        // The user has entered their seed phrase
        state = PASSWORD_REPORT;
        break;
      default:
        throw new IllegalStateException("Unexpected state for 'next': "+state.name());
    }
  }

  @Override
  public void showPrevious() {

    switch (state) {
      case PASSWORD_ENTER_SEED_PHRASE:
        state = PASSWORD_ENTER_PASSWORD;
        break;
      case PASSWORD_REPORT:
        state = PASSWORD_ENTER_PASSWORD;
        break;
      default:
        throw new IllegalStateException("Unexpected state for 'previous': "+state.name());
    }

  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @return The password the user entered
   */
  public String getPassword() {
    return enterPasswordPanelModel.getEnterPasswordModel().getValue();
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param enterPasswordPanelModel The "enter password" panel model
   */
  void setEnterPasswordPanelModel(PasswordEnterPasswordPanelModel enterPasswordPanelModel) {
    this.enterPasswordPanelModel = enterPasswordPanelModel;
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param enterSeedPhrasePanelModel The "enter seed phrase" panel model
   */
  void setEnterSeedPhrasePanelModel(PasswordEnterSeedPhrasePanelModel enterSeedPhrasePanelModel) {
    this.enterSeedPhrasePanelModel = enterSeedPhrasePanelModel;
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param reportPanelModel The "report" panel model
   */
  void setReportPanelModel(PasswordReportPanelModel reportPanelModel) {
    this.reportPanelModel = reportPanelModel;
  }

}
