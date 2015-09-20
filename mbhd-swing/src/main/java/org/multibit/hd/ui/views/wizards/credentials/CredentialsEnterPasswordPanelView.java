package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_environment_alert.DisplayEnvironmentAlertModel;
import org.multibit.hd.ui.views.components.display_environment_alert.DisplayEnvironmentAlertView;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.select_wallet.SelectWalletModel;
import org.multibit.hd.ui.views.components.select_wallet.SelectWalletView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.util.List;
import java.util.Locale;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Credentials: Enter password</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class CredentialsEnterPasswordPanelView extends AbstractWizardPanelView<CredentialsWizardModel, CredentialsEnterPasswordPanelModel> {

  // Panel specific components
  private ModelAndView<DisplayEnvironmentAlertModel, DisplayEnvironmentAlertView> displayEnvironmentPopoverMaV;
  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;
  private ModelAndView<SelectWalletModel, SelectWalletView> selectWalletMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public CredentialsEnterPasswordPanelView(AbstractWizard<CredentialsWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.LOCK, MessageKey.PASSWORD_TITLE, null);

  }

  @Override
  public void newPanelModel() {

    displayEnvironmentPopoverMaV = Popovers.newDisplayEnvironmentPopoverMaV(getPanelName());
    enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());
    enterPasswordMaV.getView().setAddLabel(false);
    selectWalletMaV = Components.newSelectWalletMaV(getPanelName());

    // Configure the panel model
    final CredentialsEnterPasswordPanelModel panelModel = new CredentialsEnterPasswordPanelModel(
      getPanelName(),
      enterPasswordMaV.getModel(),
      selectWalletMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setEnterPasswordPanelModel(panelModel);
    getWizardModel().setEnterPasswordPanelView(this);

    // Register components
    registerComponents(displayEnvironmentPopoverMaV, enterPasswordMaV, selectWalletMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXLayout(),
      "[]", // Column constraints
      "[]0[]20[]0[]20[]" // Row constraints
    ));

    contentPanel.add(Labels.newPasswordNote(), "wrap");
    contentPanel.add(enterPasswordMaV.getView().newComponentPanel(), "wrap");

    contentPanel.add(Labels.newSelectWalletNote(), "wrap");
    contentPanel.add(selectWalletMaV.getView().newComponentPanel(), "wrap");

    contentPanel.add(Labels.newRestoreWalletNote(), "wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<CredentialsWizardModel> wizard) {

    PanelDecorator.addExitCancelCreateRestoreUnlockAsNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Initialise with "Unlock" disabled to force users to enter a credentials
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      false
    );

  }

  @Override
  public boolean beforeShow() {
    Optional<Locale> localeOptional;
    if (Configurations.currentConfiguration != null && Configurations.currentConfiguration.getLocale() != null) {
      localeOptional = Optional.of(Configurations.currentConfiguration.getLocale());
    } else {
      localeOptional = Optional.absent();
    }

    List<WalletSummary> wallets = WalletManager.getSoftWalletSummaries(localeOptional);

    selectWalletMaV.getModel().setWalletList(wallets, WalletManager.INSTANCE.getCurrentWalletSummary());
    selectWalletMaV.getView().setEnabled(true);

    return true;
  }

  @Override
  public void afterShow() {

    registerDefaultButton(getNextButton());

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        enterPasswordMaV.getView().requestInitialFocus();

        // This requires a environment popover check
        checkForEnvironmentEventPopover(displayEnvironmentPopoverMaV);

        selectWalletMaV.getView().updateViewFromModel();

      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      isUnlockEnabled()
    );

  }

  /**
   * @return True if the "unlock" button should be enabled
   */
  private boolean isUnlockEnabled() {

    boolean passwordEntered = !Strings.isNullOrEmpty(
      getPanelModel().get()
        .getEnterPasswordModel()
        .getValue()
    );

    boolean walletsAvailable = !selectWalletMaV.getView().isEmpty();

    return passwordEntered && walletsAvailable;

  }

  /**
   * Allow further user interaction after a failed unlock process
   */
  public void enableForFailedUnlock() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on EDT");

    getNextButton().setEnabled(true);
    getExitButton().setEnabled(true);
    getRestoreButton().setEnabled(true);

    enterPasswordMaV.getView().setSpinnerVisible(false);
    enterPasswordMaV.getView().requestInitialFocus();

    selectWalletMaV.getView().setEnabled(true);

  }

  /**
   * Update the UI to reflect an incorrect password
   */
  public void incorrectPassword() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on EDT");

    enterPasswordMaV.getView().incorrectPassword();

  }
}