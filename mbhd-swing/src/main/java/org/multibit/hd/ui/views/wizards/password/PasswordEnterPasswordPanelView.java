package org.multibit.hd.ui.views.wizards.password;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertModel;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertView;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class PasswordEnterPasswordPanelView extends AbstractWizardPanelView<PasswordWizardModel, PasswordEnterPasswordPanelModel> {

  // Panel specific components
  private ModelAndView<DisplaySecurityAlertModel, DisplaySecurityAlertView> displaySecurityAlertMaV;
  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public PasswordEnterPasswordPanelView(AbstractWizard<PasswordWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PASSWORD_TITLE, AwesomeIcon.LOCK);

  }

  @Override
  public void newPanelModel() {

    displaySecurityAlertMaV = Components.newDisplaySecurityAlertMaV();
    enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());

    // Configure the panel model
    final PasswordEnterPasswordPanelModel panelModel = new PasswordEnterPasswordPanelModel(
      getPanelName(),
      enterPasswordMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setEnterPasswordPanelModel(panelModel);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    contentPanel.add(Labels.newPasswordNote(), "wrap");

    contentPanel.add(enterPasswordMaV.getView().newComponentPanel(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<PasswordWizardModel> wizard) {

    PanelDecorator.addExitCancelRestoreUnlock(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      false
    );

  }

  @Override
  public void afterShow() {

    registerDefaultButton(getFinishButton());

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        enterPasswordMaV.getView().requestInitialFocus();

        // Check for any security alerts
        Optional<SecurityEvent> securityEvent = CoreServices.applicationEventService.getLatestSecurityEvent();
        if (securityEvent.isPresent()) {

          displaySecurityAlertMaV.getModel().setValue(securityEvent.get());

          // Show the security alert as a popover
          Panels.showLightBoxPopover(displaySecurityAlertMaV.getView().newComponentPanel());

        }

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExiting) {
    // If a password has been entered, put it into the WalletData (so that it is available for address generation)
    // TODO - remove when we have proper HD wallets  - won't need password for address generation
    CharSequence password = enterPasswordMaV.getModel().getValue();
    if (!"".equals(password)) {
      // TODO should be using WalletService
      Optional<WalletData> walletDataOptional = WalletManager.INSTANCE.getCurrentWalletData();
      if (walletDataOptional.isPresent()) {
        walletDataOptional.get().setPassword(password);
      }
    }

    // Keep track of this
    CoreServices.logHistory(Languages.safeText(MessageKey.CONFIRM_PASSWORD));

    return true;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      isFinishEnabled()
    );

  }

  /**
   * @return True if the "finish" button should be enabled
   */
  private boolean isFinishEnabled() {

    return !Strings.isNullOrEmpty(
      getPanelModel().get()
      .getEnterPasswordModel()
      .getValue());

  }

}