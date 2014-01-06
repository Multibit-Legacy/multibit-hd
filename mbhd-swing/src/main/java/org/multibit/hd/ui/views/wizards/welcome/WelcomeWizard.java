package org.multibit.hd.ui.views.wizards.welcome;

import org.multibit.hd.ui.views.wizards.AbstractWizard;

import javax.swing.*;

import static org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState.*;

/**
 * <p>Wizard to provide the following to UI for "Welcome":</p>
 * <ol>
 * <li>Welcome and choose language</li>
 * <li>Create or restore a wallet</li>
 * <li>Create a wallet password</li>
 * </ol>
 *
 * @since 0.0.1
 *         
 */
public class WelcomeWizard extends AbstractWizard<WelcomeWizardModel> {

  public WelcomeWizard(WelcomeWizardModel model) {
    super(model);
  }

  @Override
  protected void addWizardContent(JPanel wizardPanel) {

    wizardPanel.add(new WelcomeView(this).getWizardPanel(), WELCOME.name());
    wizardPanel.add(new SelectWalletView(this).getWizardPanel(), SELECT_WALLET.name());
    wizardPanel.add(new CreateWalletSeedPhraseView(this).getWizardPanel(), CREATE_WALLET_SEED_PHRASE.name());
    wizardPanel.add(new ConfirmWalletSeedPhraseView(this).getWizardPanel(), CONFIRM_WALLET_SEED_PHRASE.name());
    wizardPanel.add(new RestoreWalletChoicesView(this).getWizardPanel(), RESTORE_WALLET.name());
    wizardPanel.add(new CreateWalletPasswordView(this).getWizardPanel(), CREATE_WALLET_PASSWORD.name());

  }

}
