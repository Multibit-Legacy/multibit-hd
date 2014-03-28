package org.multibit.hd.brit.payer;

import org.bouncycastle.openpgp.PGPException;
import org.multibit.hd.brit.dto.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

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

  public PayerRequest createPayerRequest(BRITWalletId britWalletId, byte[] sessionId, Date firstTransactionDate);

  /**
   * Encrypt the PayerRequest with the Matcher public PGP key
   * @param payerRequest the PayerRequest to encrypt
   * @return the EncryptedPayerRequest containing the encrypted payload
   * @throws NoSuchAlgorithmException
   */
  public EncryptedPayerRequest encryptPayerRequest(PayerRequest payerRequest) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, PGPException;

  public MatcherResponse decryptMatcherReponse(EncryptedMatcherResponse encryptedMatcherResponse) throws NoSuchAlgorithmException;

}
