package org.multibit.hd.ui.views.wizards.exit;

import org.multibit.hd.ui.views.wizards.AbstractWizard;

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

  public ExitWizard() {

    super();

    getContentPanel().add(new ExitPanel(this), "Exit");

  }

}
