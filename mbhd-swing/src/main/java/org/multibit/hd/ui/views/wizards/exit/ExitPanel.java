package org.multibit.hd.ui.views.wizards.exit;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.AbstractWizard;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class ExitPanel extends JPanel {

  private final AbstractWizard wizard;

  /**
   * The "cancel" action
   */
  private Action cancelAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      wizard.close();
    }
  };

  /**
   * The "exit" action
   */
  private Action exitAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {

      CoreEvents.fireShutdownEvent();

    }
  };

  /**
   * @param wizard The wizard managing the states
   */
  public ExitPanel(AbstractWizard wizard) {

    this.wizard = wizard;

    PanelDecorator.applyWizardTheme(this,wizardComponents(), MessageKey.EXIT_TITLE);

    add(Buttons.newCancelButton(cancelAction), "span 2,left,push");
    add(Buttons.newExitButton(exitAction), "right");

  }

  private JPanel wizardComponents() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill", // Layout constrains
      "[][][]", // Column constraints
      "[]" // Row constraints
    ));

    return panel;
  }
}
