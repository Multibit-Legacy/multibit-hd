package org.multibit.hd.brit.redeemer;

import com.google.bitcoin.core.ECKey;

/**
 * * <p>Value object to provide the following to BRIT API:</p>
 * <ul>
 * <li>Configuration of Redeemer</li>
 * <li>Storage of EC and PGP private and public keys used in BRIT redemption</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class RedeemerConfig {

  private ECKey redeemerECKey;

  public RedeemerConfig(ECKey redeemerECkey) {
    this.redeemerECKey = redeemerECkey;
  }

  public ECKey getRedeemerECKey() {
    return redeemerECKey;
  }
}
