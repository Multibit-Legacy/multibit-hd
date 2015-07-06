package org.multibit.hd.brit.matcher;

import org.multibit.hd.brit.dto.EncryptedMatcherResponse;
import org.multibit.hd.brit.dto.EncryptedPayerRequest;
import org.multibit.hd.brit.dto.MatcherResponse;
import org.multibit.hd.brit.dto.PayerRequest;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>Interface to provide the following to BRIT:</p>
 * <ul>
 * <li>encapsulation of functionality required to match BRIT payers and redeemers</li>
 * </ul>
 *
 * @since 0.0.1
 */
public interface Matcher {

  /**
   * Get the configuration for the Matcher.
   * This contains the PGP configuration and location of the matcher store
   *
   * @return MatcherConfig the matcher configuration data
   */
  MatcherConfig getConfig();

  /**
   * Decrypt a PGP encrypted PayerRequest using the PGP private key in the Matcher's MatcherConfig
   *
   * @param encryptedPayerRequest the encyptedPayerRequest to decrypt
   *
   * @return An unencrypted PayerRequest
   *
   * @throws Exception
   */
  PayerRequest decryptPayerRequest(EncryptedPayerRequest encryptedPayerRequest) throws Exception;

  /**
   * Process the PayerRequest information and produce a MatcherResponse.
   * This contains the Bitcoin addresses to send payments to and a replay date
   *
   * @param payerRequest The payerRequest from the Payer
   *
   * @return MatcherResponse the response to the request
   */
  MatcherResponse process(PayerRequest payerRequest);

  /**
   * Encrypt the matcherResponse with an AES key derived from the Payer's BRITWalletId and sessionId
   *
   * @param matcherResponse The unencrypted Matcher Response
   * @param payerRequest The payerRequest from the Payer
   *
   * @return The encrypted MatcherResponse
   *
   * @throws NoSuchAlgorithmException
   */
  EncryptedMatcherResponse encryptMatcherResponse(MatcherResponse matcherResponse, PayerRequest payerRequest) throws NoSuchAlgorithmException, InvalidKeyException;


  /**
   * Get the MatcherStore used to persist the Matcher information
   */
  MatcherStore getMatcherStore();

}
