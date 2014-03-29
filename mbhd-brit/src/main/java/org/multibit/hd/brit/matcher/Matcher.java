package org.multibit.hd.brit.matcher;

import org.multibit.hd.brit.dto.*;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

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
   * Get the list of Bitcoin addresses for the specified date.
   * The first time this is called for a particular date the Bitcoin addresses will be chosen at random
   * from the universe of Redeemer Bitcoin addresses.
   *
   * Subsequent calls for the same encounter date will return the same list of addresses
   * @param encounterDate
   * @return BitcoinAddresses a list of Strings equal to Bitcoin addresses
   */
  public List<String> getBitcoinAddressList(Date encounterDate);

  /**
   * For a given BRITWalletId, get matching encounter date (the first time this BRITWalletId was seen)
   * Also return a user provided firstTransactionDate if it has been supplied in the past
   */
  public WalletToEncounterDateLink getWalletToEncounterDateLink(BRITWalletId britWalletId);

  /**
   * Store the provided WalletToEncounterDateLink for future use.
   * If a WalletToEncounterDateLink exists for the BRITWalletId, overwrite the previous entry.
   */
  public void storeWalletToEncounterDateLink(WalletToEncounterDateLink walletToEncounterDateLink);
}
