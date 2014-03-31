package org.multibit.hd.brit.redeemer;

/**
 *  <p>[Pattern] to provide the following to [related classes]:</p>
 *  <ul>
 *  <li></li>
 *  </ul>
 *  Example:</p>
 *  <pre>
 *  </pre>
 *  </p>
 *  
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
