package org.multibit.hd.brit.redeemer;

/**
 * <p>Factory to provide the following to BRIT API:</p>
 * <ul>
 * <li>Create redeemers</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class Redeemers {

  /**
   * Private constructor for utility classes
   */
  private Redeemers() {
  }

  /**
   * @param redeemerConfig The Redeemer configuration
   *
   * @return A new basic Redeemer
   */
  public static Redeemer newBasicRedeemer(RedeemerConfig redeemerConfig) {
    return new BasicRedeemer(redeemerConfig);
  }
}
