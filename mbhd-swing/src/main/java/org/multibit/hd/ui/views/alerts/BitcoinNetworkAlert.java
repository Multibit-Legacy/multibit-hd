package org.multibit.hd.ui.views.alerts;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin sequence</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BitcoinNetworkAlert {

  private final JPanel panel;

  public BitcoinNetworkAlert(String localisedMessage) {

    panel = new JPanel(new MigLayout(
      "debug,fillx",
      "[][right]", // Columns
      "[]" // Rows
    ));

    panel.add(new JLabel(localisedMessage), "grow,push");
    panel.add(AwesomeDecorator.createIconButton(
      AwesomeIcon.TIMES,
      "",
      true,
      new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          close();
        }
      }

    ),
      "shrink,right");
  }

  private void close() {
    panel.setVisible(false);
  }

  public JPanel getContentPanel() {
    return panel;
  }
}
