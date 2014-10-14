package org.multibit.hd.brit.payer;

/**
 * <p>Factory to provide the following to BRIT API:</p>
 * <ul>
 * <li>Create Payer instances</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class Payers {

  /**
   * Private constructor for utility classes
   */
  private Payers() {
  }

  /**
   * @param payerConfig The Payer configuration
   *
   * @return A new basic Payer
   */
  public static Payer newBasicPayer(PayerConfig payerConfig) {
    return new BasicPayer(payerConfig);
  }
}
