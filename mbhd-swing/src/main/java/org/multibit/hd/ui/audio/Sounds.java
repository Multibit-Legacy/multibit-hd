package org.multibit.hd.ui.audio;

import java.awt.*;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Provision of standard sounds</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Sounds {

  /**
   * Utilities have no public constructor
   */
  private Sounds() {
  }

  /**
   * Make a standard beep sound (useful for audio feedback of failure)
   */
  public static void beep() {

    Toolkit.getDefaultToolkit().beep();
  }
}
