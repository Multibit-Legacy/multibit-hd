package org.multibit.hd.ui.views.wizards.welcome;

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
    super(model, isExiting);
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      WELCOME.name(),
      new WelcomePanelView(this, WELCOME.name()));
    wizardViewMap.put(
      SELECT_WALLET.name(),
      new SelectWalletPanelView(this, SELECT_WALLET.name()));
    wizardViewMap.put(
      CREATE_WALLET_SEED_PHRASE.name(),
      new CreateWalletSeedPhrasePanelView(this, CREATE_WALLET_SEED_PHRASE.name()));
    wizardViewMap.put(
      CONFIRM_WALLET_SEED_PHRASE.name(),
      new ConfirmWalletSeedPhrasePanelView(this, CONFIRM_WALLET_SEED_PHRASE.name()));
    wizardViewMap.put(
      RESTORE_WALLET.name(),
      new RestoreWalletChoicesPanelView(this, RESTORE_WALLET.name()));
    wizardViewMap.put(
      CREATE_WALLET_PASSWORD.name(),
      new CreateWalletPasswordPanelView(this, CREATE_WALLET_PASSWORD.name()));
    wizardViewMap.put(
      SELECT_BACKUP_LOCATION.name(),
      new SelectBackupLocationPanelView(this, SELECT_BACKUP_LOCATION.name()));
    wizardViewMap.put(
      CREATE_WALLET_REPORT.name(),
      new CreateWalletReportPanelView(this, CREATE_WALLET_REPORT.name()));

  }

}
