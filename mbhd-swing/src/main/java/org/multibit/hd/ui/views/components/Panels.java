package org.multibit.hd.ui.views.components;

import javax.swing.*;

/**
 * <p>Factory to provide the following to views:</p>
 * <ul>
 * <li>Creation of panels</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Panels {

  public static JFrame frame;

  /**
   * @param panel The component panel to act as the focus of the light box
   *
   * @return A light box panel wrapping the original
   */
  public static LightBoxPanel applyLightBoxPanel(JPanel panel) {

    return new LightBoxPanel(panel);

  }

  /**
   * @param message The message to display
   *
   * @return A light box panel wrapping the original
   */
  public static JPanel newAlertPanel(String message) {

    //return new AlertPanel("Alert!");
    return null;

  }
}
