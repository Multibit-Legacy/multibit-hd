package org.multibit.hd.ui.views;

import org.multibit.hd.core.services.CoreServices;

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

    contentPanel = new JPanel();

    JProgressBar progressBar = new JProgressBar();

    contentPanel.add(progressBar, BorderLayout.LINE_END);

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

}
