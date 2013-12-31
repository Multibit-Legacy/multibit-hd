package org.multibit.hd.ui.views.wizards.welcome;

import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.AbstractWizard;

import javax.swing.*;

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
public class WelcomeWizard extends AbstractWizard {

  public WelcomeWizard() {

    super();

  }

  @Override
  protected void addWizardContent(JPanel wizardPanel) {

    wizardPanel.add(new WelcomePanel(this), Panels.WELCOME_ACTION_NAME);
    wizardPanel.add(new SelectWalletPanel(this), Panels.SELECT_WALLET_ACTION_NAME);
    wizardPanel.add(new CreateWalletPanel(this), Panels.CREATE_WALLET_ACTION_NAME);
    wizardPanel.add(new RestoreWalletPanel(this), Panels.RESTORE_WALLET_ACTION_NAME);
    wizardPanel.add(new CreateWalletPasswordPanel(this), Panels.CREATE_WALLET_PASSWORD_ACTION_NAME);

  }

}
