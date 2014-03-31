package org.multibit.hd.brit.payer;

/**
 *  <p>Factory to provide the following to BRIT classes :</p>
 *  <ul>
 *  <li>Create payers</li>
 *  </ul>
 *  Example:</p>
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
