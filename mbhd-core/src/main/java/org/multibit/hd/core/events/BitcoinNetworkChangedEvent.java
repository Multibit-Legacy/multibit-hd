package org.multibit.hd.core.events;

import org.multibit.hd.core.dto.BitcoinNetworkSummary;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of a change to the Bitcoin network status</li>
 * </ul>
 *
 * <p>This is an infrequent event</p>
 *
 * @since 0.0.1
 *         
 */
public class BitcoinNetworkChangedEvent implements CoreEvent {

  private final BitcoinNetworkSummary summary;

  public BitcoinNetworkChangedEvent(BitcoinNetworkSummary summary) {
    this.summary = summary;
  }

  /**
   * @return The summary
   */
  public BitcoinNetworkSummary getSummary() {
    return summary;
  }
}
