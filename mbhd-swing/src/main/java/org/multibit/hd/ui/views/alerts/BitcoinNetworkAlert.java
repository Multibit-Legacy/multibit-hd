package org.multibit.hd.ui.views.alerts;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.components.Labels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
      "fill,ins 0,hidemode 3",
      "[grow][]", // Columns
      "[]" // Rows
    ));

    panel.add(new JLabel(localisedMessage), "push");
    panel.add(
      Labels.newPanelCloseLabel(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          close();
        }
      }),
      "shrink,right");
    panel.setBackground(Color.RED);
  }

  private void close() {
    panel.setVisible(false);
  }

  public JPanel getContentPanel() {
    return panel;
  }
}
