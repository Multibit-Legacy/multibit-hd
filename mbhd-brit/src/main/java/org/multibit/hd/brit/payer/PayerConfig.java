package org.multibit.hd.brit.payer;

import org.bouncycastle.openpgp.PGPPublicKey;

/**
 * <p>Value object to provide the following to BRIT API:</p>
 * <ul>
 * <li>Configuration of Payer</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class PayerConfig {

  /**
   * The PGP public key for the Matcher.
   * This is used to encrypt traffic to the Matcher
   */
  private PGPPublicKey matcherPublicKey;

  public PayerConfig(PGPPublicKey matcherPublicKey) {
    this.matcherPublicKey = matcherPublicKey;
  }

  /**
   * @return The Matcher public key
   */
  public PGPPublicKey getMatcherPublicKey() {
    return matcherPublicKey;
  }
}
