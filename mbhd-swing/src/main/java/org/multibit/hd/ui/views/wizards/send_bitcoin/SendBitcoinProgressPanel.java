package org.multibit.hd.ui.views.wizards.send_bitcoin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Show send progress</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class SendBitcoinProgressPanel extends JPanel {

  private final AbstractWizard wizard;

  /**
   * The "finish" action
   */
  private Action finishAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      wizard.close();
    }
  };

  /**
   * @param wizard The wizard managing the states
   */
  public SendBitcoinProgressPanel(AbstractWizard wizard) {

    this.wizard = wizard;

    PanelDecorator.applyWizardTheme(this, wizardComponents(), MessageKey.SEND_PROGRESS_TITLE);

    add(Buttons.newFinishButton(finishAction), "span 4,push,right");

  }


  private JPanel wizardComponents() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill", // Layout constrains
      "[][][]", // Column constraints
      "[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    panel.add(Panels.newBroadcastStatus(),"wrap");
    panel.add(Panels.newRelayStatus(),"wrap");
    panel.add(Panels.newConfirmationCount(),"wrap");

    return panel;
  }

}
