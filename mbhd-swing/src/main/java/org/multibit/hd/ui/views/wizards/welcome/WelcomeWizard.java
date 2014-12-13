package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.welcome.create_trezor_wallet.CreateTrezorWalletPreparationPanelView;
import org.multibit.hd.ui.views.wizards.welcome.create_trezor_wallet.CreateTrezorWalletSelectBackupLocationPanelView;
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
 *
 */
public class WelcomeWizard extends AbstractHardwareWalletWizard<WelcomeWizardModel> {

  public WelcomeWizard(WelcomeWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      WELCOME_LICENCE.name(),
      new WelcomeLicencePanelView(this, WELCOME_LICENCE.name()));

    wizardViewMap.put(
      WELCOME_SELECT_LANGUAGE.name(),
      new WelcomeSelectLanguagePanelView(this, WELCOME_SELECT_LANGUAGE.name()));

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
      TREZOR_CREATE_WALLET_PREPARATION.name(),
      new CreateTrezorWalletPreparationPanelView(this, TREZOR_CREATE_WALLET_PREPARATION.name()));

    wizardViewMap.put(
      TREZOR_CREATE_WALLET_SELECT_BACKUP_LOCATION.name(),
      new CreateTrezorWalletSelectBackupLocationPanelView(this, TREZOR_CREATE_WALLET_SELECT_BACKUP_LOCATION.name()));

    // TODO Implement this
    wizardViewMap.put(
      TREZOR_CREATE_WALLET_REQUEST_CREATE_WALLET.name(),
      new CreateTrezorWalletPreparationPanelView(this, TREZOR_CREATE_WALLET_REQUEST_CREATE_WALLET.name()));

    // TODO Implement this
    wizardViewMap.put(
      TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET.name(),
      new CreateTrezorWalletPreparationPanelView(this, TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET.name()));

    // TODO Implement this
    wizardViewMap.put(
      TREZOR_CREATE_WALLET_CONFIRM_ENTROPY.name(),
      new CreateTrezorWalletPreparationPanelView(this, TREZOR_CREATE_WALLET_CONFIRM_ENTROPY.name()));

    // TODO Implement this
    wizardViewMap.put(
      TREZOR_CREATE_WALLET_ENTER_NEW_PIN.name(),
      new CreateTrezorWalletPreparationPanelView(this, TREZOR_CREATE_WALLET_ENTER_NEW_PIN.name()));

    // TODO Implement this
    wizardViewMap.put(
      TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN.name(),
      new CreateTrezorWalletPreparationPanelView(this, TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN.name()));

    // TODO Implement this
    wizardViewMap.put(
      TREZOR_CREATE_WALLET_CONFIRM_WORD.name(),
      new CreateTrezorWalletPreparationPanelView(this, TREZOR_CREATE_WALLET_CONFIRM_WORD.name()));

    // TODO Implement this
    wizardViewMap.put(
      TREZOR_CREATE_WALLET_REPORT.name(),
      new CreateTrezorWalletPreparationPanelView(this, TREZOR_CREATE_WALLET_REPORT.name()));

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

    // Merge the Next and Finish button behaviour

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Ensure the panel updates its model (the button is outside of the panel itself)
        wizardPanelView.updateFromComponentModels(Optional.absent());

        if (WelcomeWizardState.SELECT_EXISTING_WALLET.equals(getWizardModel().getSelectWalletChoice())) {

          // Treat as a Finish
          hide(getWizardModel().getPanelName(), false);

        } else {

          // Treat as a Next

          // Move to the next state
          getWizardModel().showNext();

          // Show the panel based on the state
          show(getWizardModel().getPanelName());
        }
      }

      ;
    };

  }
}
