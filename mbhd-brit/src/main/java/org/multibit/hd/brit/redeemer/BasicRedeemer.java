package org.multibit.hd.brit.redeemer;

/**
 *  <p>[Pattern] to provide the following to [related classes]:<br>
 *  <ul>
 *  <li></li>
 *  </ul>
 *  Example:<br>
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
