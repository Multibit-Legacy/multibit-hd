package org.multibit.hd.brit.payer;

import org.multibit.hd.brit.dto.*;

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

  public PayerRequest createPayerRequest(BRITWalletId britWalletId, byte[] sessionId);

  public EncryptedPayerRequest encryptPayerRequest(PayerRequest payerRequest);

  public MatcherResponse decryptMatcherReponse(EncryptedMatcherResponse encryptedMatcherResponse);

}
