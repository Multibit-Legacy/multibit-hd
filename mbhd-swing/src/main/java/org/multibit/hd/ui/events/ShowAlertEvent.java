package org.multibit.hd.ui.events;

import org.multibit.hd.core.api.RAGStatus;

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
  private final RAGStatus severity;

  /**
   * @param message  The localised message to display
   * @param severity The severity
   */
  public ShowAlertEvent(String message, RAGStatus severity) {

    this.message = message;
    this.severity = severity;

  }

  /**
   * @return The localised message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @return A severity indicator
   */
  public RAGStatus getSeverity() {
    return severity;
  }
}
