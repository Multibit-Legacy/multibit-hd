package org.multibit.hd.brit.matcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.Map;

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

  /**
   * The last payerRequest received.
   * (Having a single last payerRequest won't work in a multithreaded environment)
   */
  private PayerRequest lastPayerRequest;

  /**
   * A map containing the link from a BRITWalletId to the previous encounter of this wallet (if available)
   */
  private Map<BRITWalletId, WalletToEncounterDateLink> previousEncounterMap;


  public BasicMatcher(MatcherConfig matcherConfig) {
    this.matcherConfig = matcherConfig;
    // TODO populate with previous data from the matcher store (location is now in the MatcherConfig)
    previousEncounterMap = Maps.newHashMap();
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
    lastPayerRequest = payerRequest;

    WalletToEncounterDateLink previousEncounter = getWalletToEncounterDateLink(payerRequest.getBRITWalletId());

    // The replay date is the earliest of:
    // + the payerRequest.firstTreatmentDate in the PayerRequest (if available)
    // + the firstTransactionDate in the previousEncounter (which would have been supplied in the past for this wallet)
    // + the previousEncounterDate (the first time this wallet was seen by the Matcher
    // BRITWalletId
    Date replayDate = new Date();   // If this is a brand new wallet, never used or seen before by the matcher you can replay from now.
    if (previousEncounter != null && previousEncounter.getEncounterDateOptional().isPresent()) {
      replayDate = previousEncounter.getEncounterDateOptional().get();
    }
    if (previousEncounter != null && previousEncounter.getFirstTransactionDate().isPresent()) {
      replayDate = previousEncounter.getFirstTransactionDate().get().before(replayDate) ? previousEncounter.getFirstTransactionDate().get() : replayDate;
    }
    if (payerRequest.getFirstTransactionDate().isPresent()) {
       replayDate = payerRequest.getFirstTransactionDate().get().before(replayDate) ? payerRequest.getFirstTransactionDate().get() : replayDate;
     }

    // Lookup the current valid set of Bitcoin addresses to return to the payer
    List<String> currentBitcoinAddressList = getBitcoinAddressList(new Date());
    return new MatcherResponse(replayDate, currentBitcoinAddressList);
  }

  @Override
  public EncryptedMatcherResponse encryptMatcherResponse(MatcherResponse matcherResponse) throws NoSuchAlgorithmException {
    // Stretch the 20 byte britWalletId to 32 bytes (256 bits)
    byte[] stretchedBritWalletId = MessageDigest.getInstance("SHA-256").digest(lastPayerRequest.getBRITWalletId().getBytes());

    // Create an AES key from the stretchedBritWalletId and the sessionKey and decrypt the payload
    byte[] encryptedMatcherResponsePayload = AESUtils.encrypt(matcherResponse.serialise(), new KeyParameter(stretchedBritWalletId), lastPayerRequest.getSessionKey());

    return new EncryptedMatcherResponse(encryptedMatcherResponsePayload);
  }

  @Override
  public List<String> getBitcoinAddressList(Date encounterDate) {
    List<String> currentBitcoinAddresses = Lists.newArrayList();
    // TODO - interrogate backing store and get the encounterDate's addresses, if present.
    // TODO   Otherwise create a new set of addresses randomly, store and return them
    currentBitcoinAddresses.add("bebop");
    currentBitcoinAddresses.add("zang");

    return currentBitcoinAddresses;
  }

  @Override
  public WalletToEncounterDateLink getWalletToEncounterDateLink(BRITWalletId britWalletId) {
    // See if we have already seen this WalletId before

    // If this is present, return it.
    // If this is null, return a null.
    return previousEncounterMap.get(britWalletId);
  }

  @Override
  public void storeWalletToEncounterDateLink(WalletToEncounterDateLink walletToEncounterDateLink) {
    previousEncounterMap.put(walletToEncounterDateLink.getBritWalletId(), walletToEncounterDateLink);

    // TODO store to backing store
  }
}
