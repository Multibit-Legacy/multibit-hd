package org.multibit.hd.core.events;

import org.multibit.hd.core.dto.HistoryEntry;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of a historical event (money transfer, error etc)</li>
 * </ul>
 * <p>This event occurs after the history service has updated its persistent store</p>
 *
 * <p>This is an infrequent event</p>

 * @since 0.0.1
 *
 */
public class HistoryChangedEvent implements CoreEvent {

  private final HistoryEntry historyEntry;

  /**
   * @param historyEntry The history entry from the original event
   */
  public HistoryChangedEvent(HistoryEntry historyEntry) {
    this.historyEntry = historyEntry;
  }

  public HistoryEntry getHistoryEntry() {
    return historyEntry;
  }

  @Override
  public String toString() {
    return "HistoryChangedEvent{" +
      "historyEntry=" + historyEntry +
      '}';
  }
}
