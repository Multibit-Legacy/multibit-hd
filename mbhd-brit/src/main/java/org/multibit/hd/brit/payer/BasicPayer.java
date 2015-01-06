package org.multibit.hd.brit.payer;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bouncycastle.openpgp.PGPException;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.dto.*;
import org.multibit.hd.brit.exceptions.MatcherResponseException;
import org.multibit.hd.brit.exceptions.PayerRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
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

  private static final Logger log = LoggerFactory.getLogger(BasicPayer.class);

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
    this.sessionKey = Arrays.copyOf(sessionKey, sessionKey.length);
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
      try (OutputStream tempStream = new FileOutputStream(tempFile)) {
        // Copy the original to the temporary location
        ByteStreams.copy(new ByteArrayInputStream(serialisedPayerRequest), tempStream);
        // Attempt to force the bits to hit the disk. In reality the OS or hard disk itself may still decide
        // to not write through to physical media for at least a few seconds, but this is the best we can do.
        tempStream.flush();
      }

      // PGP encrypt the file
      PGPUtils.encryptFile(encryptedBytesOutputStream, tempFile, payerConfig.getMatcherPublicKey());

      // TODO Secure file delete (or avoid File altogether) - consider recommendations from #295 (MultiBit Common)
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
    } catch (NoSuchAlgorithmException | KeyCrypterException | MatcherResponseException e) {
      throw new MatcherResponseException("Could not decrypt MatcherResponse", e);
    }
  }

}
