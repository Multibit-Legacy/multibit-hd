package org.multibit.hd.brit.matcher;

import org.multibit.hd.brit.dto.*;

/**
 *  <p>Interface to provide the following to BRIT:<br>
 *  <ul>
 *  <li>encapsulation of functionality required to match BRIT payers and redeemers</li>
 *  </ul>
 *  </p>
 *  
 */
public interface Matcher {

  public MatcherConfig getConfig();

  public PayerRequest decryptPayerRequest(EncryptedPayerRequest encryptedPayerRequest);

  public MatcherResponse process(PayerRequest payerRequest);

  public EncryptedMatcherResponse encryptMatcherResponse(MatcherResponse matcherResponse);

  public boolean validateAddressGenerator(BRITWalletId britWalletId, byte[] sessionId, AddressGenerator addressGenerator);
}
