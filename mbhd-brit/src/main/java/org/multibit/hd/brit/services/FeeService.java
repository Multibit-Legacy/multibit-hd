package org.multibit.hd.brit.services;

import com.google.bitcoin.core.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.dto.MatcherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URL;
import java.util.*;

/**
 *  <p>Service to provide the following to Payers:<br>
 *  <ul>
 *  <li>perform a lookup to the BRIT server to get the list of Bitcoin addresses fees need to be paid to</li>
 *  <li>provide the details of the next fee to be paid by the Payer</li>
 *  </p>
 *  
 */
public class FeeService {

  private static final Logger log = LoggerFactory.getLogger(FeeService.class);

  private final PGPPublicKey matcherPublicKey;
  private final URL matcherURL;

  public final static BigInteger FEE_PER_SEND = BigInteger.valueOf(500);    // In satoshi
  public final static int NEXT_SEND_DELTA_LOWER_LIMIT = 20;
  public final static int NEXT_SEND_DELTA_UPPER_LIMIT = 30;

  private MatcherResponse matcherResponse = null;

  private Random random;


  /**
   * Construct a fee service
   * @param matcherPublicKey The PGP public key of the matcher service to perform exchanges with
   * @param matcherURL the HTTP URL to send PayerRequests to
   */
  public FeeService(PGPPublicKey matcherPublicKey, URL matcherURL) {
    this.matcherPublicKey = matcherPublicKey;
    this.matcherURL = matcherURL;
    this.random = new Random();
  }

  /**
   * Perform a BRIT exchange with the Matcher to work out what addresses the Payer should pay to.
   *
   * The caller needs to save the wallet after this call to persist extensions added.
   *
   * @param seed the seed of the Wallet (from which the britWalletId is worked out)
   * @param wallet the wallet to perform the BRIT exchange against
   */
  public void performExchangeWithMatcher(byte[] seed, Wallet wallet) {
    // TODO Exchange with Matcher returns a MatcherResponse
    matcherResponse = new MatcherResponse(new Date(), Lists.<String>newArrayList());

    // Add the replay date and the Bitcoin addresses to the wallet as extensions

    // The wallet now needs saving but that is out-of-scope for BRIT as it does not
    // know how to persist wallets
  }

  /**
   * Calculate the FeeState for the wallet passed in.
   * This calculates what amount of fee needs paying when.
   *
   * The caller needs to save the wallet after this call to persist extensions added.
   *
   * @param wallet the wallet to calculate the fee state for
   */
  public FeeState calculateFeeState(Wallet wallet) {
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

    int currentNumberOfSends = sendTransactions.size();

    // Work out the total amount that should be paid by the Payer for this wallet
    BigInteger grossFeeToBePaid = FEE_PER_SEND.multiply(BigInteger.valueOf(currentNumberOfSends));

    // Calculate the universe of Bitcoin addresses that this wallet could have sent/ be sending fees to
    // This is the union of:
    // + the hardwired addresses
    // + the Bitcoin addresses in the wallet extension
    Set<String> feeAddressesUniverse = Sets.newHashSet(getHardwiredFeeAddresses());
    if (matcherResponse != null) {
      feeAddressesUniverse.addAll(matcherResponse.getAddressList());
    }

    // Work out which of the sends send money to a fee address.
    // Keep track of the amount sent as fees and the count of the last send to fees made
    int sendCount = 0;
    Transaction lastFeePayingSendTransaction = null;
    int lastFeePayingSendingCount = 0;
    BigInteger feePaid = BigInteger.ZERO;

    for (Transaction sendTransaction : transactions) {
      List<TransactionOutput> sendTransactionOutputList = sendTransaction.getOutputs();
      if (sendTransactionOutputList != null) {
        for (TransactionOutput sendTransactionOutput : sendTransactionOutputList) {
          try {
            Address toAddress = sendTransactionOutput.getScriptPubKey().getToAddress(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));
            if (feeAddressesUniverse.contains(toAddress.toString())) {
              // It pays some fee
              feePaid = feePaid.add(sendTransactionOutput.getValue());
              lastFeePayingSendTransaction = sendTransaction;
              lastFeePayingSendingCount = sendCount;
            }
          } catch (ScriptException se) {
            log.debug("Cannot cast script to Address for transaction : " + sendTransaction.getHash().toString());
          }
        }
      }
      sendCount++;
    }

    BigInteger netFeeToBePaid = grossFeeToBePaid.subtract(feePaid);

    // TODO nextFeeSendCount and nextFeeSendAddress may already be on the wallet in an extension - if so use those else recalculate

    // Work out the count of the sends at which the next payment will be made
    int nextFeeSendCount = lastFeePayingSendingCount + NEXT_SEND_DELTA_LOWER_LIMIT + random.nextInt(NEXT_SEND_DELTA_UPPER_LIMIT - NEXT_SEND_DELTA_LOWER_LIMIT);
    // If we already have more sends than that then mark the next send as a fee send
    if (currentNumberOfSends >= nextFeeSendCount) {
      nextFeeSendCount = currentNumberOfSends;
      // Note that the candidateNextFeeSend counts from zero so if the currentNumberOfSends is, say 20 a candidateNextFeeSend of 20 will be
      // the 21st send i.e. the next one (which is as soon as possible)
    }

    // Work out the next fee send address
    List<String> candidateSendFeeAddresses;
    if (matcherResponse == null) {
      candidateSendFeeAddresses = getHardwiredFeeAddresses();
    } else {
      candidateSendFeeAddresses = matcherResponse.getAddressList();
    }
    String nextSendFeeAddress = candidateSendFeeAddresses.get(random.nextInt(candidateSendFeeAddresses.size()));

    log.debug("The wallet has currentNumberOfSends = " + currentNumberOfSends);
    log.debug("The wallet owes a GROSS total of " + grossFeeToBePaid + " satoshi in fees");
    log.debug("The wallet had paid a total of " + feePaid + " satoshi in fees");
    log.debug("The wallet owes a NET total of " + netFeeToBePaid + " satoshi in fees");
    if (lastFeePayingSendTransaction == null) {
      log.debug("No transaction in this wallet has paid any fee.");
    } else {
      log.debug("The last send transaction that paid any fee was tx = '" + lastFeePayingSendTransaction.getHash().toString() + "'. The sendCount = " + lastFeePayingSendingCount);
    }
    log.debug("The next send fee transaction will be at the send count of " + nextFeeSendCount);
    log.debug("The next send fee address to send to is " + nextSendFeeAddress);

    // TODO nextFeeSendCount and nextFeeSendAddress need persisting to the wallet as extensions

    return new FeeState(true, nextSendFeeAddress, currentNumberOfSends, nextFeeSendCount, FEE_PER_SEND, netFeeToBePaid);
  }

  /**
   * Get the List of hardwired fee addresses that will be used if the BRIT Matcher exchange fails
   * @return List of bitcoin addresses to use as hardwired fee addresses
   */
  public List<String> getHardwiredFeeAddresses() {
    // Return the multibit.org donation address
    List<String> hardwiredFeeAddresses = Lists.newArrayList();
    hardwiredFeeAddresses.add("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    return hardwiredFeeAddresses;
  }
}
