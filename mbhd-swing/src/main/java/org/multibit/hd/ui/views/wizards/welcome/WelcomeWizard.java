package org.multibit.hd.ui.views.wizards.welcome;

import org.multibit.hd.ui.views.wizards.AbstractWizard;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI for "Send Bitcoin":</p>
 * <ol>
 * <li>Welcome and choose language</li>
 * <li>Create or restore a wallet</li>
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

    wizardPanel.add(new WelcomePanel(this), "Welcome");

  }


}
