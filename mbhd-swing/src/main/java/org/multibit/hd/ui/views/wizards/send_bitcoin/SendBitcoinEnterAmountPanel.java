package org.multibit.hd.ui.views.wizards.send_bitcoin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.components.Buttons;

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

  public SendBitcoinEnterAmountPanel() {

    MigLayout layout = new MigLayout(
      "fill", // Layout constrains
      "[]", // Column constraints
      "[grow]10[shrink]" // Row constraints
    );
    setLayout(layout);

    add(panelControls(),"grow,wrap");

    add(Buttons.newCancelButton(onCancelAction()),"left");
    add(Buttons.newPreviousButton(onPreviousAction()),"right");
    add(Buttons.newNextButton(onNextAction()),"right");

  }

  private Action onCancelAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    };
  }

  private Action onPreviousAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    };
  }

  private Action onNextAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    };
  }

  private JPanel panelControls() {

    JPanel panel = new JPanel(new MigLayout("fill"));
    panel.add(new JLabel("Enter some amount"));

    return panel;
  }
}
