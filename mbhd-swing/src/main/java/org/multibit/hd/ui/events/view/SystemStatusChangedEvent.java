package org.multibit.hd.ui.events.view;

import org.multibit.hd.core.dto.RAGStatus;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates the system status has changed</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class SystemStatusChangedEvent implements ViewEvent {

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

  @Override
  public String toString() {
    return "SystemStatusChangedEvent{" +
            "localisedMessage='" + localisedMessage + '\'' +
            ", severity=" + severity +
            '}';
  }
}
