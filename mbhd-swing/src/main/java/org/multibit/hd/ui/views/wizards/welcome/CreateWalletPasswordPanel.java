package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Create and confirm a master password</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class CreateWalletPasswordPanel extends JPanel implements ActionListener {

  private static final Logger log = LoggerFactory.getLogger(WelcomePanel.class);

  private final AbstractWizard wizard;

  private final ModelAndView<ConfirmPasswordModel, ConfirmPasswordView> confirmPasswordMaV;

  /**
   * The "previous" action
   */
  private Action previousAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      wizard.previous();
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
  public CreateWalletPasswordPanel(AbstractWizard wizard) {

    this.wizard = wizard;
    this.confirmPasswordMaV = Components.newConfirmPassword();

    PanelDecorator.applyWizardTheme(this, wizardComponents(), MessageKey.CREATE_WALLET_PASSWORD_TITLE);

    // Swap buttons to maintain reading order
    if (Languages.isLeftToRight()) {
      if (wizard.isExiting()) {
        add(Buttons.newExitButton(wizard.getExitAction()), "span 2,push");
      } else {
        add(Buttons.newCancelButton(wizard.getCancelAction()), "span 2,push");
      }
      add(Buttons.newPreviousButton(nextAction), "right,shrink");
      add(Buttons.newNextButton(nextAction), "right,shrink");
    } else {
      add(Buttons.newNextButton(nextAction), "left,push");
      add(Buttons.newPreviousButton(previousAction), "left,push");
      if (wizard.isExiting()) {
        add(Buttons.newExitButton(wizard.getExitAction()), "span 2,shrink");
      } else {
        add(Buttons.newCancelButton(wizard.getCancelAction()), "span 2,shrink");
      }
    }

  }

  private JPanel wizardComponents() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill", // Layout constrains
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Labels.newWalletPasswordNote(),"wrap");
    panel.add(confirmPasswordMaV.getView().newPanel(),"wrap");

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
    String command = String.valueOf(source.getActionCommand());

    wizard.show(command);

  }
}
