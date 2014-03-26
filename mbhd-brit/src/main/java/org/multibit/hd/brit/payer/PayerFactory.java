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
public class PayerFactory {

  /**
   * private constructor for utility classes
   */
  private PayerFactory() {
  }

  public static Payer createBasicPayer(PayerConfig payerConfig) {
    return new BasicPayer(payerConfig);
  }
}
