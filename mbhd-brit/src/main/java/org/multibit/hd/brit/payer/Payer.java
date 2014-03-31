package org.multibit.hd.brit.payer;

import com.google.common.base.Optional;
import org.bouncycastle.openpgp.PGPException;
import org.multibit.hd.brit.dto.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.util.Date;

/**
 *  <p>Interface to provide the following to BRIT:</p>
 *  <ul>
 *  <li>encapsulation of functionality required to pay BRIT payments</li>
 *  </ul>
 *  </p>
 *  
 */
public interface Payer {

  /**
   * Get the PayerConfig, which contains the Matcher's public PGP key
   *
   * @return PayerConfig
   */
  public PayerConfig getConfig();

  /**
   * Create an unencrypted PayerRequest for transmission to the Matcher
   *
   * @param britWalletId         The britWalletId of the Payer's wallet
   * @param sessionKey           A random sessionKey
   * @param firstTransactionDate The date of the first transaction in the Payer's wallet, or Optional.absent() if there are none.
   * @return PayerRequest, unencrypted
   */
  public PayerRequest createPayerRequest(BRITWalletId britWalletId, byte[] sessionKey, Optional<Date> firstTransactionDate);

  /**
   * Encrypt the PayerRequest with the Matcher public PGP key
   *
   * @param payerRequest the PayerRequest to encrypt
   * @return the EncryptedPayerRequest containing the encrypted payload
   * @throws NoSuchAlgorithmException
   */
  public EncryptedPayerRequest encryptPayerRequest(PayerRequest payerRequest) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, PGPException;

  /**
   * Decrypt the encryptedMatcherResponse using an AES key derived from the BRITWalletId and sessionKey
   *
   * @param encryptedMatcherResponse The encryptedMatcherRespnse to decrypt
   * @return MatcherResponse, unencrypted
   * @throws NoSuchAlgorithmException
   * @throws UnsupportedEncodingException
   */
  public MatcherResponse decryptMatcherReponse(EncryptedMatcherResponse encryptedMatcherResponse) throws NoSuchAlgorithmException, UnsupportedEncodingException, ParseException;

}
