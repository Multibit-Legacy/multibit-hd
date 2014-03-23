package org.multibit.hd.brit.redeemer;

/**
 *  <p>Factory to provide the following to BRIT classes :<br>
 *  <ul>
 *  <li>Create redeemers</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class RedeemerFactory {

  /**
   * private constructor for utility classes
   */
  private RedeemerFactory() {
  }

  public static Redeemer createBasicRedeemer(RedeemerConfig redeemerConfig) {
    return new BasicRedeemer(redeemerConfig);
  }
}
