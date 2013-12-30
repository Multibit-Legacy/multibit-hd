package org.multibit.hd.ui.views.wizards.welcome;

import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Show send progress</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class SelectWalletPanel extends JPanel implements ActionListener {

  private static final Logger log = LoggerFactory.getLogger(WelcomePanel.class);

  private final AbstractWizard wizard;

  private String currentSelection = Panels.CREATE_WALLET_ACTION_NAME;

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
   * The "previous" action
   */
  private Action previousAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      wizard.show(Panels.CREATE_WALLET_ACTION_NAME);
    }
  };

  /**
   * The "next" action
   */
  private Action nextAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {

      wizard.show(currentSelection);
    }
  };

  /**
   * @param wizard The wizard managing the states
   */
  public SelectWalletPanel(AbstractWizard wizard) {

    this.wizard = wizard;

    PanelDecorator.applyWizardTheme(this, wizardComponents());

    // Swap buttons to maintain reading order
    if (Languages.isLeftToRight()) {
      add(Buttons.newExitButton(exitAction), "span 2,push");
      add(Buttons.newPreviousButton(previousAction), "right,shrink");
      add(Buttons.newNextButton(nextAction), "right,shrink");
    } else {
      add(Buttons.newNextButton(nextAction), "left,push");
      add(Buttons.newPreviousButton(previousAction), "left,push");
      add(Buttons.newExitButton(exitAction), "span 2,shrink");
    }

  }

  private JPanel wizardComponents() {

    JPanel panel = Panels.newPanel();

    panel.add(Labels.newSelectWalletTitle(), "wrap");
    panel.add(Components.newWalletSelector(this));

    return panel;
  }

  /**
   * <p>Handle the "select wallet" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JRadioButton source = (JRadioButton) e.getSource();
    currentSelection = String.valueOf(source.getActionCommand());

    log.debug("Selection changed to '{}'",currentSelection);

  }
}
