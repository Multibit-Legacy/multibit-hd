package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.welcome.create_hardware_wallet.*;
import org.multibit.hd.ui.views.wizards.welcome.create_wallet.*;
import org.multibit.hd.ui.views.wizards.welcome.restore_wallet.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Map;

import static org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState.*;

/**
 * <p>Wizard to provide the following to UI for "Welcome":</p>
 * <ol>
 * <li>Welcome and choose language</li>
 * <li>Create or restore a wallet</li>
 * <li>Create a wallet with seed phrase and backup location</li>
 * </ol>
 *
 * @since 0.0.1
 */
public class WelcomeWizard extends AbstractHardwareWalletWizard<WelcomeWizardModel> {

  public WelcomeWizard(WelcomeWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent(), false);
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {
    wizardViewMap.put(
      WELCOME_SELECT_LANGUAGE.name(),
      new WelcomeSelectLanguagePanelView(this, WELCOME_SELECT_LANGUAGE.name()));

    wizardViewMap.put(
      WELCOME_ATTACH_HARDWARE_WALLET.name(),
      new WelcomeAttachHardwareWalletPanelView(this, WELCOME_ATTACH_HARDWARE_WALLET.name()));

    wizardViewMap.put(
      WELCOME_SELECT_WALLET.name(),
      new WelcomeSelectWalletPanelView(this, WELCOME_SELECT_WALLET.name()));

    wizardViewMap.put(
      CREATE_WALLET_PREPARATION.name(),
      new CreateWalletPreparationPanelView(this, CREATE_WALLET_PREPARATION.name()));

    wizardViewMap.put(
      CREATE_WALLET_SELECT_BACKUP_LOCATION.name(),
      new CreateWalletSelectBackupLocationPanelView(this, CREATE_WALLET_SELECT_BACKUP_LOCATION.name()));

    wizardViewMap.put(
      CREATE_WALLET_SEED_PHRASE.name(),
      new CreateWalletSeedPhrasePanelView(this, CREATE_WALLET_SEED_PHRASE.name()));

    wizardViewMap.put(
      CREATE_WALLET_CONFIRM_SEED_PHRASE.name(),
      new CreateWalletConfirmSeedPhrasePanelView(this, CREATE_WALLET_CONFIRM_SEED_PHRASE.name()));

    wizardViewMap.put(
      CREATE_WALLET_CREATE_PASSWORD.name(),
      new CreateWalletCreatePasswordPanelView(this, CREATE_WALLET_CREATE_PASSWORD.name()));

    wizardViewMap.put(
      CREATE_WALLET_REPORT.name(),
      new CreateWalletReportPanelView(this, CREATE_WALLET_REPORT.name()));

    wizardViewMap.put(
      HARDWARE_CREATE_WALLET_PREPARATION.name(),
      new CreateHardwareWalletPreparationPanelView(this, HARDWARE_CREATE_WALLET_PREPARATION.name()));

    wizardViewMap.put(
      HARDWARE_CREATE_WALLET_SELECT_BACKUP_LOCATION.name(),
      new CreateHardwareWalletSelectBackupLocationPanelView(this, HARDWARE_CREATE_WALLET_SELECT_BACKUP_LOCATION.name()));

    wizardViewMap.put(
      HARDWARE_CREATE_WALLET_ENTER_DETAILS.name(),
      new CreateHardwareWalletEnterDetailsPanelView(this, HARDWARE_CREATE_WALLET_ENTER_DETAILS.name()));

    wizardViewMap.put(
      HARDWARE_CREATE_WALLET_REQUEST_CREATE_WALLET.name(),
      new CreateHardwareWalletRequestCreateWalletPanelView(this, HARDWARE_CREATE_WALLET_REQUEST_CREATE_WALLET.name()));

    wizardViewMap.put(
      HARDWARE_CREATE_WALLET_CONFIRM_CREATE_WALLET.name(),
      new CreateHardwareWalletConfirmCreateWalletPanelView(this, HARDWARE_CREATE_WALLET_CONFIRM_CREATE_WALLET.name()));

    wizardViewMap.put(
      HARDWARE_CREATE_WALLET_ENTER_NEW_PIN.name(),
      new CreateHardwareWalletEnterNewPinPanelView(this, HARDWARE_CREATE_WALLET_ENTER_NEW_PIN.name()));

    wizardViewMap.put(
      HARDWARE_CREATE_WALLET_CONFIRM_NEW_PIN.name(),
      new CreateHardwareWalletConfirmNewPinPanelView(this, HARDWARE_CREATE_WALLET_CONFIRM_NEW_PIN.name()));

    wizardViewMap.put(
      HARDWARE_CREATE_WALLET_CONFIRM_WORD.name(),
      new CreateHardwareWalletConfirmWordPanelView(this, HARDWARE_CREATE_WALLET_CONFIRM_WORD.name()));

    wizardViewMap.put(
      HARDWARE_CREATE_WALLET_REPORT.name(),
      new CreateHardwareWalletReportPanelView(this, HARDWARE_CREATE_WALLET_REPORT.name()));

    wizardViewMap.put(
      RESTORE_PASSWORD_SEED_PHRASE.name(),
      new RestorePasswordEnterSeedPhraseView(this, RESTORE_PASSWORD_SEED_PHRASE.name()));

    wizardViewMap.put(
      RESTORE_PASSWORD_REPORT.name(),
      new RestorePasswordReportPanelView(this, RESTORE_PASSWORD_REPORT.name()));

    wizardViewMap.put(
      RESTORE_WALLET_SEED_PHRASE.name(),
      new RestoreWalletSeedPhrasePanelView(this, RESTORE_WALLET_SEED_PHRASE.name()));

    wizardViewMap.put(
      RESTORE_WALLET_SELECT_BACKUP_LOCATION.name(),
      new RestoreWalletSelectBackupLocationPanelView(this, RESTORE_WALLET_SELECT_BACKUP_LOCATION.name()));

    wizardViewMap.put(
      RESTORE_WALLET_SELECT_BACKUP.name(),
      new RestoreWalletSelectBackupPanelView(this, RESTORE_WALLET_SELECT_BACKUP.name()));

    wizardViewMap.put(
      RESTORE_WALLET_TIMESTAMP.name(),
      new RestoreWalletTimestampPanelView(this, RESTORE_WALLET_TIMESTAMP.name()));

    wizardViewMap.put(
      RESTORE_WALLET_REPORT.name(),
      new RestoreWalletReportPanelView(this, RESTORE_WALLET_REPORT.name()));

  }

  @Override
  public <P> Action getNextAction(final AbstractWizardPanelView<WelcomeWizardModel, P> wizardPanelView) {

    // Provide specific behaviour depending on state

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Ensure the panel updates its model (the button is outside of the panel itself)
        wizardPanelView.updateFromComponentModels(Optional.absent());

        switch (getWizardModel().getState()) {

          case WELCOME_ATTACH_HARDWARE_WALLET:
            // Check for initialised hardware wallet on cold start
            Optional<HardwareWalletService> hardwareWalletService = CoreServices.getCurrentHardwareWalletService();
            if (hardwareWalletService.isPresent()
              && hardwareWalletService.get().isDeviceReady()
              && hardwareWalletService.get().isWalletPresent()) {
              // Initialised hardware wallet is attached so move directly to credentials
              // We cannot move from WELCOME_ATTACH_HARDWARE_WALLET due to complications
              // with language changes in soft wallets, so a state progress is made first
              // followed by a hide
              getWizardModel().showNext();
              hide(WelcomeWizardState.WELCOME_SELECT_WALLET.name(), false);
            } else {
              standardNext();
            }
            break;
          case WELCOME_SELECT_WALLET:

            // Radio buttons indicate the next state
            switch (getWizardModel().getSelectWalletChoice()) {
              case WELCOME_SELECT_WALLET:
                // Transition to credentials
                hide(getWizardModel().getPanelName(), false);
                break;
              default:
                standardNext();
                break;
            }

            break;
          case HARDWARE_CREATE_WALLET_ENTER_NEW_PIN:
          case HARDWARE_CREATE_WALLET_CONFIRM_NEW_PIN:
            // Treat as a PIN entry
            getWizardModel().providePin(getWizardModel().getMostRecentPin());
            break;
          default:
            // Treat as a Next

            // Move to the next state
            standardNext();

            break;

        }

      }
    };

  }

  /**
   * Standard "next" button handling
   */
  private void standardNext() {
    // Treat as a Next

    // Move to the next state
    getWizardModel().showNext();

    // Show the panel based on the state
    show(getWizardModel().getPanelName());
  }
}
