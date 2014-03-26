package org.multibit.hd.brit.payer;

/**
 *  <p>[Pattern] to provide the following to [related classes]:<br>
 *  <ul>
 *  <li>ability to pay BRIT payments</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class BasicPayer implements Payer {

  private PayerConfig payerConfig;

  public BasicPayer(PayerConfig payerConfig) {
    this.payerConfig = payerConfig;
  }

  @Override
  public PayerConfig getConfig() {
    return payerConfig;
  }
}
