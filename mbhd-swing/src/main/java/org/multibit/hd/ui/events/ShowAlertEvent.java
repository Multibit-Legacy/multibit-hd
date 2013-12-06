package org.multibit.hd.ui.events;

import java.awt.*;

/**
 * <p>Event to provide the following to views:</p>
 * <ul>
 * <li>Essential information to show an alert panel</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ShowAlertEvent {

  private final String message;
  private final Color background;

  /**
   * @param message    The localised message to display
   * @param background The background colour
   */
  public ShowAlertEvent(String message, Color background) {

    this.message = message;
    this.background = background;

  }

  /**
   * @return The background colour
   */
  public Color getBackground() {
    return background;
  }

  /**
   * @return The localised message
   */
  public String getMessage() {
    return message;
  }
}
