package org.multibit.hd.brit.services;

import com.google.bitcoin.core.*;
import com.google.bitcoin.params.MainNetParams;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.dto.*;
import org.multibit.hd.brit.exceptions.MatcherResponseException;
import org.multibit.hd.brit.exceptions.PayerRequestException;
import org.multibit.hd.brit.extensions.MatcherResponseWalletExtension;
import org.multibit.hd.brit.extensions.SendFeeDtoWalletExtension;
import org.multibit.hd.brit.payer.Payer;
import org.multibit.hd.brit.payer.PayerConfig;
import org.multibit.hd.brit.payer.Payers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.*;

/**
 * <p>Service to provide the following to Payers:</p>
 * <ul>
 * <li>perform a lookup to the BRIT server to get the list of Bitcoin addresses fees need to be paid to</li>
 * <li>provide the details of the next fee to be paid by the Payer</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class FeeService {

  private static final Logger log = LoggerFactory.getLogger(FeeService.class);

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
  public final static BigInteger FEE_PER_SEND = BigInteger.valueOf(1000);    // In satoshi

  /**
   * The lower limit of the gap from one fee send to the next
   */
  public final static int NEXT_SEND_DELTA_LOWER_LIMIT = 10;

  /**
   * The upper limit of the gap from one fee send to the next
   */
  public final static int NEXT_SEND_DELTA_UPPER_LIMIT = 15;

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

  }

  /**
   * Perform a BRIT exchange with the Matcher to work out what addresses the Payer should pay to.
   *
   * @param seed   the seed of the Wallet (from which the britWalletId is worked out)
   * @param wallet the wallet to perform the BRIT exchange against
   */
  public void performExchangeWithMatcher(byte[] seed, Wallet wallet) {

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

    MatcherResponse matcherResponse;
    try {
      // Encrypt the PayerRequest with the Matcher PGP public key.
      EncryptedPayerRequest encryptedPayerRequest = payer.encryptPayerRequest(payerRequest);

      // Do the HTTP(S) post which, if successful, returns an EncryptedMatcherResponse as a byte array
      EncryptedMatcherResponse encryptedMatcherResponse = new EncryptedMatcherResponse(doPost(matcherURL, encryptedPayerRequest.getPayload()));

      // Decrypt the MatcherResponse - the payer does this as it knows how it was AES encrypted (by construction)
      matcherResponse = payer.decryptMatcherResponse(encryptedMatcherResponse);
    } catch (IOException | PayerRequestException | MatcherResponseException e) {
      // The exchange with the matcher failed
      log.debug("The exchange with the matcher failed. The error was {}", e.getClass().getCanonicalName() + e.getMessage());

      // Fall back to the list of hardwired addresses
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
   * @param wallet the wallet to calculate the fee state for
   */
  public FeeState calculateFeeState(Wallet wallet) {

    log.debug("Wallet at beginning of calculateFeeState = {}" , wallet.toString(false, true, true, null));

    // Get all the send transactions sent by me, ordered by date
    List<Transaction> sendTransactions = getSentBySelfTransactionList(wallet);
    int currentNumberOfSends = sendTransactions.size();
    log.debug("The wallet send count is {}", currentNumberOfSends);

    // Work out the total amount that should be paid by the Payer for this wallet
    BigInteger grossFeeToBePaid = FEE_PER_SEND.multiply(BigInteger.valueOf(currentNumberOfSends));

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
    BigInteger feePaid = BigInteger.ZERO;

    for (Transaction sendTransaction : sendTransactions) {
      List<TransactionOutput> sendTransactionOutputList = sendTransaction.getOutputs();
      if (sendTransactionOutputList != null) {
        for (TransactionOutput sendTransactionOutput : sendTransactionOutputList) {
          try {
            Address toAddress = sendTransactionOutput.getScriptPubKey().getToAddress(networkParameters);
            if (feeAddressesUniverse.contains(toAddress)) {
              // It pays some fee
              feePaid = feePaid.add(sendTransactionOutput.getValue());
              lastFeePayingSendAddressOptional = Optional.of(toAddress.toString());
              lastFeePayingSendingCountOptional = Optional.of(lastFeePayingSendCount);
            }
          } catch (ScriptException se) {
            log.debug("Cannot cast script to Address for transaction: {}", sendTransaction.getHash().toString());
          }
        }
      }
      lastFeePayingSendCount++;
    }

    // The net amount fee still to be paid is the gross amount minus the amount paid so far
    // This could be negative if the user has overpaid
    BigInteger netFeeToBePaid = grossFeeToBePaid.subtract(feePaid);


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
      log.debug("Reusing the next address to send fee to. It will be is {}", nextSendFeeAddress);

    } else {

      // Work out the count of the sends at which the next payment will be made
      int numberOfSendCountsPaidFor = feePaid.divide(FEE_PER_SEND).intValue();
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

    // If the user has overpaid then they have amountOverpaid/ FEE_PER_SEND free sends so adjust the nextFeeSendCount accordingly
    if (netFeeToBePaid.compareTo(BigInteger.ZERO) < 0) {
      int numberOfFreeSends = netFeeToBePaid.negate().divide(FEE_PER_SEND).intValue();
      // if the nextSendFeeCount is less than the numberOfFreeSendCount + NEXT_SEND_DELTA_LOWER_LIMIT then push out the nextSendFeeCount a little
      if ((nextSendFeeCount - currentNumberOfSends) < (numberOfFreeSends + NEXT_SEND_DELTA_LOWER_LIMIT)) {
        nextSendFeeCount = currentNumberOfSends + numberOfFreeSends + NEXT_SEND_DELTA_LOWER_LIMIT;
        log.debug("The user has overpaid and has {} free sends. Pushing out nextSendFeeCount to {}", numberOfFreeSends, nextSendFeeCount);
        // Persist back to wallet
        wallet.addOrUpdateExtension(new SendFeeDtoWalletExtension(new SendFeeDto(Optional.of(nextSendFeeCount), Optional.of(nextSendFeeAddress))));
      }
    }

    log.debug("Wallet at end of calculateFeeState = " + wallet.toString(false, false, true, null));

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
   * Get all the send transactions in the wallet that are sent by self
   * (Sends that originate from another copy of this HD have no client fee attached)
   *
   * @param wallet the wallet to look for sends for
   *
   * @return List of the transactions in this wallet sent by self
   */
  private List<Transaction> getSentBySelfTransactionList(Wallet wallet) {

    // Get all the wallets transactions and sort by date
    ArrayList<Transaction> transactions = new ArrayList<>(wallet.getTransactions(false));
    Collections.sort(transactions, new Comparator<Transaction>() {
      public int compare(Transaction t1, Transaction t2) {
        return t1.getUpdateTime().compareTo(t2.getUpdateTime());
      }
    });

    // Iterate over all transactions sorted by date, looking for transaction outputs that are sends
    List<Transaction> sendTransactions = Lists.newArrayList();

    for (Transaction transaction : transactions) {
      if (transaction.getValueSentFromMe(wallet).compareTo(BigInteger.ZERO) > 0) {
        if (transaction.getConfidence() != null && TransactionConfidence.Source.SELF.equals(transaction.getConfidence().getSource())) {
          // This transaction sends from self
          sendTransactions.add(transaction);
        }
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
   * Perform a post of the specified bytes to the specified URL
   *
   * @param url     The URL to post to
   * @param payload the bytes to post
   */
  private byte[] doPost(URL url, byte[] payload) throws IOException {

    URLConnection urlConn;
    DataOutputStream postOutputStream;
    DataInputStream responseInputStream;
    ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream(1024);

    // URL connection channel.
    urlConn = url.openConnection();

    urlConn.setRequestProperty("Content-Length", String.valueOf(payload.length));
    // Let the server know that we want input.
    urlConn.setDoInput(true);
    // Let the server know that we want to do output.
    urlConn.setDoOutput(true);
    // No caching, we want the real thing.
    urlConn.setUseCaches(false);
    // Specify the content type.
    urlConn.setRequestProperty
      ("Content-Type", "application/octet-stream");
    // Send POST output.
    postOutputStream = new DataOutputStream(urlConn.getOutputStream());
    postOutputStream.write(payload);
    postOutputStream.flush();
    postOutputStream.close();
    // Get response data
    responseInputStream = new DataInputStream(urlConn.getInputStream());

    byte readByte;

    boolean keepGoing = true;
    while (keepGoing) {
      try {
        readByte = responseInputStream.readByte();
        responseOutputStream.write(readByte);
      } catch (IOException ioe) {
        // response is all read (EOFException) or has fallen over
        keepGoing = false;
      }
    }

    responseInputStream.close();

    return responseOutputStream.toByteArray();
  }

  /**
   * Calculate the date of the first transaction in the Wallet
   *
   * @param wallet The wallet to inspect the transactions of
   *
   * @return Either the date of the first transaction in the wallet, or Optional.absent() if there are no transactions
   */
  private Optional<Date> calculateFirstTransactionDate(Wallet wallet) {

    ArrayList<Transaction> transactions = new ArrayList<>(wallet.getTransactions(false));
    if (transactions.size() == 0) {
      return Optional.absent();
    }

    // Sort the transactions by date
    Collections.sort(transactions, new Comparator<Transaction>() {
      public int compare(Transaction t1, Transaction t2) {
        return t1.getUpdateTime().compareTo(t2.getUpdateTime());
      }
    });

    return Optional.of(transactions.get(0).getUpdateTime());
  }
}
