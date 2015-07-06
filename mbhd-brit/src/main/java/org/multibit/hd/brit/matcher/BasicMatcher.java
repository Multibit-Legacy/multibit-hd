package org.multibit.hd.brit.matcher;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.bitcoinj.core.Address;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Set;

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
  private static final int NUMBER_OF_ADDRESSES_PER_DAY = 50;

  private SecureRandom secureRandom;

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
    log.trace("Attempting to decrypt payload:\n{}\n", new String(encryptedPayerRequest.getPayload(), Charsets.UTF_8));

    ByteArrayInputStream serialisedPayerRequestEncryptedInputStream = new ByteArrayInputStream(encryptedPayerRequest.getPayload());

    ByteArrayOutputStream serialisedPayerRequestOutputStream = new ByteArrayOutputStream(1024);

    // PGP encrypt the file
    PGPUtils.decryptFile(serialisedPayerRequestEncryptedInputStream, serialisedPayerRequestOutputStream,
            new FileInputStream(matcherConfig.getMatcherSecretKeyringFile()), matcherConfig.getPassword());

    return PayerRequest.parse(serialisedPayerRequestOutputStream.toByteArray());
  }

  @Override
  public MatcherResponse process(PayerRequest payerRequest) {

    WalletToEncounterDateLink previousEncounter = matcherStore.lookupWalletToEncounterDateLink(payerRequest.getBritWalletId());

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
      WalletToEncounterDateLink thisEncounter = new WalletToEncounterDateLink(payerRequest.getBritWalletId(), Optional.of(new Date()), Optional.of(replayDate));
      matcherStore.storeWalletToEncounterDateLink(thisEncounter);
    }
    // Lookup the current valid set of Bitcoin addresses to return to the payer
    Date now = new Date();
    Set<Address> currentBitcoinAddressList = matcherStore.lookupBitcoinAddressListForDate(now);

    if (currentBitcoinAddressList == null || currentBitcoinAddressList.isEmpty()) {

      // No Bitcoin addresses have been set up for this date - create some addresses, store it and return it
      currentBitcoinAddressList = Sets.newHashSet();
      Set<Address> allAddresses = matcherStore.getAllBitcoinAddresses();

      if (allAddresses != null && !allAddresses.isEmpty()) {
        // Create a subset of all addresses for use today
        Address[] randomAccessAllAddresses = allAddresses.toArray(new Address[allAddresses.size()]);
        // Ensure we create a complete subset (no duplications, no missing entries)
        while (currentBitcoinAddressList.size() < NUMBER_OF_ADDRESSES_PER_DAY) {
          // Index should lie between 0 and size() so that array is safe
          int index = secureRandom.nextInt(allAddresses.size());
          currentBitcoinAddressList.add(randomAccessAllAddresses[index]);
        }

      } else {
        log.error("Could not produce a new set of Bitcoin addresses for '{}'. There are no Bitcoin addresses to pick from. Check " +
          "'var/matcher/store/all.txt' is not missing/empty.",now.toString());
      }

      // On a Matcher level lock, double check there is no data and write the list for today
      synchronized (lockObject) {
        Set<Address> doubleCheckCurrentBitcoinAddressList = matcherStore.lookupBitcoinAddressListForDate(now);
        if (doubleCheckCurrentBitcoinAddressList == null || doubleCheckCurrentBitcoinAddressList.isEmpty()) {
          // We're certain that new addresses need to be stored
          matcherStore.storeBitcoinAddressesForDate(currentBitcoinAddressList, now);
        }
      }
      currentBitcoinAddressList = matcherStore.lookupBitcoinAddressListForDate(now);

      Preconditions.checkNotNull(currentBitcoinAddressList,"'currentBitcoinAddressList' must be present after storage.");
      Preconditions.checkState(!currentBitcoinAddressList.isEmpty(), "'currentBitcoinAddressList' must not be empty after storage.");

    }
    return new MatcherResponse(Optional.of(replayDate), currentBitcoinAddressList);
  }

  @Override
  public EncryptedMatcherResponse encryptMatcherResponse(MatcherResponse matcherResponse, PayerRequest payerRequest) throws NoSuchAlgorithmException, InvalidKeyException {
    // Stretch the 20 byte britWalletId to 32 bytes (256 bits)
    byte[] stretchedBritWalletId = MessageDigest.getInstance("SHA-256").digest(payerRequest.getBritWalletId().getBytes());

    // Create an AES key from the stretchedBritWalletId and the sessionKey and encrypt the payload
    byte[] encryptedMatcherResponsePayload = AESUtils.encrypt(matcherResponse.serialise(), new KeyParameter(stretchedBritWalletId), payerRequest.getSessionKey());

    // Use the payer session key to create an HMAC of the payload for integrity checking
    SecretKeySpec hmacKeySpec = new SecretKeySpec(payerRequest.getSessionKey(), "HmacSHA256");
    Mac mac = Mac.getInstance("HmacSHA1");
    mac.init(hmacKeySpec);
    byte[] hmacResponsePayload = mac.doFinal(encryptedMatcherResponsePayload);

    return new EncryptedMatcherResponse(encryptedMatcherResponsePayload, hmacResponsePayload);
  }

  @Override
  public MatcherStore getMatcherStore() {
    return matcherStore;
  }
}
