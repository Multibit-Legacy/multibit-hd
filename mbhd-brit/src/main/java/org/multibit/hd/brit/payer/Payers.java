package org.multibit.hd.brit.payer;

/**
 *  <p>Factory to provide the following to BRIT classes :<br>
 *  <ul>
 *  <li>Create payers</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class Payers {

  /**
   * Private constructor for utility classes
   */
  private Payers() {
  }

  public static Payer newBasicPayer(PayerConfig payerConfig) {
    return new BasicPayer(payerConfig);
  }
}
