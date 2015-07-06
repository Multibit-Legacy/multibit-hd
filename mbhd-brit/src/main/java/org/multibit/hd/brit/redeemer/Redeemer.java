package org.multibit.hd.brit.redeemer;

/**
 * <p>Interface to provide the following to BRIT API:</p>
 * <ul>
 * <li>Encapsulation of functionality required to redeem BRIT payments</li>
 * </ul>
 *
 * @since 0.0.1
 */
public interface Redeemer {

  /**
   * @return The redeemer configuration
   */
  RedeemerConfig getConfig();
}
