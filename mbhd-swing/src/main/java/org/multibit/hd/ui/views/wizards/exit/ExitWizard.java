package org.multibit.hd.ui.views.wizards.exit;

import org.multibit.hd.ui.views.wizards.AbstractWizard;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI for "Exit":</p>
 * <ol>
 * <li>Confirm choice</li>
 * </ol>
 *
 * @since 0.0.1
 *         
 */
public class ExitWizard extends AbstractWizard {

  @Override
  protected void addWizardContent(JPanel wizardPanel) {

    wizardPanel.add(new ExitPanel(this), "Exit");

  }

}
