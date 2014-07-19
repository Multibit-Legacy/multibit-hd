package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

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
public class WelcomeWizard extends AbstractWizard<WelcomeWizardModel> {

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

}
