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
 * <li>Send bitcoin: Confirm send</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class SendBitcoinConfirmSendPanel extends JPanel {

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
   * The "send" action
   */
  private Action sendAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      wizard.next();
    }
  };

  /**
   * @param wizard The wizard managing the states
   */
  public SendBitcoinConfirmSendPanel(AbstractWizard wizard) {

    this.wizard = wizard;

    MigLayout layout = new MigLayout(
      "fill", // Layout constrains
      "[]", // Column constraints
      "[grow]10[shrink]" // Row constraints
    );
    setLayout(layout);

    add(addWizardComponents(), "grow,wrap");

    add(Buttons.newCancelButton(cancelAction), "left");
    add(Buttons.newPreviousButton(previousAction), "right");
    add(Buttons.newSendButton(sendAction), "right");

  }

  private JPanel addWizardComponents() {

    JPanel panel = new JPanel(new MigLayout(
      "fill", // Layout constrains
      "[][][]", // Column constraints
      "[]10[]10[]10[]" // Row constraints
    ));
    panel.add(Labels.newConfirmSendTitle(),"wrap");
    panel.add(Labels.newConfirmSendAmount(),"wrap");
    panel.add(Panels.newNotes(),"wrap");
    panel.add(Panels.newWalletPassword(),"wrap");

    return panel;
  }

}
