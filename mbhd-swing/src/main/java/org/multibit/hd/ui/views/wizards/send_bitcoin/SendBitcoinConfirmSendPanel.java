package org.multibit.hd.ui.views.wizards.send_bitcoin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.components.Buttons;
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

    add(panelControls(), "grow,wrap");

    add(Buttons.newCancelButton(cancelAction), "left");
    add(Buttons.newPreviousButton(previousAction), "right");
    add(Buttons.newNextButton(nextAction), "right");

  }

  Action cancelAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      wizard.close();
    }
  };

  Action previousAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      wizard.previous();
    }
  };

  Action nextAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      wizard.next();
    }
  };


  private JPanel panelControls() {

    JPanel panel = new JPanel(new MigLayout("fill"));
    panel.add(new JLabel("Blurb"));

    return panel;
  }
}
