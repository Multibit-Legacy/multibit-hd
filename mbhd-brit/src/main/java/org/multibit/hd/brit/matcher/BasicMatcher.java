package org.multibit.hd.brit.matcher;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

/**
 * <p>Class to provide the following to BRIT API:</p>
 * <ul>
 * <li>Ability to match Redeemers and Payers</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class BasicMatcher implements Matcher {

  private static final Logger log = LoggerFactory.getLogger(BasicMatcher.class);

  private final MatcherConfig matcherConfig;

  private static final Object lockObject = new Object();

  /**
   * The number of Bitcoin addresses to send back to the Payer per day
   */
  private static final int NUMBER_OF_ADDRESSES_PER_DAY = 4; // TODO Increase this

  private SecureRandom secureRandom;

  /**
   * The last payerRequest received.
   * (Having a single last payerRequest won't work in a multi-threaded environment)
   */
  private PayerRequest lastPayerRequest;

  /**
   * The matcher store containing all the bitcoin address information
   */
  private final MatcherStore matcherStore;

  /**
   * @param matcherConfig The Matcher configuration
   * @param matcherStore  The Matcher store
   */
  public BasicMatcher(MatcherConfig matcherConfig, MatcherStore matcherStore) {

    this.matcherConfig = matcherConfig;
    this.matcherStore = matcherStore;

    secureRandom = new SecureRandom();
  }

  @Override
  public MatcherConfig getConfig() {
    return matcherConfig;
  }

  @Override
  public PayerRequest decryptPayerRequest(EncryptedPayerRequest encryptedPayerRequest) throws Exception {
    log.debug("Attempting to decrypt payload:\n" + new String(encryptedPayerRequest.getPayload(), "UTF-8"));

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

    WalletToEncounterDateLink previousEncounter = matcherStore.lookupWalletToEncounterDateLink(payerRequest.getBRITWalletId());

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

    // TODO update record if replay date coming in is earlier than the one on the existing record (or if it is absent)

    // If the previousEncounter was null then store this encounter
    if (previousEncounter == null) {
      WalletToEncounterDateLink thisEncounter = new WalletToEncounterDateLink(payerRequest.getBRITWalletId(), Optional.of(new Date()), Optional.of(replayDate));
      matcherStore.storeWalletToEncounterDateLink(thisEncounter);
    }
    // Lookup the current valid set of Bitcoin addresses to return to the payer
    Date now = new Date();
    List<String> currentBitcoinAddressList = matcherStore.lookupBitcoinAddressListForDate(now);

    if (currentBitcoinAddressList == null) {
      // No bitcoin addresses have been set up for this date - create some addresses, store it and return it
      currentBitcoinAddressList = Lists.newArrayList();
      List<String> allAddresses = matcherStore.getAllBitcoinAddress();
      if (allAddresses != null && allAddresses.size() > 0) {
        for (int i = 0; i < NUMBER_OF_ADDRESSES_PER_DAY; i++) {
          currentBitcoinAddressList.add(allAddresses.get(secureRandom.nextInt(allAddresses.size())));
        }
      } else {
        log.error("Could not produce a new set of Bitcoin addresses for " + now.toString() + ". There are no Bitcoin addresses to pick from. Probably your var/matcher/store/all.txt is missing/empty.");
      }

      // On a matcher level lock, double check there is no data and write the list for today
      synchronized (lockObject) {
        if (matcherStore.lookupBitcoinAddressListForDate(now) == null) {
          matcherStore.storeBitcoinAddressListForDate(currentBitcoinAddressList, now);
        }
      }
      currentBitcoinAddressList = matcherStore.lookupBitcoinAddressListForDate(now);
    }
    return new MatcherResponse(Optional.of(replayDate), currentBitcoinAddressList);
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
  public MatcherStore getMatcherStore() {
    return matcherStore;
  }
}
