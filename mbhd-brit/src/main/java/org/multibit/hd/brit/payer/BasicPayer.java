package org.multibit.hd.brit.payer;

import com.google.common.base.Optional;
import org.bouncycastle.openpgp.PGPException;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.dto.*;
import org.multibit.hd.brit.exceptions.MatcherResponseException;
import org.multibit.hd.brit.exceptions.PayerRequestException;
import org.multibit.hd.brit.utils.FileUtils;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.util.Date;

/**
 * <p>Payer to provide the following to BRIT:</p>
 * <ul>
 * <li>Implementation of a basic Payer</li>
 * </ul>
 *
 * @since 0.0.1
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
  public PayerRequest newPayerRequest(BRITWalletId britWalletId, byte[] sessionKey, Optional<Date> firstTransactionDate) {

    this.britWalletId = britWalletId;
    this.sessionKey = sessionKey;
    return new PayerRequest(britWalletId, sessionKey, firstTransactionDate);

  }

  @Override
  public EncryptedPayerRequest encryptPayerRequest(PayerRequest payerRequest) throws PayerRequestException {
    try {
      // Serialise the contents of the payerRequest
      byte[] serialisedPayerRequest = payerRequest.serialise();

      ByteArrayOutputStream encryptedBytesOutputStream = new ByteArrayOutputStream(1024);

      // TODO Can we change PGPUtils to accept a stream rather than a file to reduce IO vulnerability?
      // Make a temporary file containing the serialised payer request
      File tempFile = File.createTempFile("req", "tmp");

      // Write serialised payerRequest to the temporary file
      FileUtils.writeFile(new ByteArrayInputStream(serialisedPayerRequest), new FileOutputStream(tempFile));

      // PGP encrypt the file
      PGPUtils.encryptFile(encryptedBytesOutputStream, tempFile, payerConfig.getMatcherPublicKey());

      // TODO Secure file delete (or avoid File altogether)
      if (!tempFile.delete()) {
        throw new IOException("Could not delete file + '" + tempFile.getAbsolutePath() + "'");
      }

      return new EncryptedPayerRequest(encryptedBytesOutputStream.toByteArray());
    } catch (IOException | NoSuchProviderException | PGPException e) {
      throw new PayerRequestException("Could not encrypt PayerRequest", e);
    }
  }

  @Override
  public MatcherResponse decryptMatcherResponse(EncryptedMatcherResponse encryptedMatcherResponse) throws MatcherResponseException {
    try {
      // Stretch the 20 byte britWalletId to 32 bytes (256 bits)
      byte[] stretchedBritWalletId = MessageDigest.getInstance("SHA-256").digest(britWalletId.getBytes());

      // Create an AES key from the stretchedBritWalletId and the sessionKey and decrypt the payload
      byte[] serialisedMatcherResponse = AESUtils.decrypt(encryptedMatcherResponse.getPayload(), new KeyParameter(stretchedBritWalletId), sessionKey);

      // Parse the serialised MatcherResponse
      return MatcherResponse.parse(serialisedMatcherResponse);
    } catch (NoSuchAlgorithmException | ParseException e) {
      throw new MatcherResponseException("Could not decrypt MatcherResponse", e);
    }
  }
}
