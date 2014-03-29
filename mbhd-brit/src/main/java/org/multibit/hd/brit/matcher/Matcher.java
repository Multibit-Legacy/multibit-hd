package org.multibit.hd.brit.matcher;

import org.multibit.hd.brit.dto.EncryptedMatcherResponse;
import org.multibit.hd.brit.dto.EncryptedPayerRequest;
import org.multibit.hd.brit.dto.MatcherResponse;
import org.multibit.hd.brit.dto.PayerRequest;

import java.security.NoSuchAlgorithmException;

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

  public PayerRequest decryptPayerRequest(EncryptedPayerRequest encryptedPayerRequest) throws Exception;

  /**
   * Process the PayerRequest information and produce a MatcherResponse.
   * This contains the Bitcoin addresses to send payments to and a replay date
   *
   * @param payerRequest The payerRequest from the Payer
   * @return MatcherResponse the response to the request
   */
  public MatcherResponse process(PayerRequest payerRequest);

  public EncryptedMatcherResponse encryptMatcherResponse(MatcherResponse matcherResponse) throws NoSuchAlgorithmException;
}
