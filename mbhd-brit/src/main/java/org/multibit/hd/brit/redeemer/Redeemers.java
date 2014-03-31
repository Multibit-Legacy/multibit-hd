package org.multibit.hd.brit.redeemer;

/**
 *  <p>Factory to provide the following to BRIT classes :</p>
 *  <ul>
 *  <li>Create redeemers</li>
 *  </ul>
 *  Example:</p>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class Redeemers {

  /**
   * Private constructor for utility classes
   */
  private Redeemers() {
  }

  public static Redeemer newBasicRedeemer(RedeemerConfig redeemerConfig) {
    return new BasicRedeemer(redeemerConfig);
  }
}
