package org.multibit.hd.core.utils;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.MainNetParams;
import org.multibit.hd.core.config.Configurations;

import java.util.Locale;

/**
 * <p>Enum to provide the following to Exchange API:</p>
 * <ul>
 * <li>All supported Bitcoin network parameters</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum BitcoinNetwork {

  /**
   * We consider MainNet to be the only valid way to functionally test a Bitcoin wallet.
   *
   * While TestNet3 and RegTest are useful, they are essentially alt-coins and
   * are not as well supported as MainNet. Also MainNet provides a very wide
   * variety of network failure conditions that are generally unseen on TestNet3
   * or RegTest. As a result functional testing that passes on those networks
   * could fail on MainNet.
   *
   * In short, if you trust your code enough to commit real money to it then you
   * are sure to be testing it properly.
   */
  MAIN_NET,
  /**
   * TestNet3 does not offer a robust testing environment - use at your own risk.
   *
   * You will need to modify the get() switch to include this value.
   * This is done to ensure that only developers are using this network.
   */
  TEST_NET3,
  /**
   * RegTest does not offer a robust testing environment - use at your own risk
   *
   * You will need to modify the get() switch to include this value
   * This is done to ensure that only developers are using this network.
   */
  REG_TEST

  // End of enum
  ;

  /**
   * @return The Bitcoinj NetworkParameters for the given key
   */
  public NetworkParameters get() {

    switch (this) {
      case MAIN_NET:
        return MainNetParams.get();
      default:
        throw new IllegalStateException("Unknown entry: " + this.name());
    }
  }

  /**
   * @param networkParameters A text representation of an enum constant (case-insensitive)
   *
   * @return The matching enum value
   */
  public static BitcoinNetwork of(String networkParameters) {
    return BitcoinNetwork.valueOf(networkParameters.toUpperCase(Locale.ENGLISH));
  }

  /**
   * @return The current Bitcoin network parameters key
   */
  public static BitcoinNetwork current() {

    if (Configurations.currentConfiguration == null) {
      // We should only be in this situation during testing
      return BitcoinNetwork.MAIN_NET;
    }

    return BitcoinNetwork.of(
      Configurations
        .currentConfiguration
        .getBitcoin()
        .getBitcoinNetwork()
    );
  }

}
