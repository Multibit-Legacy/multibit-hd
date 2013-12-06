package org.multibit.hd.core.events;

import org.multibit.hd.core.api.BitcoinNetworkSummary;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of a Bitcoin network change event</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BitcoinNetworkChangeEvent {

  private final BitcoinNetworkSummary summary;

  // TODO Consider a hint

  public BitcoinNetworkChangeEvent(BitcoinNetworkSummary summary) {
    this.summary = summary;
  }

  /**
   * @return The summary
   */
  public BitcoinNetworkSummary getSummary() {
    return summary;
  }
}
