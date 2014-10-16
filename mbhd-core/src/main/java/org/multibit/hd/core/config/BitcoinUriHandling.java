package org.multibit.hd.core.config;

/**
 * <p>Enum to provide the following to application:</p>
 * <ul>
 * <li>Indication of user preference for Bitcoin URI handling</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum BitcoinUriHandling {

  /**
   * Ignore all Bitcoin URI request (no alert or wizard)
   */
  IGNORE,
  /**
   * Present an alert and if acknowledged fill in as much detail as possible
   */
  FILL

  // End of enum
  ;

  /**
   * @return The Bitcoin URI handling strategy
   */
  public static BitcoinUriHandling current() {

    String rawBitcoinUriHandling = Configurations.currentConfiguration.getAppearance().getBitcoinUriHandling();

    return valueOf(rawBitcoinUriHandling);
  }
}
