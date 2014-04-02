package org.multibit.hd.brit.services;

import com.google.bitcoin.core.*;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.dto.MatcherResponse;
import org.multibit.hd.brit.dto.SendFeeDto;
import org.multibit.hd.brit.extensions.MatcherResponseWalletExtension;
import org.multibit.hd.brit.extensions.SendFeeDtoWalletExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URL;
import java.util.*;

/**
 *  <p>Service to provide the following to Payers:<br>
 *  <ul>
 * <li>perform a lookup to the BRIT server to get the list of Bitcoin addresses fees need to be paid to</li>
 *  <li>provide the details of the next fee to be paid by the Payer</li>
 *  </p>
 *  
 */
public class FeeService {

  private static final Logger log = LoggerFactory.getLogger(FeeService.class);

  private final PGPPublicKey matcherPublicKey;
  private final URL matcherURL;

  /**
   * BRIT fee charged per send.
   * This is set to be 50% of the (expected drop in) miner's fee in 2014Q2 to 0.01 mBTC per KB
   */
  public final static BigInteger FEE_PER_SEND = BigInteger.valueOf(500);    // In satoshi

  /**
   * The lower limit of the gap from one fee send to the next
   */
  public final static int NEXT_SEND_DELTA_LOWER_LIMIT = 20;

  /**
   * THe upper limit of the gap from one fee send to the next
   */
  public final static int NEXT_SEND_DELTA_UPPER_LIMIT = 30;

  private Random random;

  /**
   * The count at which the next fee will be sent (TODO should be persisted in wallet)
   */
  //private int walletPersistedNextFeeSendCount = -1; // not set yet

  /**
   * The address the next fee will be sent (TODO should be persisted in wallet)
   */
  //private String walletPersistedNextFeeSendAddress;

  /**
   * Construct a fee service
   *
   * @param matcherPublicKey The PGP public key of the matcher service to perform exchanges with
   * @param matcherURL       the HTTP URL to send PayerRequests to
   */
  public FeeService(PGPPublicKey matcherPublicKey, URL matcherURL) {
    this.matcherPublicKey = matcherPublicKey;
    this.matcherURL = matcherURL;
    this.random = new Random();
  }

  /**
   * Perform a BRIT exchange with the Matcher to work out what addresses the Payer should pay to.
   * <p/>
   *
   * @param seed   the seed of the Wallet (from which the britWalletId is worked out)
   * @param wallet the wallet to perform the BRIT exchange against
   */
  public void performExchangeWithMatcher(byte[] seed, Wallet wallet) {
    // TODO Exchange with real Matcher to returns a real MatcherResponse
    MatcherResponse matcherResponse = new MatcherResponse(new Date(), Lists.<String>newArrayList());

    // Add the MatcherResponse as a wallet extension to persist it
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
    // Get all the send transactions, ordered by date
    List<Transaction> sendTransactions = getSendTransactionList(wallet);
    int currentNumberOfSends = sendTransactions.size();

    // Work out the total amount that should be paid by the Payer for this wallet
    BigInteger grossFeeToBePaid = FEE_PER_SEND.multiply(BigInteger.valueOf(currentNumberOfSends));

    // Get the previous persisted MatcherResponse from the wallet, if available
    MatcherResponse matcherResponseFromWallet = getMatcherResponseFromWallet(wallet);

    // Calculate all the possible fee addresses
    Set<String> feeAddressesUniverse = Sets.newHashSet(getHardwiredFeeAddresses());
    if (matcherResponseFromWallet != null && matcherResponseFromWallet.getAddressList() != null) {
      feeAddressesUniverse.addAll(matcherResponseFromWallet.getAddressList());
    }

    // Work out which of the sends actually send money to a fee address.
    // Keep track of the amount sent as fees and the count of the last send to fees made
    int sendCount = 0;
    Optional<String> lastFeePayingSendAddressOptional = Optional.absent();
    Optional<Integer> lastFeePayingSendingCountOptional = Optional.absent();
    BigInteger feePaid = BigInteger.ZERO;

    for (Transaction sendTransaction : sendTransactions) {
      List<TransactionOutput> sendTransactionOutputList = sendTransaction.getOutputs();
      if (sendTransactionOutputList != null) {
        for (TransactionOutput sendTransactionOutput : sendTransactionOutputList) {
          try {
            Address toAddress = sendTransactionOutput.getScriptPubKey().getToAddress(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));
            if (feeAddressesUniverse.contains(toAddress.toString())) {
              // It pays some fee
              feePaid = feePaid.add(sendTransactionOutput.getValue());
              lastFeePayingSendAddressOptional = Optional.of(toAddress.toString());
              lastFeePayingSendingCountOptional = Optional.of(sendCount);
            }
          } catch (ScriptException se) {
            log.debug("Cannot cast script to Address for transaction : " + sendTransaction.getHash().toString());
          }
        }
      }
      sendCount++;
    }
    log.debug("The wallet send count is " + sendCount);

    // The net amount fee still to be paid if the gross amount minus the amount paid so far
    BigInteger netFeeToBePaid = grossFeeToBePaid.subtract(feePaid);

    int nextSendFeeCount;
    String nextSendFeeAddress;

    // nextSendFeeCount and nextSendFeeAddress may already be on the wallet in an extension - if so use those else recalculate
    SendFeeDto sendFeeDto = getSendFeeDtoFromWallet(wallet);
    if (sendFeeDto == null) {
      log.debug("There was no persisted send fee information");
    } else {
      log.debug("The wallet persisted next fee send count is " + sendFeeDto.getSendFeeCount());
      log.debug("The wallet persisted next fee send address is " + sendFeeDto.getSendFeeAddress());
    }
    // If the persisted next fee send count is in the future and the last send is NOT a fee payment then reuse the persisted info
    boolean usePersistedData = false;
    if (sendFeeDto != null && sendFeeDto.getSendFeeCount().isPresent()) {
      if ((sendFeeDto.getSendFeeCount().get() >= sendCount) &&
              !((lastFeePayingSendingCountOptional.isPresent()) && (sendCount - 1 == lastFeePayingSendingCountOptional.get()))) {
        usePersistedData = true;
      }
    }

    if (usePersistedData) {
      nextSendFeeCount = sendFeeDto.getSendFeeCount().get();
      nextSendFeeAddress = sendFeeDto.getSendFeeAddress().get();
      log.debug("Reusing the next send fee transaction. It will be at the send count of " + nextSendFeeCount);
      log.debug("Reusing the next address to send fee to. It will be is " + nextSendFeeAddress);
    } else {
      // Work out the count of the sends at which the next payment will be made
      nextSendFeeCount = (lastFeePayingSendingCountOptional.isPresent() ? lastFeePayingSendingCountOptional.get() : 0) +
              + NEXT_SEND_DELTA_LOWER_LIMIT + random.nextInt(NEXT_SEND_DELTA_UPPER_LIMIT - NEXT_SEND_DELTA_LOWER_LIMIT);
      // If we already have more sends than that then mark the next send as a fee send ie send a fee ASAP
      if (currentNumberOfSends >= nextSendFeeCount) {
        nextSendFeeCount = currentNumberOfSends;
        // Note that the nextSendFeeCount counts from zero so if the currentNumberOfSends is, say 20 a nextSendFeeCount of 20 will be
        // the 21st send i.e. the next one (which is as soon as possible)
      }

      // Work out the next fee send address - it is random but always changes
      List<String> candidateSendFeeAddresses;
      if (matcherResponseFromWallet == null || matcherResponseFromWallet.getAddressList() == null ||
              matcherResponseFromWallet.getAddressList().size() <= 1) {
        candidateSendFeeAddresses = getHardwiredFeeAddresses();
      } else {
        candidateSendFeeAddresses = matcherResponseFromWallet.getAddressList();
      }
      do {
        nextSendFeeAddress = candidateSendFeeAddresses.get(random.nextInt(candidateSendFeeAddresses.size()));
      }
      while (sendFeeDto != null && sendFeeDto.getSendFeeAddress().isPresent() && nextSendFeeAddress.equals(sendFeeDto.getSendFeeAddress().get()));

      log.debug("New next send fee transaction. It will be at the send count of " + nextSendFeeCount);
      log.debug("New next address to send fee to. It will be is " + nextSendFeeAddress);

      // Persist back to wallet
      wallet.addOrUpdateExtension(new SendFeeDtoWalletExtension(new SendFeeDto(Optional.of(nextSendFeeCount), Optional.of(nextSendFeeAddress))));
    }

    log.debug("The wallet has currentNumberOfSends = " + currentNumberOfSends);
    log.debug("The wallet owes a GROSS total of " + grossFeeToBePaid + " satoshi in fees");
    log.debug("The wallet had paid a total of " + feePaid + " satoshi in fees");
    log.debug("The wallet owes a NET total of " + netFeeToBePaid + " satoshi in fees");
    if (lastFeePayingSendAddressOptional.isPresent()) {
      log.debug("The last fee address sent any fee was = '" + lastFeePayingSendAddressOptional.get() + "'. The sendCount then was " + lastFeePayingSendingCountOptional.toString());
    } else {
      log.debug("No transaction in this wallet has paid any fee.");
    }

    return new FeeState(true, nextSendFeeAddress, currentNumberOfSends, nextSendFeeCount, FEE_PER_SEND, netFeeToBePaid);
  }

  private List<Transaction> getSendTransactionList(Wallet wallet) {
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
        // This transaction sends from me
        sendTransactions.add(transaction);
      }
    }
    return sendTransactions;
  }

  /*
   * Calculate the universe of Bitcoin addresses that this wallet could have sent/ be sending fees to
   * This is the union of:
   * + the hardwired addresses
   * + the Bitcoin addresses in the MatcherResponse wallet extension (if available)
   */


  private MatcherResponse getMatcherResponseFromWallet(Wallet wallet) {
    Map<String, WalletExtension> walletExtensionsMap = wallet.getExtensions();

    if (walletExtensionsMap != null && walletExtensionsMap.get(MatcherResponseWalletExtension.MATCHER_RESPONSE_WALLET_EXTENSION_ID) != null) {
      return ((MatcherResponseWalletExtension) walletExtensionsMap.get(MatcherResponseWalletExtension.MATCHER_RESPONSE_WALLET_EXTENSION_ID)).getMatcherResponse();
    } else {
      return null;
    }
  }

  private SendFeeDto getSendFeeDtoFromWallet(Wallet wallet) {
    Map<String, WalletExtension> walletExtensionsMap = wallet.getExtensions();

    if (walletExtensionsMap != null && walletExtensionsMap.get(SendFeeDtoWalletExtension.SEND_FEE_DTO_WALLET_EXTENSION_ID) != null) {
      return ((SendFeeDtoWalletExtension) walletExtensionsMap.get(SendFeeDtoWalletExtension.SEND_FEE_DTO_WALLET_EXTENSION_ID)).getSendFeeDto();
    } else {
      return null;
    }
  }

  /**
   * Get the List of hardwired fee addresses that will be used if the BRIT Matcher exchange fails
   *
   * @return List of bitcoin addresses to use as hardwired fee addresses
   */
  public List<String> getHardwiredFeeAddresses() {
    // Return the multibit.org donation address
    List<String> hardwiredFeeAddresses = Lists.newArrayList();
    // TODO add in some very well secured addresses owned by the MultiBit devs

    // Add in some addresses ffrom the MultiBit donations wallet
    hardwiredFeeAddresses.add("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");
    hardwiredFeeAddresses.add("14Ru32Lb4kdLGfAMz1VAtxh3UFku62HaNH");
    hardwiredFeeAddresses.add("1KesQEF2yC2FzkJYLLozZJdbBF7zRhrdSC");
    hardwiredFeeAddresses.add("1CuWW5fDxuFN6CcrRi51ADWHXAMJPYxY5y");
    hardwiredFeeAddresses.add("1NfNX36S8aocBomvWgySaK9fn93pbpEhmY");
    hardwiredFeeAddresses.add("1J1nTRJJT3ghsnAEvwd8dMmoTuaAMSLf4V");

    return hardwiredFeeAddresses;
  }
}
