package org.multibit.hd.ui.views.alerts;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Provision of alert panels</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Alerts {

  /**
   * @return A new Bitcoin network alert
   */
  public static BitcoinNetworkAlert newBitcoinNetworkAlert(String localisedMessage) {

    return new BitcoinNetworkAlert(localisedMessage);

  }

}
