package org.multibit.hd.brit.services;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.dto.*;
import org.multibit.hd.brit.exceptions.MatcherResponseException;
import org.multibit.hd.brit.exceptions.PayerRequestException;
import org.multibit.hd.brit.extensions.MatcherResponseWalletExtension;
import org.multibit.hd.brit.extensions.SendFeeDtoWalletExtension;
import org.multibit.hd.brit.payer.Payer;
import org.multibit.hd.brit.payer.PayerConfig;
import org.multibit.hd.brit.payer.Payers;
import org.multibit.hd.brit.utils.HttpsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.openpgp.PGPPublicKey;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;

/**
 * <p>Service to provide the following to Payers:</p>
 * <ul>
 * <li>perform a lookup to the BRIT server to get the list of Bitcoin addresses fees need to be paid to</li>
 * <li>provide the details of the next fee to be paid by the Payer</li>
 * <li>the minimum, default and maximum feePerKB to use on spends in satoshi</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class FeeService {

  private static final Logger log = LoggerFactory.getLogger(FeeService.class);

  public static final Coin MINIMUM_FEE_PER_KB = Coin.valueOf(2500);   // Slightly higher than the minimum relay fee (1000 sat per KB)  as per Bitcoin Core 0.9
  public static final Coin DEFAULT_FEE_PER_KB = Coin.valueOf(10000);  // 0.1 mBTC per KB - a long used fee structure which orks as of spam sattacks of July 2015
  public static final Coin MAXIMUM_FEE_PER_KB = Coin.valueOf(50000);  // 0.5 mBTC per KB

  public static final String DONATION_ADDRESS = "1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty";
  public static final String DEFAULT_DONATION_AMOUNT = "0.01"; // in BTC as per BIP21

  /**
   * Always work with MainNet in BRIT (no access to wallet configuration)
   */
  private static final NetworkParameters networkParameters = MainNetParams.get();

  private final PGPPublicKey matcherPublicKey;
  private final URL matcherURL;

  /**
   * BRIT fee charged per send.
   * This is set to be equal to the (expected drop in) miner's fee in 2014Q2 to 0.01 mBTC per KB
   */
  public final static Coin FEE_PER_SEND = Coin.valueOf(1000);    // In satoshi

  /**
   * The lower limit of the gap from one fee send to the next
   */
  public final static int NEXT_SEND_DELTA_LOWER_LIMIT = 15;

  /**
   * The upper limit of the gap from one fee send to the next
   */
  public final static int NEXT_SEND_DELTA_UPPER_LIMIT = 25;

  private TransactionSentBySelfProvider transactionSentBySelfProvider;


  private SecureRandom secureRandom;

  /**
   * Construct a fee service
   *
   * @param matcherPublicKey The PGP public key of the matcher service to perform exchanges with
   * @param matcherURL       the HTTP URL to send PayerRequests to
   */
  public FeeService(PGPPublicKey matcherPublicKey, URL matcherURL) {

    Preconditions.checkNotNull(matcherPublicKey);
    Preconditions.checkNotNull(matcherURL);

    this.matcherPublicKey = matcherPublicKey;
    this.matcherURL = matcherURL;
    this.secureRandom = new SecureRandom();

    // Use a default provider which uses the transaction confidence.
    // This works ok but the user can do a repair wallet and all transaction become not sent by self.
    transactionSentBySelfProvider = new TransactionConfidenceSentBySelfProvider();

    log.debug("Creating FeeService with matcherURL: {}", matcherURL);
  }

  public void setTransactionSentBySelfProvider(TransactionSentBySelfProvider transactionSentBySelfProvider) {
    this.transactionSentBySelfProvider = transactionSentBySelfProvider;
  }

  /**
   * Perform a BRIT exchange with the Matcher to work out what addresses the Payer should pay to.
   *
   * @param seed   the seed of the Wallet (from which the britWalletId is worked out)
   * @param wallet the wallet to perform the BRIT exchange against
   */
  public void performExchangeWithMatcher(byte[] seed, Wallet wallet) {

    log.debug("Performing exchange with matcher ...");

    // Work out the BRITWalletId for this seed
    BRITWalletId britWalletId = new BRITWalletId(seed);

    // Create a random session id
    byte[] sessionId = new byte[AESUtils.BLOCK_LENGTH];
    secureRandom.nextBytes(sessionId);

    // Create a first transaction date
    Optional<Date> firstTransactionDateOptional = calculateFirstTransactionDate(wallet);

    // Create a BRIT Payer
    Payer payer = Payers.newBasicPayer(new PayerConfig(matcherPublicKey));

    // Ask the payer to create an EncryptedPayerRequest containing a BRITWalletId, a session id and a firstTransactionDate
    PayerRequest payerRequest = payer.newPayerRequest(britWalletId, sessionId, firstTransactionDateOptional);

    // Avoid leaking information into the logs
    log.trace("Payer request:\n{}\n", new String(payerRequest.serialise(), Charsets.UTF_8));

    MatcherResponse matcherResponse;
    try {
      // Encrypt the PayerRequest with the Matcher PGP public key.
      EncryptedPayerRequest encryptedPayerRequest = payer.encryptPayerRequest(payerRequest);

      log.debug("Sending encrypted Payer request to Matcher");

      // Do the HTTP(S) POST which, if successful, returns an EncryptedMatcherResponse as a byte array
      byte[] response = HttpsUtils.doPost(matcherURL, encryptedPayerRequest.getPayload(), "application/octet-stream");
      EncryptedMatcherResponse encryptedMatcherResponse = new EncryptedMatcherResponse(response);

      log.debug("Matcher response (encrypted):{} bytes", encryptedMatcherResponse.getPayload().length);

      // Decrypt the MatcherResponse - the payer does this as it knows how it was AES encrypted (by construction)
      matcherResponse = payer.decryptMatcherResponse(encryptedMatcherResponse);

      log.debug("Matcher response decrypted OK");

      // Avoid leaking information into the logs
      log.trace("Matcher response (decrypted):\n{}\n", new String(matcherResponse.serialise(), Charsets.UTF_8));

    } catch (IOException | PayerRequestException | MatcherResponseException e) {
      // The exchange with the matcher failed
      log.warn("The exchange with the Matcher failed. The error was {}", e.getClass().getCanonicalName() + e.getMessage());

      // Fall back to the list of hardwired addresses
      log.warn("Using hardwired addresses");
      matcherResponse = new MatcherResponse(Optional.<Date>absent(), getHardwiredFeeAddresses());
    }

    // Add the MatcherResponse as a wallet extension so that on the next wallet write it will be persisted
    wallet.addOrUpdateExtension(new MatcherResponseWalletExtension(matcherResponse));
  }

  /**
   * Calculate the FeeState for the wallet passed in.
   * This calculates what amount of fee needs paying when.
   * <p/>
   * The caller needs to save the wallet after this call to persist extensions added.
   *
   * @param wallet   The wallet to calculate the fee state for
   * @param forceNow True if the fee should be paid immediately (dust levels permitting)
   */
  public FeeState calculateFeeState(Wallet wallet, boolean forceNow) {

    //log.debug("Wallet at beginning of calculateFeeState = {}", wallet.toString(false, true, true, null));

    // Get all the send transactions sent by me, ordered by date
    List<Transaction> sendTransactions = getSentBySelfTransactionList(wallet);
    int currentNumberOfSends = sendTransactions.size();
    log.debug("The wallet send count is {}", currentNumberOfSends);

    // Work out the total amount that should be paid by the Payer for this wallet
    Coin grossFeeToBePaid = FEE_PER_SEND.multiply(currentNumberOfSends);

    // Get the previous persisted MatcherResponse from the wallet, if available
    MatcherResponse matcherResponseFromWallet = getMatcherResponseFromWallet(wallet);

    // Calculate all the possible fee addresses
    Set<Address> feeAddressesUniverse = Sets.newHashSet(getHardwiredFeeAddresses());
    if (matcherResponseFromWallet != null && matcherResponseFromWallet.getBitcoinAddresses() != null) {
      feeAddressesUniverse.addAll(matcherResponseFromWallet.getBitcoinAddresses());
    }

    // Work out which of the sends actually send money to a fee address.
    // Keep track of the amount sent as fees and the count of the last send to fees made
    int lastFeePayingSendCount = 0;
    Optional<String> lastFeePayingSendAddressOptional = Optional.absent();
    Optional<Integer> lastFeePayingSendingCountOptional = Optional.absent();
    Coin feePaid = Coin.ZERO;

    for (Transaction sendTransaction : sendTransactions) {
      List<TransactionOutput> sendTransactionOutputList = sendTransaction.getOutputs();
      if (sendTransactionOutputList != null) {
        for (TransactionOutput sendTransactionOutput : sendTransactionOutputList) {
          try {
            Script script = sendTransactionOutput.getScriptPubKey();
            // Calculate a TO address if possible
            if (script.isSentToAddress() || script.isPayToScriptHash() || script.isSentToRawPubKey()) {
              Address toAddress = sendTransactionOutput.getScriptPubKey().getToAddress(networkParameters);
              if (feeAddressesUniverse.contains(toAddress)) {
                // It pays some fee
                feePaid = feePaid.add(sendTransactionOutput.getValue());
                lastFeePayingSendAddressOptional = Optional.of(toAddress.toString());
                lastFeePayingSendingCountOptional = Optional.of(lastFeePayingSendCount);
              }
            } else {
              log.debug("Cannot generate a To address (because it is not defined) for  sendTransactionOutput {}", sendTransactionOutput);
            }
          } catch (ScriptException se) {
            log.debug("Cannot cast script to Address for sendTransactionOutput: {}", sendTransactionOutput.getHash().toString());
          }
        }
      }
      lastFeePayingSendCount++;
    }

    // The net amount fee still to be paid is the gross amount minus the amount paid so far
    // This could be negative if the user has overpaid
    Coin netFeeToBePaid = grossFeeToBePaid.subtract(feePaid);

    // nextSendFeeCount and nextSendFeeAddress may already be on the wallet in an extension - if so use those else recalculate
    SendFeeDto sendFeeDto = getSendFeeDtoFromWallet(wallet);
    if (sendFeeDto == null) {
      log.debug("There was no persisted send fee information");
    } else {
      log.debug("The wallet persisted next fee send count is {}", sendFeeDto.getSendFeeCount());
      log.debug("The wallet persisted next fee send address is {}", sendFeeDto.getSendFeeAddress());
    }
    // If the persisted next fee send count is in the future and the last send is NOT a fee payment then reuse the persisted info
    boolean usePersistedData = false;
    if (sendFeeDto != null && sendFeeDto.getSendFeeCount().isPresent()) {
      if ((sendFeeDto.getSendFeeCount().get() >= lastFeePayingSendCount) &&
              !((lastFeePayingSendingCountOptional.isPresent()) && (lastFeePayingSendingCountOptional.get().equals(sendFeeDto.getSendFeeCount().get())))) {
        usePersistedData = true;
      }
    }

    // Work out the next client fee address
    final Address nextSendFeeAddress;
    int nextSendFeeCount;

    if (usePersistedData) {
      nextSendFeeCount = sendFeeDto.getSendFeeCount().get();
      nextSendFeeAddress = sendFeeDto.getSendFeeAddress().get();

      log.debug("Reusing the next send fee transaction. It will be at the send count of {}", nextSendFeeCount);
      log.debug("Reusing the next address to send fee to. It will be {}", nextSendFeeAddress);
    } else {
      // Work out the count of the sends at which the next payment will be made
      // The first nextSendFeeCount is earlier than others by a factor of FIRST_SEND_DELTA_FACTOR
      int numberOfSendCountsPaidFor = (int) feePaid.divide(FEE_PER_SEND);

      nextSendFeeCount = numberOfSendCountsPaidFor +
              +NEXT_SEND_DELTA_LOWER_LIMIT + secureRandom.nextInt(NEXT_SEND_DELTA_UPPER_LIMIT - NEXT_SEND_DELTA_LOWER_LIMIT);

      // If we already have more sends than that then mark the next send as a fee send ie send a fee ASAP
      if (currentNumberOfSends >= nextSendFeeCount) {
        nextSendFeeCount = currentNumberOfSends;
        // Note that the nextSendFeeCount counts from zero so if the currentNumberOfSends is, say 20 a nextSendFeeCount of 20 will be
        // the 21st send i.e. the next one (which is as soon as possible)
      }

      // Work out the next fee send address - it is random
      Set<Address> candidateSendFeeAddresses;
      if (matcherResponseFromWallet == null
              || matcherResponseFromWallet.getBitcoinAddresses() == null
              || matcherResponseFromWallet.getBitcoinAddresses().isEmpty()) {
        candidateSendFeeAddresses = getHardwiredFeeAddresses();
      } else {
        candidateSendFeeAddresses = matcherResponseFromWallet.getBitcoinAddresses();
      }

      // Randomly select a single address from the candidates
      int index = secureRandom.nextInt(candidateSendFeeAddresses.size());
      Address[] randomAccessCandidates = candidateSendFeeAddresses.toArray(new Address[candidateSendFeeAddresses.size()]);
      nextSendFeeAddress = randomAccessCandidates[index];

      log.debug("New next send fee transaction. It will be at the send count of {}", nextSendFeeCount);
      log.debug("New next address to send fee to. It will be is {}", nextSendFeeAddress);

      // Persist back to wallet
      wallet.addOrUpdateExtension(new SendFeeDtoWalletExtension(new SendFeeDto(Optional.of(nextSendFeeCount), Optional.of(nextSendFeeAddress))));
    }

    // If the user has overpaid then they have amountOverpaid / FEE_PER_SEND free sends so adjust the nextFeeSendCount accordingly
    if (netFeeToBePaid.compareTo(Coin.ZERO) < 0) {

      int numberOfFreeSends = (int) netFeeToBePaid.negate().divide(FEE_PER_SEND);

      // if the nextSendFeeCount is less than the numberOfFreeSendCount + NEXT_SEND_DELTA_LOWER_LIMIT then push out the nextSendFeeCount a little
      if ((nextSendFeeCount - currentNumberOfSends) < (numberOfFreeSends + NEXT_SEND_DELTA_LOWER_LIMIT)) {
        nextSendFeeCount = currentNumberOfSends + numberOfFreeSends + NEXT_SEND_DELTA_LOWER_LIMIT;
        log.debug("The user has overpaid and has {} free sends. Pushing out nextSendFeeCount to {}", numberOfFreeSends, nextSendFeeCount);
        // Persist back to wallet
        wallet.addOrUpdateExtension(new SendFeeDtoWalletExtension(new SendFeeDto(Optional.of(nextSendFeeCount), Optional.of(nextSendFeeAddress))));
      }

    } else {
      // User has not incurred a fee or has underpaid - check for a forced payment due to emptying wallet
      if (forceNow) {

        // Indicate that the fee is due now (but they may be let off)
        nextSendFeeCount = currentNumberOfSends;

        if (netFeeToBePaid.compareTo(Transaction.MIN_NONDUST_OUTPUT) > 0) {
          log.debug("Including forced payment. The user has underpaid and owes more than the dust limit.");
        } else {
          log.debug("Excluding forced payment. The user has underpaid and owes less than the dust limit.");
          netFeeToBePaid = Coin.ZERO;
        }
      }
    }

    //log.debug("Wallet at end of calculateFeeState: {}", wallet.toString(false, false, true, null));

    log.debug("The wallet has currentNumberOfSends = {}", currentNumberOfSends);
    log.debug("The wallet owes a GROSS total of {} satoshi in fees", grossFeeToBePaid);
    log.debug("The wallet had paid a total of {} satoshi in fees", feePaid);
    log.debug("The wallet owes a NET total of {} satoshi in fees", netFeeToBePaid);

    if (lastFeePayingSendAddressOptional.isPresent()) {
      log.debug("The last fee address sent any fee was = '{}'. The sendCount then was {}.", lastFeePayingSendAddressOptional.get(), lastFeePayingSendingCountOptional.toString());
    } else {
      log.debug("No transaction in this wallet has paid any fee.");
    }

    return new FeeState(true, nextSendFeeAddress, currentNumberOfSends, nextSendFeeCount, FEE_PER_SEND, netFeeToBePaid);
  }


  /**
   * Normalise the feePerKB so that it is always between the minimum and maximum values
   *
   * @param rawFeePerKB the raw value of feePerKB, as satoshi
   * @return the normalised feePerKB, as Coin
   */
  public static Coin normaliseRawFeePerKB(long rawFeePerKB) {
    if (rawFeePerKB == 0) {
      return DEFAULT_FEE_PER_KB;
    }

    if (Coin.valueOf(rawFeePerKB).compareTo(MINIMUM_FEE_PER_KB) < 0) {
      return MINIMUM_FEE_PER_KB;
    }

    if (Coin.valueOf(rawFeePerKB).compareTo(MAXIMUM_FEE_PER_KB) > 0) {
      return MAXIMUM_FEE_PER_KB;
    }

    // Ok as is
    return Coin.valueOf(rawFeePerKB);
  }

  /**
   * Get all the send transactions in the wallet that are sent by self
   * (Sends that originate from another copy of this HD have no client fee attached)
   *
   * @param wallet the wallet to look for sends for
   * @return List of the transactions in this wallet sent by self
   */
  private List<Transaction> getSentBySelfTransactionList(Wallet wallet) {

    // Get all the wallets transactions and sort by date
    ArrayList<Transaction> transactions = new ArrayList<>(wallet.getTransactions(false));
    Collections.sort(transactions, new TransactionUpdateTimeComparator());

    // Iterate over all transactions sorted by date, looking for transaction outputs that are sends
    List<Transaction> sendTransactions = Lists.newArrayList();

    for (Transaction transaction : transactions) {
      boolean sentBySelf = transactionSentBySelfProvider.isSentBySelf(wallet, transaction);

      if (sentBySelf) {
        // This transaction sends from self - this will exclude unconfirmed tx
        sendTransactions.add(transaction);
      }
    }

    return sendTransactions;
  }

  public static MatcherResponse getMatcherResponseFromWallet(Wallet wallet) {

    Map<String, WalletExtension> walletExtensionsMap = wallet.getExtensions();

    if (walletExtensionsMap != null && walletExtensionsMap.get(MatcherResponseWalletExtension.MATCHER_RESPONSE_WALLET_EXTENSION_ID) != null) {
      return ((MatcherResponseWalletExtension) walletExtensionsMap.get(MatcherResponseWalletExtension.MATCHER_RESPONSE_WALLET_EXTENSION_ID)).getMatcherResponse();
    } else {
      return null;
    }

  }

  public static SendFeeDto getSendFeeDtoFromWallet(Wallet wallet) {

    Map<String, WalletExtension> walletExtensionsMap = wallet.getExtensions();

    if (walletExtensionsMap != null && walletExtensionsMap.get(SendFeeDtoWalletExtension.SEND_FEE_DTO_WALLET_EXTENSION_ID) != null) {
      return ((SendFeeDtoWalletExtension) walletExtensionsMap.get(SendFeeDtoWalletExtension.SEND_FEE_DTO_WALLET_EXTENSION_ID)).getSendFeeDto();
    } else {
      return null;
    }

  }

  /**
   * Get the Set of hardwired fee addresses that will be used if the BRIT Matcher exchange fails
   *
   * @return Set of bitcoin addresses to use as hardwired fee addresses
   */
  public Set<Address> getHardwiredFeeAddresses() {

    // TODO (BS) add in some very well secured addresses owned by Bitcoin Solutions Ltd

    String[] rawAddresses = new String[]{

            "1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty",
            "14Ru32Lb4kdLGfAMz1VAtxh3UFku62HaNH",
            "1KesQEF2yC2FzkJYLLozZJdbBF7zRhrdSC",
            "1CuWW5fDxuFN6CcrRi51ADWHXAMJPYxY5y",
            "1NfNX36S8aocBomvWgySaK9fn93pbpEhmY",
            "1J1nTRJJT3ghsnAEvwd8dMmoTuaAMSLf4V"

    };

    Set<Address> hardwiredFeeAddresses = Sets.newHashSet();
    try {

      for (String rawAddress : rawAddresses) {
        hardwiredFeeAddresses.add(new Address(networkParameters, rawAddress));
      }

    } catch (AddressFormatException e) {
      throw new IllegalArgumentException("Hardwired BRIT address is malformed.", e);
    }

    return hardwiredFeeAddresses;
  }


  /**
   * Calculate the date of the first transaction in the Wallet
   *
   * @param wallet The wallet to inspect the transactions of
   * @return Either the date of the first transaction in the wallet, or Optional.absent() if there are no transactions
   */
  private Optional<Date> calculateFirstTransactionDate(Wallet wallet) {

    ArrayList<Transaction> transactions = new ArrayList<>(wallet.getTransactions(false));
    if (transactions.size() == 0) {
      return Optional.absent();
    }

    // Sort the transactions
    Collections.sort(transactions, new TransactionUpdateTimeComparator());

    return Optional.of(transactions.get(0).getUpdateTime());
  }

  private static class TransactionUpdateTimeComparator implements Comparator<Transaction>, Serializable {

    private static final long serialVersionUID = 1251024601073024138L;

    public int compare(Transaction t1, Transaction t2) {
      return t1.getUpdateTime().compareTo(t2.getUpdateTime());
    }
  }
}
