package org.multibit.hd.brit.matcher;

import com.google.common.collect.Lists;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.dto.*;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 *  <p>Class to provide the following to BRIT:<br>
 *  <ul>
 *  <li>ability to match redeemers and payers</li>
 *  </ul>

 *  </p>
 *  
 */
public class BasicMatcher implements Matcher {

  private MatcherConfig matcherConfig;

  private byte[] sessionKey;

  private BRITWalletId britWalletId;

  public BasicMatcher(MatcherConfig matcherConfig) {
    this.matcherConfig = matcherConfig;
  }

  @Override
  public MatcherConfig getConfig() {
    return matcherConfig;
  }

  @Override
  public PayerRequest decryptPayerRequest(EncryptedPayerRequest encryptedPayerRequest) throws Exception {

    ByteArrayInputStream serialisedPayerRequestEncryptedInputStream = new ByteArrayInputStream(encryptedPayerRequest.getPayload());

    ByteArrayOutputStream serialisedPayerRequestOutputStream = new ByteArrayOutputStream(1024);

    // PGP encrypt the file
    PGPUtils.decryptFile(serialisedPayerRequestEncryptedInputStream, serialisedPayerRequestOutputStream,
            new FileInputStream(matcherConfig.getMatcherSecretKeyringFile()), matcherConfig.getPassword());


    return PayerRequest.parse(serialisedPayerRequestOutputStream.toByteArray());
  }

  @Override
  public MatcherResponse process(PayerRequest payerRequest) {

    sessionKey = payerRequest.getSessionKey();
    britWalletId = payerRequest.getBRITWalletId();

    // The replay date is the earlier of the payerRequest.firstTreatmentDate and the previousEncounterDate for this
    // BRITWalletId
    Date previousEncounterDate = lookupPreviousEncounterDate(payerRequest.getBRITWalletId());
    Date replayDate = payerRequest.getFirstTransactionDate().before(previousEncounterDate) ? payerRequest.getFirstTransactionDate() : previousEncounterDate;

    // Lookup the current valid set of Bitcoin addresses to return to the payer
    List<String> currentBitcoinAddressList = lookupCurrentBitcoinAddresses();
    return new MatcherResponse(replayDate, currentBitcoinAddressList);
  }

  @Override
  public EncryptedMatcherResponse encryptMatcherResponse(MatcherResponse matcherResponse) throws NoSuchAlgorithmException {
    // Stretch the 20 byte britWalletId to 32 bytes (256 bits)
    byte[] stretchedBritWalletId = MessageDigest.getInstance("SHA-256").digest(britWalletId.getBytes());

    // Create an AES key from the stretchedBritWalletId and the sessionKey and decrypt the payload
    byte[] encryptedMatcherResponsePayload = AESUtils.encrypt(matcherResponse.serialise(), new KeyParameter(stretchedBritWalletId), sessionKey);

    return new EncryptedMatcherResponse(encryptedMatcherResponsePayload);
  }

  private Date lookupPreviousEncounterDate(BRITWalletId britWalletId) {
    // TODO
    return new Date();
  }

  private List<String> lookupCurrentBitcoinAddresses() {
    List<String> currentBitcoinAddresses = Lists.newArrayList();
    // TODO
    currentBitcoinAddresses.add("bebop");
    currentBitcoinAddresses.add("zang");

    return currentBitcoinAddresses;
  }
}
