package org.multibit.hd.ui.events;

import org.multibit.hd.core.api.RAGStatus;

/**
 * <p>Event to provide the following to views:</p>
 * <ul>
 * <li>Essential information for a system status change</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class SystemStatusChangedEvent {

  private final String localisedMessage;
  private final RAGStatus severity;

  /**
   * @param localisedMessage The localised message describing the system status
   * @param severity         The severity
   */
  public SystemStatusChangedEvent(String localisedMessage, RAGStatus severity) {

    this.localisedMessage = localisedMessage;
    this.severity = severity;

  }

  /**
   * @return The localised message describing the system status
   */
  public String getLocalisedMessage() {
    return localisedMessage;
  }

  /**
   * @return The severity of the system status
   */
  public RAGStatus getSeverity() {
    return severity;
  }
}
