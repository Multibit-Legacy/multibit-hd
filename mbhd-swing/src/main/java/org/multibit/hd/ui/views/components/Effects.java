package org.multibit.hd.ui.views.components;

import javax.swing.*;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Provision of standard sounds</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Effects {

  /**
   * Utilities have no public constructor
   */
  private Effects() {
  }

  /**
   * <p>Seek attention from the user (bouncing icon etc)</p>
   *
   * @param frame The frame
   */
  protected void seekUserAttention(JFrame frame) {
    if (!frame.hasFocus() || JFrame.ICONIFIED == frame.getExtendedState()) {
      frame.setVisible(true);
    }
  }
}
