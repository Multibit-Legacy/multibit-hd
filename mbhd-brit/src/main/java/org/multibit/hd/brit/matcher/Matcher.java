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

  /**
   * Get the configuration for the Matcher.
   * This contains the PGP configuration and location of the matcher store
   * @return MatcherConfig the matcher configuration data
   */
  public MatcherConfig getConfig();

  /**
   * Decrypt a PGP encrypted PayerRequest using the PGP private key in the Matcher's MatcherConfig
   * @param encryptedPayerRequest the encyptedPayerRequest to decrypt
   * @return An unencrypted PayerRequest
   * @throws Exception
   */
  public PayerRequest decryptPayerRequest(EncryptedPayerRequest encryptedPayerRequest) throws Exception;

  /**
   * Process the PayerRequest information and produce a MatcherResponse.
   * This contains the Bitcoin addresses to send payments to and a replay date
   *
   * @param payerRequest The payerRequest from the Payer
   * @return MatcherResponse the response to the request
   */
  public MatcherResponse process(PayerRequest payerRequest);

  /**
   * Encrypt the matcherResponse with an AES key derived from the Payer's BRITWalletId and sessionId
   * @param matcherResponse The unencrypted Matcher Response
   * @return The encrypted MatcherResponse
   * @throws NoSuchAlgorithmException
   */
  public EncryptedMatcherResponse encryptMatcherResponse(MatcherResponse matcherResponse) throws NoSuchAlgorithmException;


  /**
   * Get the MatcherStore used to persist the Matcher information
   */
  public MatcherStore getMatcherStore();

}
