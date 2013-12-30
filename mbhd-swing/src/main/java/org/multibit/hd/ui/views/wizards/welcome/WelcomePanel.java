package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Confirm send</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class WelcomePanel extends JPanel implements ActionListener {

  private static final Logger log = LoggerFactory.getLogger(WelcomePanel.class);

  private final AbstractWizard wizard;

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
   * The "next" action
   */
  private Action nextAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      wizard.next();
    }
  };

  /**
   * @param wizard The wizard managing the states
   */
  public WelcomePanel(AbstractWizard wizard) {

    this.wizard = wizard;

    PanelDecorator.applyWizardTheme(this, wizardComponents());

    // Swap buttons to maintain reading order
    if (Languages.isLeftToRight()) {
      add(Buttons.newExitButton(exitAction), "span 2,push");
      add(Buttons.newNextButton(nextAction), "right,shrink");
    } else {
      add(Buttons.newNextButton(nextAction), "left,push");
      add(Buttons.newExitButton(exitAction), "span 2,shrink");
    }

  }

  private JPanel wizardComponents() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill", // Layout constrains
      "[][][]", // Column constraints
      "[]10[]10[]10[]" // Row constraints
    ));

    panel.add(Labels.newWelcomeTitle(), "wrap");
    panel.add(Panels.newLanguageSelector(this), "wrap");
    panel.add(Labels.newWelcomeNote(), "wrap");

    return panel;
  }

  /**
   * <p>Handle the change locale action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    String localeCode = String.valueOf(source.getSelectedItem()).substring(0,2);

    Locale locale = Languages.newLocaleFromCode(localeCode);

    log.debug("Language changed to '{}'",localeCode);

    ControllerEvents.fireChangeLocaleEvent(locale);

  }
}
