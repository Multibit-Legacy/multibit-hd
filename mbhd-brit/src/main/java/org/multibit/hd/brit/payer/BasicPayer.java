package org.multibit.hd.brit.payer;

import com.google.common.base.Optional;
import org.bouncycastle.openpgp.PGPException;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.dto.*;
import org.multibit.hd.brit.utils.FileUtils;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

/**
 *  <p>[Pattern] to provide the following to [related classes]:<br>
 *  <ul>
 *  <li>ability to pay BRIT payments</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class BasicPayer implements Payer {

  private PayerConfig payerConfig;

  public BasicPayer(PayerConfig payerConfig) {
    this.payerConfig = payerConfig;
  }

  private BRITWalletId britWalletId;

  private byte[] sessionKey;

  @Override
  public PayerConfig getConfig() {
    return payerConfig;
  }

  @Override
  public PayerRequest createPayerRequest(BRITWalletId britWalletId, byte[] sessionKey, Optional<Date> firstTransactionDate) {
    this.britWalletId = britWalletId;
    this.sessionKey = sessionKey;
    return new PayerRequest(britWalletId, sessionKey, firstTransactionDate);
  }

  @Override
  public EncryptedPayerRequest encryptPayerRequest(PayerRequest payerRequest) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, PGPException {
    // Serialise the contents of the payerRequest
    byte[] serialisedPayerRequest = payerRequest.serialise();

    ByteArrayOutputStream encryptedBytesOutputStream = new ByteArrayOutputStream(1024);

    // Make a temporary file containing the serialised payment request
    File tempFile = File.createTempFile("req", "tmp");

    // Write serialised payerRequest to the temporary file
    FileUtils.writeFile(new ByteArrayInputStream(serialisedPayerRequest), new FileOutputStream(tempFile));

    // PGP encrypt the file
    PGPUtils.encryptFile(encryptedBytesOutputStream, tempFile, payerConfig.getMatcherPublicKey());

    tempFile.delete();  // TODO secure delete

    return new EncryptedPayerRequest(encryptedBytesOutputStream.toByteArray());
  }

  @Override
  public MatcherResponse decryptMatcherReponse(EncryptedMatcherResponse encryptedMatcherResponse) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    // Stretch the 20 byte britWalletId to 32 bytes (256 bits)
    byte[] stretchedBritWalletId = MessageDigest.getInstance("SHA-256").digest(britWalletId.getBytes());

    // Create an AES key from the stretchedBritWalletId and the sessionKey and decrypt the payload
    byte[] serialisedMatcherResponse = AESUtils.decrypt(encryptedMatcherResponse.getPayload(), new KeyParameter(stretchedBritWalletId), sessionKey);

    // Parse the serialised MatcherResponse
    return MatcherResponse.parse(serialisedMatcherResponse);
  }
}
