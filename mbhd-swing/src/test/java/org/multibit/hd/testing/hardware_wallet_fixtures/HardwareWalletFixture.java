package org.multibit.hd.testing.hardware_wallet_fixtures;

import org.multibit.hd.hardware.core.HardwareWalletClient;

/**
 * <p>Interface to provide the following to FEST requirements:</p>
 * <ul>
 * <li>Standard methods for hardware wallet interactions</li>
 * </ul>
 *
 * @since 0.0.5
 *  
 */
public interface HardwareWalletFixture {

  /**
   * <p>Trigger a low level message on the device</p>
   *
   * <p>Typically this simulates user input or spontaneous activity from the device</p>
   *
   * @param description The log entry describing the expectation (e.g. "Show PIN matrix" etc)
   */
  void fireNextEvent(String description);

  /**
   * @return The (usually mocked) hardware wallet client
   */
  HardwareWalletClient getClient();
}
