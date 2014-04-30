package org.multibit.hd.core.events;

import org.multibit.hd.core.dto.ExchangeSummary;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of an exchange status change</li>
 * </ul>
 * <p>Normally sent whenever communication fails or succeeds with an exchange</p>
 * <p>Contains a summary indicating RAG status and localised message</p>
 *
 * @since 0.0.1
 *  
 */
public class ExchangeStatusChangedEvent implements CoreEvent {

  final ExchangeSummary summary;

  /**
   * @param summary The exchange summary
   */
  public ExchangeStatusChangedEvent(ExchangeSummary summary) {
    this.summary = summary;
  }

  /**
   * @return The exchange summary
   */
  public ExchangeSummary getSummary() {
    return summary;
  }

  @Override
  public String toString() {
    return "ExchangeStatusChangedEvent{" +
      "summary=" + summary +
      '}';
  }
}
