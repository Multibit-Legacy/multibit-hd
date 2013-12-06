package org.multibit.hd.ui.views;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the footer display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class FooterView {

  private final JPanel contentPanel;

  public FooterView() {

    CoreServices.uiEventBus.register(this);

    // TODO trim this by 5px on insets
    contentPanel = new JPanel(new MigLayout(
      "",
      "[][][]",
      "[]"
    ));

    JProgressBar progressBar = new JProgressBar();
    JLabel messageLabel = new JLabel("A message");

    JLabel statusLabel = new JLabel("OK");
    JLabel statusIcon = AwesomeDecorator.createIconLabel(
      AwesomeIcon.CIRCLE,
      "",
      false
    );
    statusIcon.setForeground(Color.GREEN);

    contentPanel.add(progressBar, "shrink,left");
    contentPanel.add(messageLabel, "grow,push");
    contentPanel.add(statusLabel, "split,shrink,right");
    contentPanel.add(statusIcon, "right");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

}
