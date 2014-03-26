package org.multibit.hd.brit.payer;

/**
 *  <p>Interface to provide the following to BRIT:<br>
 *  <ul>
 *  <li>encapsulation of functionality required to pay BRIT payments</li>
 *  </ul>
 *  </p>
 *  
 */
public interface Payer {

  public PayerConfig getConfig();
}
