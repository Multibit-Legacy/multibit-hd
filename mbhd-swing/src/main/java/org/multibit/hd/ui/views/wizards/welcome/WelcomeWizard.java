package org.multibit.hd.ui.views.wizards.welcome;

import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

import java.util.Map;

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

  public WelcomeWizard(WelcomeWizardModel model, boolean isExiting) {
    super(model, isExiting);
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardView> wizardViewMap) {

    wizardViewMap.put(WELCOME.name(), new WelcomeView(this));
    wizardViewMap.put(SELECT_WALLET.name(), new SelectWalletView(this));
    wizardViewMap.put(CREATE_WALLET_SEED_PHRASE.name(), new CreateWalletSeedPhraseView(this));
    wizardViewMap.put(CONFIRM_WALLET_SEED_PHRASE.name(), new ConfirmWalletSeedPhraseView(this));
    wizardViewMap.put(RESTORE_WALLET.name(), new RestoreWalletChoicesView(this));
    wizardViewMap.put(CREATE_WALLET_PASSWORD.name(), new CreateWalletPasswordView(this));

  }

}
