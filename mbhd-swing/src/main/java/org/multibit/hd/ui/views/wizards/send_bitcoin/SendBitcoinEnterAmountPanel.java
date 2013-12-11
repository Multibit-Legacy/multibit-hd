package org.multibit.hd.ui.views.wizards.send_bitcoin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
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

public class SendBitcoinEnterAmountPanel extends JPanel {

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
  public SendBitcoinEnterAmountPanel(AbstractWizard wizard) {

    this.wizard = wizard;

    // TODO Create a WizardPanel
    // Standard wizard layout
    MigLayout layout = new MigLayout(
      "fill", // Layout constrains
      "[]", // Column constraints
      "[grow]10[shrink]" // Row constraints
    );
    setLayout(layout);

    add(addWizardComponents(), "grow,wrap");

    add(Buttons.newCancelButton(cancelAction), "left");
    add(Buttons.newPreviousButton(previousAction), "right");
    add(Buttons.newNextButton(nextAction), "right");

  }

  private JPanel addWizardComponents() {

    JPanel panel = new JPanel(new MigLayout(
      "fill", // Layout constrains
      "[][][]", // Column constraints
      "[]10[]10[]10[]" // Row constraints
    ));
    panel.add(Labels.newSendTitle(),"wrap");
    panel.add(Panels.newContactSearch(),"wrap");
    panel.add(Panels.newBitcoinAmount(),"wrap");

    return panel;
  }
}
