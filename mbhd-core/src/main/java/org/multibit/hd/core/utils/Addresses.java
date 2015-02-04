package org.multibit.hd.core.utils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import com.google.common.base.Optional;

/**
 * <p>Utility class to provide the following to Bitcoin Address consumers:</p>
 * <ul>
 * <li>Various useful methods</li>
 * </ul>
 *
 * @since 0.0.4
 *
 */
public class Addresses {

  /**
   * @param rawAddress The raw string representing an address
   *
   * @return The Bitcoin address parsed using the current network parameters (i.e. MainNet)
   */
  public static Optional<Address> parse(String rawAddress) {

    try {
      return Optional.of(new Address(BitcoinNetwork.current().get(), rawAddress));
    } catch (AddressFormatException e) {
      return Optional.absent();
    }

  }
}
