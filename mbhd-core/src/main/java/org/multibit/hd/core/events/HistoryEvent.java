package org.multibit.hd.core.events;

import org.joda.time.DateTime;
import org.multibit.hd.core.utils.Dates;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of a historical event (money transfer, error etc)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class HistoryEvent {

  private final String description;
  private final DateTime created = Dates.nowUtc();

  /**
   * @param description The MultiBit description of what happened
   */
  public HistoryEvent(String description) {
    this.description = description;
  }

  /**
   * @return The immutable MultiBit description of what happened
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return The immutable creation time
   */
  public DateTime getCreated() {
    return created;
  }

  @Override
  public String toString() {
    return "AuditEvent{" +
      "description='" + description + '\'' +
      ", created=" + created +
      '}';
  }
}
