package org.multibit.hd.brit.payer;

import com.google.common.base.Optional;
import org.multibit.hd.brit.dto.*;
import org.multibit.hd.brit.exceptions.MatcherResponseException;
import org.multibit.hd.brit.exceptions.PayerRequestException;

import java.util.Date;

/**
 * <p>Interface to provide the following to BRIT:</p>
 * <ul>
 * <li>Encapsulation of functionality required to make BRIT payments</li>
 * </ul>
 *
 * @since 0.0.1
 */
public interface Payer {

  /**
   * Get the PayerConfig, which contains the Matcher's public PGP key
   *
   * @return A Payer configuration
   */
  public PayerConfig getConfig();

  /**
   * Create an unencrypted PayerRequest for transmission to the Matcher
   *
   * @param britWalletId         The britWalletId of the Payer's wallet
   * @param sessionKey           A random sessionKey
   * @param firstTransactionDate The date of the first transaction in the Payer's wallet, or Optional.absent() if there are none.
   *
   * @return A new Payer request (unencrypted)
   */
  public PayerRequest newPayerRequest(BRITWalletId britWalletId, byte[] sessionKey, Optional<Date> firstTransactionDate);

  /**
   * Encrypt the PayerRequest with the Matcher public PGP key
   *
   * @param payerRequest The PayerRequest to encrypt
   *
   * @return the EncryptedPayerRequest containing the encrypted payload
   *
   * @throws org.multibit.hd.brit.exceptions.PayerRequestException
   */
  public EncryptedPayerRequest encryptPayerRequest(PayerRequest payerRequest) throws PayerRequestException;

  /**
   * Decrypt the encryptedMatcherResponse using an AES key derived from the BRITWalletId and sessionKey
   *
   * @param encryptedMatcherResponse The encrypted Matcher response
   *
   * @return A Matcher response (unencrypted)
   *
   * @throws org.multibit.hd.brit.exceptions.MatcherResponseException
   */
  public MatcherResponse decryptMatcherResponse(EncryptedMatcherResponse encryptedMatcherResponse) throws MatcherResponseException;

}
