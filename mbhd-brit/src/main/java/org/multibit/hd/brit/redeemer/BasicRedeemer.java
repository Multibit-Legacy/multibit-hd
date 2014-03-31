package org.multibit.hd.brit.redeemer;

/**
 * <p>Value object to provide the following to BRIT API:</p>
 * <ul>
 * <li>Implementation of a basic Redeemer</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class BasicRedeemer implements Redeemer {

  private RedeemerConfig redeemerConfig;

  public BasicRedeemer(RedeemerConfig redeemerConfig) {
    this.redeemerConfig = redeemerConfig;
  }

  @Override
  public RedeemerConfig getConfig() {
    return redeemerConfig;
  }
}
