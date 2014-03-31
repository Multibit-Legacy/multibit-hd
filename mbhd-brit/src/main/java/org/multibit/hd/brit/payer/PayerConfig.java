package org.multibit.hd.brit.payer;

import org.bouncycastle.openpgp.PGPPublicKey;

/**
 *  <p>Configuration to provide the following to Payers:</p>
 *  <ul>
 *  <li></li>
 *  </ul>
 *  </p>
 *  
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

  public PGPPublicKey getMatcherPublicKey() {
    return matcherPublicKey;
  }
}
