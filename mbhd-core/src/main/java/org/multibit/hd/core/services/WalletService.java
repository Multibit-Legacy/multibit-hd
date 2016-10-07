package org.multibit.hd.core.services;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.jcsv.writer.CSVEntryConverter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.script.Script;
import org.joda.time.DateTime;
import org.multibit.commons.concurrent.SafeExecutors;
import org.multibit.commons.crypto.AESUtils;
import org.multibit.commons.files.SecureFiles;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.*;
import org.multibit.hd.core.exceptions.EncryptedFileReaderWriterException;
import org.multibit.hd.core.exceptions.PaymentsLoadException;
import org.multibit.hd.core.exceptions.PaymentsSaveException;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.files.*;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.ExportManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.store.Payments;
import org.multibit.hd.core.store.PaymentsProtobufSerializer;
import org.multibit.hd.core.store.TransactionInfo;
import org.multibit.hd.core.utils.BitcoinNetwork;
import org.multibit.hd.core.utils.Coins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.*;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * <p>Service to provide the following to GUI classes:</p>
 * <ul>
 * <li>list Transactions in the current wallet</li>
 * </ul>
 * <p/>
 * Most of the functionality is provided by WalletManager and BackupManager.
 */
@SuppressFBWarnings({"WMI_WRONG_MAP_ITERATOR"})
public class WalletService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(WalletService.class);

  /**
   * The name of the directory (within the wallet directory) that contains the payments database
   */
  public final static String PAYMENTS_DIRECTORY_NAME = "payments";

  /**
   * The name of the protobuf file containing additional payments information, AES encrypted
   */
  public static final String PAYMENTS_DATABASE_NAME = "payments.aes";

  /**
   * The text separator used in localising To: and By: prefices
   */
  public static final String PREFIX_SEPARATOR = ": ";

  /**
   * The subdirectory in the PAYMENTS_DIRECTORY_NAME directory that contains the raw BIP70 payment requests
   */
  public static final String BIP70_PAYMENT_REQUEST_DIRECTORY = "bip70";

  /**
   * The suffix for the serialised BIP70 payment request
   */
  public static final String BIP70_PAYMENT_REQUEST_SUFFIX = ".paymentrequest.aes";

  /**
   * The suffix for the serialised BIP70 payment
   */
  public static final String BIP70_PAYMENT_SUFFIX = ".payment.aes";

  /**
   * The suffix for the serialised BIP70 payment ACK
   */
  public static final String BIP70_PAYMENT_ACK_SUFFIX = ".paymentack.aes";

  /**
   * The BIP44 gap limit (also used for BIP 32 wallets
   */
  public static final int GAP_LIMIT = 20;

  /**
   * The Bitcoin network parameters
   */
  private final NetworkParameters networkParameters;

  /**
   * The location of the encrypted database file containing BIP70 protobuf files
   */
  private EncryptedPaymentsFile paymentDatabaseFile;

  /**
   * The serializer for the backing store
   */
  private PaymentsProtobufSerializer protobufSerializer;

  /**
   * The MBHD payment requests in a map, indexed by the bitcoin address
   */
  private final Map<Address, MBHDPaymentRequestData> mbhdPaymentRequestDataMap = Collections.synchronizedMap(new HashMap<Address, MBHDPaymentRequestData>());

  /**
   * The additional transaction information, in the form of a map, index by the transaction hash
   */
  private final ConcurrentHashMap<String, TransactionInfo> transactionInfoMap = new ConcurrentHashMap<>();

  /**
   * The payment protocol (BIP70) payment requests (which also includes payments and paymentACKs)
   */
  private final Map<UUID, PaymentRequestData> bip70PaymentRequestDataMap = Collections.synchronizedMap(new HashMap<UUID, PaymentRequestData>());

  /**
   * The wallet id that this WalletService is using
   */
  private WalletId walletId;

  /**
   * The undo stack for undeleting payment requests - this contains both BIP70 and MBHD payment requests
   */
  private final Stack<PaymentData> undoDeletePaymentDataStack = new Stack<>();

  /**
   * The last seen payments data
   */
  private Set<PaymentData> lastSeenPaymentDataSet = Sets.newHashSet();

  /**
   * Handles wallet operations
   */
  private volatile static ExecutorService executorService = null;

  public WalletService(NetworkParameters networkParameters) {

    super();

    Preconditions.checkNotNull(networkParameters, "'networkParameters' must be present");

    this.networkParameters = networkParameters;

  }

  @Override
  protected boolean startInternal() {

    Preconditions.checkNotNull(walletId, "No walletId - have you called initialise() first?");

    return true;
  }

  @Override
  protected boolean shutdownNowInternal(ShutdownEvent.ShutdownType shutdownType) {

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      try {
        if (WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletPassword().getPassword() != null) {
          writePayments(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletPassword().getPassword());
        }
      } catch (PaymentsSaveException pse) {
        // Cannot do much as shutting down
        log.error("Failed to write payments.", pse);
      }
    }

    if (executorService != null) {
      executorService.shutdown();
      executorService = null;
    }

    // Always treat as a hard shutdown
    return true;
  }

  /**
   * Initialise the wallet service with a user data directory and a wallet id so that it knows where to put files etc
   *
   * @param walletId the walletId to use for this WalletService
   */
  public void initialise(File applicationDataDirectory, WalletId walletId, CharSequence password) {
    Preconditions.checkNotNull(applicationDataDirectory, "'applicationDataDirectory' must be present");
    Preconditions.checkNotNull(walletId, "'walletId' must be present");

    this.walletId = walletId;

    // Work out where to write the contacts for this wallet id.
    String walletRoot = WalletManager.createWalletRoot(walletId);

    File walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);

    File paymentsDirectory = new File(walletDirectory.getAbsolutePath() + File.separator + PAYMENTS_DIRECTORY_NAME);
    SecureFiles.verifyOrCreateDirectory(paymentsDirectory);

    this.paymentDatabaseFile = new EncryptedPaymentsFile(paymentsDirectory.getAbsolutePath() + File.separator + PAYMENTS_DATABASE_NAME);

    protobufSerializer = new PaymentsProtobufSerializer();

    if (paymentDatabaseFile.exists()) {
      readPayments(password);
    }
  }

  /**
   * <p>Get all the payments (payments and payment requests) in the current wallet.</p>
   * <h3>NOTE: This is moderately expensive so don't call it indiscriminately</h3>
   */
  public Set<PaymentData> getPaymentDataSet() {

    // See if there is a current wallet
    WalletManager walletManager = WalletManager.INSTANCE;

    Optional<WalletSummary> currentWalletSummary = walletManager.getCurrentWalletSummary();
    if (!currentWalletSummary.isPresent()) {
      // No wallet is present
      return Sets.newHashSet();
    }

    // Wallet is present
    WalletSummary walletSummary = currentWalletSummary.get();
    Wallet wallet = walletSummary.getWallet();

    // There should be a wallet
    Preconditions.checkNotNull(wallet, "There is no wallet to process");

    // Get all the transactions in the wallet
    Set<Transaction> transactions = wallet.getTransactions(true);

    // Adapted transaction data to return
    Set<TransactionData> transactionDataSet = Sets.newHashSet();

    // Work out the unmatched BIP70 payment requests
    Set<PaymentData> unmatchedBip70PaymentDatas = createUnmatchedPaymentRequestDatas();

    // Create a Map of all the unmatched paymentDetails
    Map<UUID, Protos.PaymentDetails> unmatchedPaymentDetailsMap = createUnmatchedPaymentDetails(unmatchedBip70PaymentDatas);

    if (transactions != null) {
      for (Transaction transaction : transactions) {
        // Adapt the transaction - adding on matching MBHDPaymentRequests and BIP70 PaymentRequests
        TransactionData transactionData = adaptTransaction(wallet, transaction, unmatchedPaymentDetailsMap);
        transactionDataSet.add(transactionData);
      }
    }

    // Determine which MBHDPaymentRequests have not been fully funded or request zero funds (these will appear as independent entities in the UI)
    Set<MBHDPaymentRequestData> paymentRequestsNotFullyFunded = Sets.newHashSet();
    for (MBHDPaymentRequestData baseMBHDPaymentRequestData : mbhdPaymentRequestDataMap.values()) {
      boolean requestAmountIsZeroOrAbsent = !baseMBHDPaymentRequestData.getAmountCoin().isPresent() || baseMBHDPaymentRequestData.getAmountCoin().get().compareTo(Coin.ZERO) == 0;
      if ((requestAmountIsZeroOrAbsent && baseMBHDPaymentRequestData.getPaidAmountCoin().compareTo(Coin.ZERO) == 0) ||
              (!requestAmountIsZeroOrAbsent && baseMBHDPaymentRequestData.getPaidAmountCoin().compareTo(baseMBHDPaymentRequestData.getAmountCoin().or(Coin.ZERO)) < 0)) {
        paymentRequestsNotFullyFunded.add(baseMBHDPaymentRequestData);
      }
    }
    // Union the transactionData set and paymentData set
    lastSeenPaymentDataSet = Sets.union(transactionDataSet, paymentRequestsNotFullyFunded);

    lastSeenPaymentDataSet = Sets.union(lastSeenPaymentDataSet, unmatchedBip70PaymentDatas);

    //log.debug("lastSeenPaymentDataSet:\n" + lastSeenPaymentDataSet.toString());
    return lastSeenPaymentDataSet;
  }

  private Set<PaymentData> createUnmatchedPaymentRequestDatas() {
    // Work out the unmatched BIP70 payment requests
    Set<PaymentData> unmatchedBip70PaymentDatas = Sets.newHashSet();
    for (PaymentData paymentData : bip70PaymentRequestDataMap.values()) {
      // If there is a tx hash then the bip70 payment is not a 'top level' object - not shown in payments table
      if (!((PaymentRequestData) paymentData).getTransactionHash().isPresent()) {
        unmatchedBip70PaymentDatas.add(paymentData);
      }
    }
    return unmatchedBip70PaymentDatas;
  }

  private Map<UUID, Protos.PaymentDetails> createUnmatchedPaymentDetails(Set<PaymentData> unmatchedBip70PaymentDatas) {
    // Create a Map of all the unmatched paymentDetails
    Map<UUID, Protos.PaymentDetails> unmatchedPaymentDetailsMap = Maps.newHashMap();
    for (PaymentData unmatchedBip70PaymentData : unmatchedBip70PaymentDatas) {
      Protos.PaymentRequest paymentRequest = ((PaymentRequestData) unmatchedBip70PaymentData).getPaymentRequest().get();
      try {
        Protos.PaymentDetails paymentDetails = Protos.PaymentDetails.parseFrom(paymentRequest.getSerializedPaymentDetails());
        unmatchedPaymentDetailsMap.put(((PaymentRequestData) unmatchedBip70PaymentData).getUuid(), paymentDetails);
      } catch (InvalidProtocolBufferException ipbe) {
        // Do nothing
        ipbe.printStackTrace();
      }
    }
    return unmatchedPaymentDetailsMap;
  }

  public int getPaymentDataSetSize() {
    // IF lastSeenPaymentDataSet is not assigned/ empty then rebuild it to determine payment list size
    if (lastSeenPaymentDataSet == null || lastSeenPaymentDataSet.isEmpty()) {
      // Self-assignment to keep Findbugs happy
      lastSeenPaymentDataSet = getPaymentDataSet();
    }
    return lastSeenPaymentDataSet.size();
  }

  /**
   * Subset the supplied payments and sort by date, descending
   * (Sorting by amount coin is also done to make the order unique, within same date. This is to stop the order 'flicking' on sync)
   *
   * @param subsettingPaymentType if PaymentType.SENDING return all sending payments for the last 24 hours
   *                              if PaymentType.RECEIVING return all requesting and receiving payments for the last 24 hours
   */
  public List<PaymentData> subsetPaymentsAndSort(Set<PaymentData> paymentDataSet, PaymentType subsettingPaymentType) {

    // Subset to the required type of payment
    List<PaymentData> subsetPaymentDataList = Lists.newArrayList();

    if (subsettingPaymentType != null) {
      DateTime aDayAgo = DateTime.now().minusHours(24);

      for (PaymentData paymentData : paymentDataSet) {

        if (subsettingPaymentType == PaymentType.SENDING
                && (paymentData.getType() == PaymentType.THEY_REQUESTED || paymentData.getType() == PaymentType.SENDING)
                && paymentData.getDate().isAfter(aDayAgo)) {

          subsetPaymentDataList.add(paymentData);

        } else if (subsettingPaymentType == PaymentType.RECEIVING) {

          if (paymentData.getType() == PaymentType.YOU_REQUESTED
                  || paymentData.getType() == PaymentType.RECEIVING
                  || paymentData.getType() == PaymentType.PART_PAID) {

            if (paymentData.getDate().isAfter(aDayAgo)) {
              subsetPaymentDataList.add(paymentData);
            }
          }
        }
      }
    }

    Collections.sort(subsetPaymentDataList, new PaymentComparator());

    return subsetPaymentDataList;
  }

  /**
   * @param query The text fragment to match (case-insensitive, anywhere in the name)
   * @return A filtered set of Payments for the given query
   */
  @SuppressFBWarnings({"ITC_INHERITANCE_TYPE_CHECKING"})
  public List<PaymentData> filterPaymentsByContent(String query) {

    String lowerQuery = query.toLowerCase();

    List<PaymentData> filteredPayments = Lists.newArrayList();

    for (PaymentData paymentData : lastSeenPaymentDataSet) {

      boolean isDescriptionMatched = paymentData.getDescription().toLowerCase().contains(lowerQuery);
      boolean isNoteMatched = paymentData.getNote().toLowerCase().contains(lowerQuery);

      boolean isQrCodeLabelMatched = false;
      boolean isPaymentAddressMatched = false;
      boolean isOutputAddressMatched = false;
      boolean isRawTransactionMatched = false;

      if (paymentData instanceof MBHDPaymentRequestData) {

        MBHDPaymentRequestData MBHDPaymentRequestData = (MBHDPaymentRequestData) paymentData;
        isQrCodeLabelMatched = MBHDPaymentRequestData.getLabel().toLowerCase().contains(lowerQuery);

        // Exact match only
        isPaymentAddressMatched = MBHDPaymentRequestData.getAddress().toString().equals(query);

      } else if (paymentData instanceof TransactionData) {

        TransactionData transactionData = (TransactionData) paymentData;
        isOutputAddressMatched = Joiner.on(" ").join(transactionData.getOutputAddresses()).toLowerCase().contains(lowerQuery);
        isRawTransactionMatched = transactionData.getRawTransaction().toLowerCase().contains(lowerQuery);

      }
      if (isDescriptionMatched
              || isNoteMatched
              || isQrCodeLabelMatched
              || isPaymentAddressMatched
              || isOutputAddressMatched
              || isRawTransactionMatched
              ) {
        filteredPayments.add(paymentData);
      }
    }

    Collections.sort(filteredPayments, new PaymentComparator());

    return filteredPayments;
  }

  /**
   * Adapt a bitcoinj transaction to a TransactionData DTO.
   * Also merges in any transactionInfo available.
   * Also checks if this transaction funds any payment requests
   *
   * @param wallet                     the current wallet
   * @param transaction                the transaction to adapt
   * @param unmatchedPaymentDetailsMap Unmatched BIP70 payment details map
   * @return TransactionData the transaction data
   */
  public TransactionData adaptTransaction(Wallet wallet, Transaction transaction, Map<UUID, Protos.PaymentDetails> unmatchedPaymentDetailsMap) {

    // Tx id
    String transactionHashAsString = transaction.getHashAsString();

    // UpdateTime
    Date updateTime = transaction.getUpdateTime();

    // Amount BTC
    Optional<Coin> amountBTC = Optional.of(transaction.getValue(wallet));

    // Fiat amount
    FiatPayment amountFiat = calculateFiatPaymentAndAddTransactionInfo(amountBTC.get(), transactionHashAsString);

    TransactionConfidence confidence = transaction.getConfidence();

    // Depth
    int depth = 0; // By default not in a block
    TransactionConfidence.ConfidenceType confidenceType = TransactionConfidence.ConfidenceType.UNKNOWN;

    PaymentStatus paymentStatus = new PaymentStatus(RAGStatus.AMBER, CoreMessageKey.UNKNOWN);
    if (confidence != null) {
      confidenceType = confidence.getConfidenceType();
      if (TransactionConfidence.ConfidenceType.BUILDING == confidenceType) {
        depth = confidence.getDepthInBlocks();
      }

      // Payment status
      paymentStatus = calculateStatus(confidence.getConfidenceType(), depth, confidence.numBroadcastPeers());
    } else {
      log.debug("No transaction confidence for t {}", transactionHashAsString);
    }


    // Payment type
    PaymentType paymentType = calculatePaymentType(amountBTC.get(), depth, confidenceType);

    // Mining fee
    Optional<Coin> miningFee = calculateMiningFee(paymentType, transactionHashAsString);

    // Description +
    // Ensure that any payment requests that are funded by this transaction know about it
    // (The payment request knows about the transactions that fund it but not the reverse)

    String description = calculateDescriptionAndUpdatePaymentRequests(wallet, transaction, transactionHashAsString, paymentType, amountBTC.get(), unmatchedPaymentDetailsMap);
    // Also works out outputAddresses

    // Include the raw serialized form of the transaction for lowest level viewing
    String rawTransaction = transaction.toString() + "\n" + Utils.HEX.encode(transaction.bitcoinSerialize()) + "\n";

    int size = -1;
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    try {
      transaction.bitcoinSerialize(byteOutputStream);
      size = byteOutputStream.size();
    } catch (IOException e) {
      log.error("Failed to serialize transaction", e);
    }

    List<Address> outputAddresses = calculateOutputAddresses(transaction);

    // Create the DTO from the raw transaction info
    TransactionData transactionData = new TransactionData(
            transactionHashAsString,
            new DateTime(updateTime),
            paymentStatus,
            amountBTC,
            amountFiat,
            miningFee,
            Optional.<Coin>absent(),
            confidenceType,
            paymentType,
            description,
            transaction.isCoinBase(),
            outputAddresses,
            rawTransaction,
            size,
            false
    );

    // Note - from the transactionInfo (if present)
    String note = calculateNote(transactionData, transactionHashAsString);
    transactionData.setNote(note);

    return transactionData;
  }

  /**
   * <p>Calculate the PaymentStatus of the transaction:</p>
   * <ul>
   * <li>RED: tx is dead, double spend, failed to be transmitted to the network etc</li>
   * <li>AMBER: tx is unconfirmed</li>
   * <li>GREEN: tx has one or more confirmations</li>
   * </ul>
   *
   * @param confidenceType the Bitcoinj ConfidenceType  to use to work out the status
   * @param depth          depth in blocks of the transaction (1 is most recent)
   * @return status of the transaction
   */
  public static PaymentStatus calculateStatus(TransactionConfidence.ConfidenceType confidenceType, int depth, int numberOfPeers) {

    if (confidenceType != null) {

      if (TransactionConfidence.ConfidenceType.BUILDING.equals(confidenceType)) {
        // Confirmed
        final PaymentStatus paymentStatus;
        if (depth == 1) {
          paymentStatus = new PaymentStatus(RAGStatus.GREEN, CoreMessageKey.CONFIRMED_BY_ONE_BLOCK);
        } else {
          paymentStatus = new PaymentStatus(RAGStatus.GREEN, CoreMessageKey.CONFIRMED_BY_SEVERAL_BLOCKS);
          paymentStatus.setStatusData(new Object[]{depth});
        }
        paymentStatus.setDepth(depth);
        return paymentStatus;

      } else if (TransactionConfidence.ConfidenceType.PENDING.equals(confidenceType)) {
        if (numberOfPeers >= 2) {
          // Seen by the network but not confirmed yet
          PaymentStatus paymentStatus = new PaymentStatus(RAGStatus.AMBER, CoreMessageKey.BROADCAST);
          paymentStatus.setStatusData(new Object[]{numberOfPeers});
          return paymentStatus;
        } else {
          if (numberOfPeers == 1) {
            // Not definitely out in the network (seen by one peer is probably the peer first broadcast to, which will INV it back
            return new PaymentStatus(RAGStatus.AMBER, CoreMessageKey.NOT_BROADCAST);
          } else {
            // seen by zero peers
            return new PaymentStatus(RAGStatus.RED, CoreMessageKey.NOT_BROADCAST);
          }
        }
      } else if (TransactionConfidence.ConfidenceType.DEAD.equals(confidenceType)) {
        // Dead
        return new PaymentStatus(RAGStatus.RED, CoreMessageKey.DEAD);
      } else if (TransactionConfidence.ConfidenceType.UNKNOWN.equals(confidenceType)) {
        // Unknown
        return new PaymentStatus(RAGStatus.AMBER, CoreMessageKey.UNKNOWN);
      }
    } else {
      // No transaction status - don't know
      return new PaymentStatus(RAGStatus.AMBER, CoreMessageKey.UNKNOWN);
    }

    // Unknown
    return new PaymentStatus(RAGStatus.AMBER, CoreMessageKey.UNKNOWN);

  }

  private PaymentType calculatePaymentType(Coin amountBTC, int depth, TransactionConfidence.ConfidenceType confidenceType) {
    PaymentType paymentType;
    if (amountBTC.compareTo(Coin.ZERO) < 0) {
      // Debit
      if (depth > 0 || TransactionConfidence.ConfidenceType.BUILDING.equals(confidenceType)) {
        paymentType = PaymentType.SENT;
      } else {
        paymentType = PaymentType.SENDING;
      }
    } else {
      // Credit
      if (depth > 0 || TransactionConfidence.ConfidenceType.BUILDING.equals(confidenceType)) {
        paymentType = PaymentType.RECEIVED;
      } else {
        paymentType = PaymentType.RECEIVING;
      }
    }
    return paymentType;
  }

  private String calculateDescriptionAndUpdatePaymentRequests(
          Wallet wallet,
          Transaction transaction,
          String transactionHashAsString,
          PaymentType paymentType,
          Coin amountBTC,
          Map<UUID, Protos.PaymentDetails> unmatchedPaymentDetailsMap
  ) {

    StringBuilder description = new StringBuilder();
    if (paymentType == PaymentType.RECEIVING || paymentType == PaymentType.RECEIVED) {
      String addresses = "";

      boolean descriptiveTextIsAvailable = false;
      if (transaction.getOutputs() != null) {
        for (TransactionOutput transactionOutput : transaction.getOutputs()) {
          if (transactionOutput.isMine(wallet)) {
            // If the output is mine then it is a vanilla isSentToAddress() = true transactionOutput
            Address receivingAddress = transactionOutput.getScriptPubKey().getToAddress(networkParameters);
            addresses = addresses + " " + receivingAddress;

            // Check if this output funds any payment requests;
            MBHDPaymentRequestData MBHDPaymentRequestData = mbhdPaymentRequestDataMap.get(receivingAddress);
            if (MBHDPaymentRequestData != null) {
              // Yes - this output funds a payment address
              if (!MBHDPaymentRequestData.getPayingTransactionHashes().contains(transactionHashAsString)) {
                // We have not yet added this tx to the total paid amount
                MBHDPaymentRequestData.getPayingTransactionHashes().add(transactionHashAsString);
                MBHDPaymentRequestData.setPaidAmountCoin(MBHDPaymentRequestData.getPaidAmountCoin().add(amountBTC));
              }

              if (MBHDPaymentRequestData.getLabel() != null && MBHDPaymentRequestData.getLabel().length() > 0) {
                descriptiveTextIsAvailable = true;
                description
                        .append(MBHDPaymentRequestData.getLabel())
                        .append(" ");
              }
              if (MBHDPaymentRequestData.getNote() != null && MBHDPaymentRequestData.getNote().length() > 0) {
                descriptiveTextIsAvailable = true;
                description
                        .append(MBHDPaymentRequestData.getNote())
                        .append(" ");
              }
            }
          }
        }
      }

      if (!descriptiveTextIsAvailable) {
        // TODO localise
        description
                .append("By")
                .append(PREFIX_SEPARATOR)
                .append(addresses.trim());
      }
    } else {
      // Sent
      // TODO localise
      description
              .append("To")
              .append(PREFIX_SEPARATOR);
      if (transaction.getOutputs() != null) {
        for (TransactionOutput transactionOutput : transaction.getOutputs()) {
          Script script = transactionOutput.getScriptPubKey();
          // Calculate a TO address if possible
          if (script.isSentToAddress() || script.isPayToScriptHash() || script.isSentToRawPubKey()) {
            description
                    .append(" ")
                    .append(transactionOutput.getScriptPubKey().getToAddress(networkParameters));
          } else {
            // Not sure what this output sends to but put something neutral
            description.append(" N/A");
          }
        }
      }

      // Link the transaction to the BIP70 payment request by UUID
      if (transaction.getOutputs() != null) {
        for (TransactionOutput transactionOutput : transaction.getOutputs()) {
          // Iterate through all the Outputs of all the BIP70 payment requests to see if this TransactionOutput pays to that Output
          for (UUID uuid : unmatchedPaymentDetailsMap.keySet()) {
            Protos.PaymentDetails unmatchedPaymentDetail = unmatchedPaymentDetailsMap.get(uuid);
            if (unmatchedPaymentDetail != null) {
              List<Protos.Output> outputs = unmatchedPaymentDetail.getOutputsList();
              for (Protos.Output output : outputs) {
                ByteString scriptBytes = output.getScript();
                Script script = new Script(scriptBytes.toByteArray());
                if (script.isSentToAddress()) {
                  Address bip70ToAddress = script.getToAddress(BitcoinNetwork.current().get());
                  Address transactionOutputToAddress = transactionOutput.getAddressFromP2PKHScript(BitcoinNetwork.current().get());
                  if (transactionOutputToAddress != null && transactionOutputToAddress.equals(bip70ToAddress)) {
                    // If so then we have matched the BIP70 payment request to the transaction
                    // Set the Transaction hash and re-add to WalletService (replacing any pre-existing paymentRequestData with the same UUID)
                    Sha256Hash txHash = transaction.getHash();
                    PaymentRequestData paymentRequestData = bip70PaymentRequestDataMap.get(uuid);
                    if (paymentRequestData != null) {
                      paymentRequestData.setTransactionHash(Optional.of(txHash));
                      addPaymentRequestData(paymentRequestData);
                      log.debug("Linking the BIP70 payment request with UUID {} to the transaction with hash {}", uuid, txHash);

                      // In theory a single tx can pay multiple payment requests but the UI does not permit this so break to save time
                      break;
                    } else {
                      log.debug("Could not find PaymentRequestData with UUID: {}, carrying on", uuid);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return description.toString();
  }

  private List<Address> calculateOutputAddresses(Transaction transaction) {
    List<Address> outputAddresses = Lists.newArrayList();

    if (transaction.getOutputs() != null) {
      for (TransactionOutput transactionOutput : transaction.getOutputs()) {
        try {
          Script script = transactionOutput.getScriptPubKey();
          // Calculate a TO address if possible
          if (script.isSentToAddress() || script.isPayToScriptHash() || script.isSentToRawPubKey()) {
            outputAddresses.add(script.getToAddress(networkParameters));
          } else {
            log.debug("Cannot generate a To address (because it is not defined) for  transactionOutput {}", transactionOutput.toString());
          }
        } catch (ScriptException se) {
          log.debug("Could not get a to address for transactionOutput {}", transactionOutput.toString());
        }
      }
    }

    return outputAddresses;
  }

  public static FiatPayment calculateFiatPaymentEquivalent(Coin amountBTC) {
    FiatPayment amountFiat = new FiatPayment();

    log.trace("Calculating fiat amount of {}", amountBTC);

    // Work it out from the current settings
    amountFiat.setExchangeName(Optional.of(ExchangeKey.current().getExchangeName()));

    if (CoreServices.getApplicationEventService() != null) {
      Optional<ExchangeRateChangedEvent> exchangeRateChangedEvent = CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent();
      if (exchangeRateChangedEvent.isPresent() && exchangeRateChangedEvent.get().getRate() != null) {
        amountFiat.setRate(Optional.of(exchangeRateChangedEvent.get().getRate().toString()));

        if (amountBTC != null) {
          BigDecimal localAmount = Coins.toLocalAmount(amountBTC, exchangeRateChangedEvent.get().getRate());
          if (localAmount.compareTo(BigDecimal.ZERO) != 0) {
            amountFiat.setAmount(Optional.of(localAmount));
          } else {
            amountFiat.setAmount(Optional.<BigDecimal>absent());
          }
        } else {
          amountFiat.setAmount(Optional.<BigDecimal>absent());
        }
        amountFiat.setCurrency(Optional.of(exchangeRateChangedEvent.get().getCurrency()));
      } else {
        amountFiat.setRate(Optional.<String>absent());
        amountFiat.setAmount(Optional.<BigDecimal>absent());
        amountFiat.setCurrency(Optional.<Currency>absent());
      }
    }

    log.trace("Calculated amount was {}", amountFiat);
    return amountFiat;
  }

  private FiatPayment calculateFiatPaymentAndAddTransactionInfo(Coin amountBTC, String transactionHashAsString) {
    // Get the transactionInfo that contains the fiat exchange info, if it is available from the payment database
    // This will use the fiat rate at time of send/ receive
    TransactionInfo transactionInfo = transactionInfoMap.get(transactionHashAsString);
    if (transactionInfo != null) {
      return transactionInfo.getAmountFiat();
    }

    FiatPayment amountFiat = calculateFiatPaymentEquivalent(amountBTC);

    // Remember the fiat information just worked out
    TransactionInfo newTransactionInfo = new TransactionInfo();
    newTransactionInfo.setHash(transactionHashAsString);
    newTransactionInfo.setAmountFiat(amountFiat);

    // Double check we are not overwriting an extant transactionInfo
    if (transactionInfoMap.get(transactionHashAsString) == null) {
      // Expected
      transactionInfoMap.putIfAbsent(transactionHashAsString, newTransactionInfo);
    }

    return amountFiat;
  }

  private String calculateNote(TransactionData transactionData, String transactionHashAsString) {
    String note = "";

    TransactionInfo transactionInfo = transactionInfoMap.get(transactionHashAsString);
    if (transactionInfo != null) {
      note = transactionInfo.getNote();

      if (note != null) {
        transactionData.setNote(note);
        // if there is a real note use that as the description
        if (note.length() > 0) {
          transactionData.setDescription(note);
        }
      } else {
        transactionData.setNote("");
      }

      transactionData.setAmountFiat(transactionInfo.getAmountFiat());

    } else {
      transactionData.setNote("");
    }

    return note;
  }

  private Optional<Coin> calculateMiningFee(PaymentType paymentType, String transactionHashAsString) {

    Optional<Coin> miningFee = Optional.absent();

    if (paymentType == PaymentType.SENDING || paymentType == PaymentType.SENT) {
      TransactionInfo transactionInfo = transactionInfoMap.get(transactionHashAsString);
      if (transactionInfo != null) {
        miningFee = transactionInfo.getMinerFee();
      }
    }

    return miningFee;
  }

  /**
   * <p>Populate the internal cache of Payments from the payment database</p>
   */
  public void readPayments(CharSequence password) throws PaymentsLoadException {
    Preconditions.checkNotNull(paymentDatabaseFile, "'paymentDatabaseFile' must be present. Please initialise WalletService.");

    log.debug("Reading payments from\n'{}'", paymentDatabaseFile.getAbsolutePath());
    mbhdPaymentRequestDataMap.clear();
    transactionInfoMap.clear();
    bip70PaymentRequestDataMap.clear();

    if (paymentDatabaseFile.exists()) {
      ByteArrayInputStream decryptedInputStream = EncryptedFileReaderWriter.readAndDecrypt(
              paymentDatabaseFile,
              password);
      Payments payments = protobufSerializer.readPayments(decryptedInputStream);

      // For quick access payment requests and transaction infos are stored in maps
      Collection<MBHDPaymentRequestData> mbhdPaymentRequestDataCollection = payments.getMBHDPaymentRequestDataCollection();

      if (mbhdPaymentRequestDataCollection != null) {
        for (MBHDPaymentRequestData MBHDPaymentRequestData : mbhdPaymentRequestDataCollection) {
          mbhdPaymentRequestDataMap.put(MBHDPaymentRequestData.getAddress(), MBHDPaymentRequestData);
        }
      }

      Collection<TransactionInfo> transactionInfos = payments.getTransactionInfoCollection();

      if (transactionInfos != null) {
        for (TransactionInfo transactionInfo : transactionInfos) {
          transactionInfoMap.put(transactionInfo.getHash(), transactionInfo);
        }
      }

      Optional<WalletSummary> walletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

      Collection<PaymentRequestData> paymentRequestDataCollection = payments.getPaymentRequestDataCollection();

      if (paymentRequestDataCollection != null) {
        for (PaymentRequestData paymentRequestData : paymentRequestDataCollection) {

          // Clear any tx hash if the tx is not in the wallet
          // (See issue https://github.com/keepkey/multibit-hd/issues/463)
          // This will get persisted at MBHD close or when payments is next written
          Optional<Sha256Hash> transactionHashOptional = paymentRequestData.getTransactionHash();
          if (transactionHashOptional.isPresent() && walletSummaryOptional.isPresent()) {
            Wallet wallet = walletSummaryOptional.get().getWallet();
            if (wallet != null && wallet.getTransaction(transactionHashOptional.get()) == null) {
              // Transaction is not in the wallet - clear it from the paymentRequestData
              paymentRequestData.setTransactionHash(Optional.<Sha256Hash>absent());
            }
          }

          bip70PaymentRequestDataMap.put(paymentRequestData.getUuid(), paymentRequestData);
        }
      }
    }

    Collection<PaymentRequestData> values = bip70PaymentRequestDataMap.values();
    readPaymentRequestsDataFiles(values, paymentDatabaseFile, password);

    log.debug(
            "Reading payments completed\nTransactionInfo count: {}\nMBHD payment request count: {}\nBIP70 payment request count: {}",
            transactionInfoMap.values().size(),
            mbhdPaymentRequestDataMap.values().size(),
            values.size()
    );
  }

  /**
   * <p>Save the payments data to the backing store</p>
   */
  public void writePayments(CharSequence password) throws PaymentsSaveException {
    Preconditions.checkNotNull(paymentDatabaseFile, "'backingStoreFile' must be present. Initialise WalletService.");
    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().isPresent(), "Current wallet summary must be present");

    try {
      log.debug("Writing payments to\n'{}'", paymentDatabaseFile.getAbsolutePath());
      log.trace("Writing TransactionInfoMap: {}", transactionInfoMap);

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
      Payments payments = new Payments();
      Collection<TransactionInfo> transactionInfoCollection = transactionInfoMap.values();
      payments.setTransactionInfoCollection(transactionInfoCollection);
      Collection<MBHDPaymentRequestData> mbhdPaymentRequestDataCollection = mbhdPaymentRequestDataMap.values();
      payments.setMBHDPaymentRequestDataCollection(mbhdPaymentRequestDataCollection);
      Collection<PaymentRequestData> paymentRequestDataCollection = bip70PaymentRequestDataMap.values();
      payments.setPaymentRequestDataCollection(paymentRequestDataCollection);
      protobufSerializer.writePayments(payments, byteArrayOutputStream);
      EncryptedFileReaderWriter.encryptAndWrite(
              byteArrayOutputStream.toByteArray(),
              WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletPassword().getPassword(),
              paymentDatabaseFile
      );

      writePaymentRequestDataFiles(paymentRequestDataCollection, paymentDatabaseFile, password);

      log.debug(
              "Writing payments completed\nTransaction infos: {}\nMBHD payment requests: {}\nBIP70 payment requests: {}",
              transactionInfoCollection.size(), mbhdPaymentRequestDataCollection.size(),
              paymentRequestDataCollection.size());
    } catch (Exception e) {
      log.error("Could not write to payments db\n'{}'", paymentDatabaseFile.getAbsolutePath(), e);
      throw new PaymentsSaveException("Could not write payments db '" + paymentDatabaseFile.getAbsolutePath() + "'. Error was '" + e.getMessage() + "'.", e);
    }
  }

  private File getOrCreateBip70PaymentRequestDirectory(File backingStoreFile) {
    // Work out the directory the raw BIP70 payment requests get written to.
    Preconditions.checkNotNull(backingStoreFile);
    File bip70PaymentRequestDirectory = new File(backingStoreFile.getParent() + File.separator + BIP70_PAYMENT_REQUEST_DIRECTORY);
    if (!bip70PaymentRequestDirectory.exists()) {
      if (!bip70PaymentRequestDirectory.mkdir()) {
        log.error("Could not make directory to store BIP70 payment requests");

      }
    }
    return bip70PaymentRequestDirectory;
  }

  /**
   * @param uuid             The UUID of the PaymentRequestData identifying the overall payment request
   * @param backingStoreFile The backing store file
   * @return A File referencing the PaymentRequest
   */
  private EncryptedBIP70PaymentRequestFile getPaymentRequestFile(UUID uuid, File backingStoreFile) {
    return new EncryptedBIP70PaymentRequestFile(
            getOrCreateBip70PaymentRequestDirectory(backingStoreFile)
                    + File.separator
                    + uuid.toString()
                    + BIP70_PAYMENT_REQUEST_SUFFIX
    );
  }

  /**
   * @param uuid             The UUID of the PaymentRequestData identifying the overall payment
   * @param backingStoreFile The backing store file
   * @return A File referencing the Payment
   */
  private EncryptedBIP70PaymentFile getPaymentFile(UUID uuid, File backingStoreFile) {
    return new EncryptedBIP70PaymentFile(
            getOrCreateBip70PaymentRequestDirectory(backingStoreFile)
                    + File.separator
                    + uuid.toString()
                    + BIP70_PAYMENT_SUFFIX
    );
  }

  /**
   * @param uuid             The UUID of the PaymentRequestData identifying the overall payment
   * @param backingStoreFile The backing store file
   * @return A File referencing the Payment ACK
   */
  private EncryptedBIP70PaymentACKFile getPaymentACKFile(UUID uuid, File backingStoreFile) {
    return new EncryptedBIP70PaymentACKFile(
            getOrCreateBip70PaymentRequestDirectory(backingStoreFile)
                    + File.separator
                    + uuid.toString()
                    + BIP70_PAYMENT_ACK_SUFFIX
    );
  }

  /**
   * Write all the PaymentRequestData collection with serialized supporting BIP70 files
   *
   * @param paymentRequestDataCollection The collection of PaymentRequestData entries to write
   * @param backingStoreFile             The location of the backing store
   */
  private void writePaymentRequestDataFiles(Collection<PaymentRequestData> paymentRequestDataCollection, File backingStoreFile, CharSequence password) throws EncryptedFileReaderWriterException {
    Preconditions.checkNotNull(paymentRequestDataCollection);

    // Work out the directory the raw BIP70 payment requests get written to.
    File bip70PaymentRequestDirectory = new File(backingStoreFile.getParent() + File.separator + BIP70_PAYMENT_REQUEST_DIRECTORY);
    if (!bip70PaymentRequestDirectory.exists()) {
      if (!bip70PaymentRequestDirectory.mkdir()) {
        log.error("Could not make directory to store BIP70 payment requests");
        return;
      }
    }

    // Write all the payment requests, payments and paymentACKS to disk, encrypted with the wallet password
    // Existing files are not overwritten
    for (PaymentRequestData paymentRequestData : paymentRequestDataCollection) {
      EncryptedBIP70PaymentRequestFile paymentRequestFile = getPaymentRequestFile(paymentRequestData.getUuid(), backingStoreFile);
      if (!paymentRequestFile.exists()) {
        // Write the PaymentRequest
        if (paymentRequestData.getPaymentRequest().isPresent()) {
          byte[] serialisedBytes = paymentRequestData.getPaymentRequest().get().toByteArray();
          EncryptedFileReaderWriter.encryptAndWriteDirect(
                  serialisedBytes,
                  password, paymentRequestFile);
          log.debug("Written serialised bytes of unencrypted length {} to output file\n'{}'", serialisedBytes.length, paymentRequestFile.getAbsolutePath());
        }
      }

      EncryptedBIP70PaymentFile paymentFile = getPaymentFile(paymentRequestData.getUuid(), backingStoreFile);
      if (!paymentFile.exists()) {
        // Write the Payment
        if (paymentRequestData.getPayment().isPresent()) {
          byte[] serialisedBytes = paymentRequestData.getPayment().get().toByteArray();

          EncryptedFileReaderWriter.encryptAndWriteDirect(
                  serialisedBytes,
                  password, paymentFile);
          log.debug("Written serialised bytes of unencrypted length {} to output file\n'{}'", serialisedBytes.length, paymentFile.getAbsolutePath());
        }
      }

      EncryptedBIP70PaymentACKFile paymentACKFile = getPaymentACKFile(paymentRequestData.getUuid(), backingStoreFile);
      if (!paymentACKFile.exists()) {
        // Write the PaymentACK
        if (paymentRequestData.getPaymentACK().isPresent()) {
          byte[] serialisedBytes = paymentRequestData.getPaymentACK().get().toByteArray();

          EncryptedFileReaderWriter.encryptAndWriteDirect(
                  serialisedBytes,
                  password, paymentACKFile);
          log.debug("Written serialised bytes of unencrypted length {} to output file\n'{}'", serialisedBytes.length, paymentACKFile.getAbsolutePath());
        }
      }
    }
  }

  /**
   * Read and populate all the PaymentRequestData collection with deserialized supporting BIP70 files
   *
   * @param paymentRequestDataCollection The collection of PaymentRequestData entries to read
   * @param backingStoreFile             The backing store file
   */
  private void readPaymentRequestsDataFiles(Collection<PaymentRequestData> paymentRequestDataCollection, EncryptedFileListItem backingStoreFile, CharSequence password) throws EncryptedFileReaderWriterException {

    Preconditions.checkNotNull(paymentRequestDataCollection);
    Preconditions.checkNotNull(backingStoreFile);

    // Read all the payment requests from disk
    for (PaymentRequestData paymentRequestData : paymentRequestDataCollection) {
      addBIP70PaymentInfoFromFiles(backingStoreFile, paymentRequestData, password);
    }
  }

  /**
   * @param backingStoreFile   The backing store file
   * @param paymentRequestData The payment request data providing the location and receiving the deserialized object
   */
  private void addBIP70PaymentInfoFromFiles(File backingStoreFile, PaymentRequestData paymentRequestData, CharSequence password) throws EncryptedFileReaderWriterException {
    // Locate the PaymentRequest
    EncryptedBIP70PaymentRequestFile paymentRequestFile = getPaymentRequestFile(paymentRequestData.getUuid(), backingStoreFile);

    try {
      if (paymentRequestFile.exists()) {

        byte[] serialisedBytes = EncryptedFileReaderWriter.readAndDecryptToByteArray(
                paymentRequestFile,
                password);
        log.debug("Read serialised bytes of unencrypted length {} from input file:\n'{}'", serialisedBytes.length, paymentRequestFile.getAbsolutePath());

        // Read the serialised Payment Request
        Optional<Protos.PaymentRequest> paymentRequest = Optional.of(Protos.PaymentRequest.parseFrom(serialisedBytes));
        paymentRequestData.setPaymentRequest(paymentRequest);
      }

      // Locate the Payment
      EncryptedBIP70PaymentFile paymentFile = getPaymentFile(paymentRequestData.getUuid(), backingStoreFile);

      if (paymentFile.exists()) {
        byte[] serialisedBytes = EncryptedFileReaderWriter.readAndDecryptToByteArray(
                paymentFile,
                password);
        log.debug("Read serialised bytes of unencrypted length {} from input file:\n'{}'", serialisedBytes.length, paymentFile.getAbsolutePath());

        // Read the serialised Payment
        Optional<Protos.Payment> payment = Optional.of(Protos.Payment.parseFrom(serialisedBytes));
        paymentRequestData.setPayment(payment);
      }

      // Locate the PaymentACK
      EncryptedBIP70PaymentACKFile paymentACKFile = getPaymentACKFile(paymentRequestData.getUuid(), backingStoreFile);

      if (paymentACKFile.exists()) {
        byte[] serialisedBytes = EncryptedFileReaderWriter.readAndDecryptToByteArray(
                paymentACKFile,
                password);
        log.debug("Read serialised bytes of unencrypted length {} from input file:\n'{}'", serialisedBytes.length, paymentACKFile.getAbsolutePath());

        // Read the serialised PaymentACK
        Optional<Protos.PaymentACK> paymentACK = Optional.of(Protos.PaymentACK.parseFrom(serialisedBytes));
        paymentRequestData.setPaymentACK(paymentACK);
      }
    } catch (InvalidProtocolBufferException e) {
      throw new EncryptedFileReaderWriterException("Failed to read BIP70 payment request/ payment/ paymentACK", e);
    }
  }

  public WalletId getWalletId() {
    return walletId;
  }

  public void addMBHDPaymentRequestData(MBHDPaymentRequestData MBHDPaymentRequestData) {
    mbhdPaymentRequestDataMap.put(MBHDPaymentRequestData.getAddress(), MBHDPaymentRequestData);
  }

  /**
   * Add a PaymentRequestData to the memory store, keyed by UUID.
   * If it has no fiat information - add it in
   *
   * @param paymentRequestData Payment request data to add (or replace if the UUID already exists)
   */
  public void addPaymentRequestData(PaymentRequestData paymentRequestData) {
    if (!paymentRequestData.getAmountFiat().hasData()) {
      paymentRequestData.setAmountFiat(calculateFiatPaymentEquivalent(paymentRequestData.getAmountCoin().or(Coin.ZERO)));
    }

    bip70PaymentRequestDataMap.put(paymentRequestData.getUuid(), paymentRequestData);

    log.debug("Adding payment request data: {}", paymentRequestData);
  }

  public void addTransactionInfo(TransactionInfo transactionInfo) {
    transactionInfoMap.putIfAbsent(transactionInfo.getHash(),transactionInfo);;
  }

  public TransactionInfo getTransactionInfoByHash(String transactionHashAsString) {
    return transactionInfoMap.get(transactionHashAsString);
  }

  public Optional<PaymentRequestData> getPaymentRequestDataByHash(String transactionHashAsString) {
    for (PaymentRequestData paymentRequestData : bip70PaymentRequestDataMap.values()) {
      if (paymentRequestData.getTransactionHash().isPresent() && paymentRequestData.getTransactionHash().get().toString().equals(transactionHashAsString)) {
        return Optional.of(paymentRequestData);
      }
    }
    return Optional.absent();
  }

  /**
   * Get a freshly adapted transaction from the current wallet by hash
   *
   * @param transactionHashAsString transaction hash as a string
   * @return transactionData freshly adapted TransactionData or null if no match
   */
  public TransactionData getTransactionDataByHash(String transactionHashAsString) {
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent() && WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet() != null) {
      Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();
      Transaction transaction = wallet.getTransaction(Sha256Hash.wrap(transactionHashAsString));

      // Work out the unmatched BIP70 payment requests
      Set<PaymentData> unmatchedBip70PaymentDatas = createUnmatchedPaymentRequestDatas();

      // Create a Map of all the unmatched paymentDetails
      Map<UUID, Protos.PaymentDetails> unmatchedPaymentDetailsMap = createUnmatchedPaymentDetails(unmatchedBip70PaymentDatas);

      return adaptTransaction(wallet, transaction, unmatchedPaymentDetailsMap);
    } else {
      // No transaction with that hash in current wallet
      return null;
    }
  }


  public List<MBHDPaymentRequestData> getMBHDPaymentRequestDataList() {
    return Lists.newArrayList(mbhdPaymentRequestDataMap.values());
  }

  public List<PaymentRequestData> getPaymentRequestDataList() {
    return Lists.newArrayList(bip70PaymentRequestDataMap.values());
  }


  /**
   * Create the next receiving address for the wallet.
   * This is worked out deterministically
   *
   * @param walletPasswordOptional Either: Optional.absent() = just recycle the first address in the wallet or:  credentials of the wallet to which the new private key is added
   * @return Address the next generated address, as a String. The corresponding private key will be added to the wallet
   */
  public String generateNextReceivingAddress(Optional<CharSequence> walletPasswordOptional) {
    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    if (!currentWalletSummary.isPresent()) {
      // No wallet is present
      throw new IllegalStateException("Trying to add a key to a non-existent wallet");
    } else {
      // Create a new address
      if (walletPasswordOptional.isPresent()) {
        ECKey newKey = currentWalletSummary.get().getWallet().freshReceiveKey();
        String address = newKey.toAddress(networkParameters).toString();
        log.debug("Generated fresh receiving address {}", address);
        return address;
      } else {
        // A credentials is required as all wallets are encrypted
        throw new IllegalStateException("No credentials specified");
      }
    }
  }

  /**
   * Get the last generated receiving address
   *
   * @return the last generated receiving address for this wallet, as a string
   */
  public String getLastGeneratedReceivingAddress() {
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      List<ECKey> issuedReceivingKeys = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet().getIssuedReceiveKeys();
      if (issuedReceivingKeys.isEmpty()) {
        return null;
      } else {
        ECKey lastGeneratedReceivingKey = issuedReceivingKeys.get(0);
        // Find the receiving key with the largest index
        for (int i = 1; i < issuedReceivingKeys.size(); i++) {
          ECKey loopKey = issuedReceivingKeys.get(i);
          ImmutableList<ChildNumber> loopKeyPath = ((DeterministicKey) loopKey).getPath();
          ImmutableList<ChildNumber> lastGeneratedPath = ((DeterministicKey) lastGeneratedReceivingKey).getPath();

          if (loopKeyPath.get(loopKeyPath.size() - 1).num() > lastGeneratedPath.get(lastGeneratedPath.size() - 1).num()) {
            lastGeneratedReceivingKey = loopKey;
          }
        }
        return lastGeneratedReceivingKey.toAddress(BitcoinNetwork.current().get()).toString();
      }
    } else {
      return null;
    }
  }

  /**
   * Work out the current gap in the MBHD payment request datas
   * This is the number of unpaid payment requests since the last paid payment request
   * The motivation for this function is that BIP44 states that there should be a gap limit of 20
   * <p/>
   * The returned variable is an Optional because sometimes the KeyChain information does not give a straightforward
   * answer to what the gap is.
   *
   * @return gap the number of unpaid payment requests since the last paid payment request, or absent if this is not available
   */
  public Optional<Integer> getGap() {
    // Find the most recently paid payment request data
    MBHDPaymentRequestData mostRecentMBHDPaymentRequestData = null;

    for (MBHDPaymentRequestData mbhdPaymentRequestData : mbhdPaymentRequestDataMap.values()) {
      if (!mbhdPaymentRequestData.getPayingTransactionHashes().isEmpty()) {
        if (mostRecentMBHDPaymentRequestData == null) {
          mostRecentMBHDPaymentRequestData = mbhdPaymentRequestData;
        } else {
          if (mbhdPaymentRequestData.getDate().compareTo(mostRecentMBHDPaymentRequestData.getDate()) > 0) {
            mostRecentMBHDPaymentRequestData = mbhdPaymentRequestData;
          }
        }
      }
    }

    if (mostRecentMBHDPaymentRequestData == null) {
      // No payment requests have been paid, hence gap is the number of payment requests
      return Optional.of(mbhdPaymentRequestDataMap.size());
    } else {
      Address mostRecentlyPaidAddress = mostRecentMBHDPaymentRequestData.getAddress();

      // Find the key in the wallet that matches this address
      // and also find last receiving key index
      Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
      if (!currentWalletSummary.isPresent()) {
        throw new IllegalStateException("No wallet available to work out gap for");
      }
      Wallet wallet = currentWalletSummary.get().getWallet();
      int numberOfIssuedExternalAddresses = wallet.getActiveKeychain().getIssuedExternalKeys();
      int numberOfIssuedInternalAddresses = wallet.getActiveKeychain().getIssuedInternalKeys();
      List<ECKey> issuedReceiveKeys = wallet.getIssuedReceiveKeys();

      boolean foundLastPaidKeyIndex = false;
      int lastPaidKeyIndex = -1;
      int lastReceivingKeyIndex = -1;

      for (ECKey loopKey : issuedReceiveKeys) {
        // Look for the lastPaidKeyIndex
        if (!foundLastPaidKeyIndex) {
          if (mostRecentlyPaidAddress.equals(loopKey.toAddress(BitcoinNetwork.current().get()))) {
            // Found it
            ImmutableList<ChildNumber> lastPaidKeyPath = ((DeterministicKey) loopKey).getPath();
            lastPaidKeyIndex = lastPaidKeyPath.get(lastPaidKeyPath.size() - 1).num();
            // Skip calculating the addresses from now on as that is a non trivial op
            foundLastPaidKeyIndex = true;
          }
        }

        ImmutableList<ChildNumber> loopKeyPath = ((DeterministicKey) loopKey).getPath();
        int loopKeyIndex = loopKeyPath.get(loopKeyPath.size() - 1).num();
        lastReceivingKeyIndex = Math.max(lastReceivingKeyIndex, loopKeyIndex);
      }

      log.debug("lastPaidKeyIndex: {}, lastReceivingKeyIndex: {}, numberOfIssuedExternalAddresses: {}, numberOfIssuedInternalAddresses: {}", lastPaidKeyIndex, lastReceivingKeyIndex, numberOfIssuedExternalAddresses, numberOfIssuedInternalAddresses);

      // If the lastReceivingKeyIndex is not one less than the numberOfIssuedExternalAddresses then the following
      // algorithm will not calculate the gap correctly so return absent
      // (JB - not sure why this happens at the moment - the number of issued change addresses can be higher than the internal addresses)
      if (lastReceivingKeyIndex + 1 != numberOfIssuedExternalAddresses) {
        return Optional.absent();
      }

      // Work out the gap from the difference in the last indices of the path
      if (lastPaidKeyIndex == -1) {
        // No payment request has been paid
        if (lastReceivingKeyIndex == -1) {
          // No receiving keys have been generated
          return Optional.of(0);
        } else {
          return Optional.of(lastReceivingKeyIndex + 1);
        }
      } else {
        // There is a last paid payment request
        if (lastReceivingKeyIndex == -1) {
          throw new IllegalStateException("A payment request has been paid but no receiving addresses have been created. This is odd.");
        } else {
          return Optional.of(lastReceivingKeyIndex - lastPaidKeyIndex);
        }
      }
    }
  }

  /**
   * Find the payment requests that are either partially or fully funded by the transaction specified
   *
   * @param transactionData The transaction data
   * @return The list of payment requests that the transaction data funds
   */
  public List<MBHDPaymentRequestData> findPaymentRequestsThisTransactionFunds(TransactionData transactionData) {

    List<MBHDPaymentRequestData> MBHDPaymentRequestDataList = Lists.newArrayList();

    if (transactionData != null && transactionData.getOutputAddresses() != null) {
      for (Address address : transactionData.getOutputAddresses()) {
        MBHDPaymentRequestData MBHDPaymentRequestData = mbhdPaymentRequestDataMap.get(address);
        if (MBHDPaymentRequestData != null) {
          // This transaction funds this payment address
          MBHDPaymentRequestDataList.add(MBHDPaymentRequestData);
        }
      }
    }

    return MBHDPaymentRequestDataList;
  }

  /**
   * Delete a MBHD payment request
   */
  public void deleteMBHDPaymentRequest(MBHDPaymentRequestData mbhdPaymentRequestData) {
    undoDeletePaymentDataStack.push(mbhdPaymentRequestData);
    mbhdPaymentRequestDataMap.remove(mbhdPaymentRequestData.getAddress());
  }

  /**
   * Delete a BIP70 payment request
   */
  public void deletePaymentRequest(PaymentRequestData paymentRequestData) {
    undoDeletePaymentDataStack.push(paymentRequestData);
    bip70PaymentRequestDataMap.remove(paymentRequestData.getUuid());

    // Delete the serialised payment request file
    EncryptedBIP70PaymentRequestFile paymentRequestFile = getPaymentRequestFile(paymentRequestData.getUuid(), paymentDatabaseFile);
    try {
      if (paymentRequestFile.exists()) {
        SecureFiles.secureDelete(paymentRequestFile);
      }
    } catch (IOException e) {
      log.error("Could not delete the payment request file\n'{}'", paymentRequestFile.getAbsolutePath());
    }

    // Delete the serialised payment file
    EncryptedBIP70PaymentFile paymentFile = getPaymentFile(paymentRequestData.getUuid(), paymentDatabaseFile);
    try {
      if (paymentFile.exists()) {
        SecureFiles.secureDelete(paymentFile);
      }
    } catch (IOException e) {
      log.error("Could not delete the payment file\n'{}'", paymentRequestFile.getAbsolutePath());
    }

    // Delete the serialised payment ACK file
    EncryptedBIP70PaymentACKFile paymentACKFile = getPaymentACKFile(paymentRequestData.getUuid(), paymentDatabaseFile);
    try {
      if (paymentACKFile.exists()) {
        SecureFiles.secureDelete(paymentACKFile);
      }
    } catch (IOException e) {
      log.error("Could not delete the payment ACK file\n'{}'", paymentACKFile.getAbsolutePath());
    }
  }

  /**
   * Undo the deletion of an MBHD or BIP70 payment request
   */
  @SuppressFBWarnings({"ITC_INHERITANCE_TYPE_CHECKING"})
  public void undoDeletePaymentData() {
    if (canUndo()) {
      PaymentData paymentData = undoDeletePaymentDataStack.pop();
      if (paymentData instanceof PaymentRequestData) {
        // BIP70 undo
        addPaymentRequestData((PaymentRequestData) paymentData);
      } else {
        if (paymentData instanceof MBHDPaymentRequestData) {
          // MBHD undo
          addMBHDPaymentRequestData((MBHDPaymentRequestData) paymentData);
        }
      }
    }
  }

  /**
   * Indicate whether an undo is possible
   */
  public boolean canUndo() {
    return !undoDeletePaymentDataStack.isEmpty();
  }

  /**
   * Export the payments to three CSV files - one for transactions, one for MBHD payment requests and one for BIP70 payment requests.
   * Sends a ExportPerformedEvent with the results.
   *
   * @param exportDirectory            The directory to export to
   * @param transactionFileStem        The stem of the export file for the transactions (will be suffixed with a file suffix and possibly a bracketed number for uniqueness)
   * @param mbhdPaymentRequestFileStem The stem of the export file for the MBHD payment requests (will be suffixed with a file suffix and possibly a bracketed number for uniqueness)
   * @param paymentRequestFileStem     The stem of the export file for the BIP70 payment requests (will be suffixed with a file suffix and possibly a bracketed number for uniqueness)
   */
  public void exportPayments(
          File exportDirectory,
          String transactionFileStem,
          String mbhdPaymentRequestFileStem,
          String paymentRequestFileStem,
          CSVEntryConverter<TransactionData> transactionHeaderConverter,
          CSVEntryConverter<TransactionData> transactionConverter,
          CSVEntryConverter<MBHDPaymentRequestData> mbhdPaymentRequestHeaderConverter,
          CSVEntryConverter<MBHDPaymentRequestData> mbhdPaymentRequestConverter,
          CSVEntryConverter<PaymentRequestData> paymentRequestHeaderConverter,
          CSVEntryConverter<PaymentRequestData> paymentRequestConverter

  ) {
    // Refresh all payments
    Set<PaymentData> paymentDataSet = getPaymentDataSet();
    ExportManager.export(
            paymentDataSet,
            getMBHDPaymentRequestDataList(),
            getPaymentRequestDataList(),
            exportDirectory,
            transactionFileStem,
            mbhdPaymentRequestFileStem,
            paymentRequestFileStem,
            transactionHeaderConverter,
            transactionConverter,
            mbhdPaymentRequestHeaderConverter,
            mbhdPaymentRequestConverter,
            paymentRequestHeaderConverter,
            paymentRequestConverter
    );
  }

  /**
   * Change the wallet credentials for the current wallet
   * The result of the operation is emitted as a ChangePasswordResultEvent
   *
   * @param oldPassword The old wallet credentials
   * @param newPassword The new wallet credentials
   */
  public static void changeCurrentWalletPassword(final String oldPassword, final String newPassword) {
    if (executorService == null) {
      executorService = SafeExecutors.newSingleThreadExecutor("wallet-service");
    }

    executorService.submit(
            new Runnable() {
              @Override
              public void run() {
                WalletService.changeCurrentWalletPasswordInternal(oldPassword, newPassword);
              }
            });
  }

  static void changeCurrentWalletPasswordInternal(final String oldPassword, final String newPassword) {
    Optional<WalletSummary> walletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

    if (walletSummaryOptional == null || !walletSummaryOptional.isPresent() || walletSummaryOptional.get().getWallet() == null) {
      // No wallet to change the credentials for
      CoreEvents.fireChangePasswordResultEvent(new ChangePasswordResultEvent(false, CoreMessageKey.CHANGE_PASSWORD_ERROR, new Object[]{"There is no wallet"}));
      return;
    }

    WalletSummary walletSummary = walletSummaryOptional.get();
    Wallet wallet = walletSummary.getWallet();
    WalletId walletId = walletSummary.getWalletId();

    // Check old credentials
    if (!wallet.checkPassword(oldPassword)) {
      CoreEvents.fireChangePasswordResultEvent(new ChangePasswordResultEvent(false, CoreMessageKey.CHANGE_PASSWORD_WRONG_OLD_PASSWORD, null));
      return;
    }

    // Locate the installation directory and current wallet paths
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).isPresent());
    File currentWalletSummaryFile = WalletManager.INSTANCE.getCurrentWalletSummaryFile(applicationDataDirectory).get();

    // Create a List of all the non-wallet files that need to have their password changed
    List<EncryptedFileListItem> filesToChangePassword = createListOfFilesToChangePassword(applicationDataDirectory, walletId);

    for (File file : filesToChangePassword) {
      log.debug("File to change password on {}", file.getAbsolutePath());
    }

    // Close the Network connection to stop writes to the wallet + payments database whilst we are rewriting files
    // Close  Contacts / Payments
    CoreServices.shutdownNow(ShutdownEvent.ShutdownType.SWITCH);

    // Close the current wallet
    WalletManager.INSTANCE.shutdownNow(ShutdownEvent.ShutdownType.SWITCH);

    // Close the backup manager for the wallet
    BackupManager.INSTANCE.shutdownNow();

    // Close the installation manager
    InstallationManager.shutdownNow(ShutdownEvent.ShutdownType.SWITCH);

    try {
      // Decrypt the seedDerivedAESKey using the old credentials and encrypt it with the new one
      byte[] encryptedOldBackupAESKey = walletSummary.getEncryptedBackupKey();

      KeyParameter oldWalletPasswordDerivedAESKey = org.multibit.commons.crypto.AESUtils.createAESKey(oldPassword.getBytes(Charsets.UTF_8), WalletManager.scryptSalt());
      byte[] decryptedOldBackupAESKey = AESUtils.decrypt(
              encryptedOldBackupAESKey,
              oldWalletPasswordDerivedAESKey,
              WalletManager.aesInitialisationVector());

      KeyParameter newWalletPasswordDerivedAESKey = org.multibit.commons.crypto.AESUtils.createAESKey(newPassword.getBytes(Charsets.UTF_8), WalletManager.scryptSalt());
      byte[] encryptedNewBackupAESKey = AESUtils.encrypt(
              decryptedOldBackupAESKey,
              newWalletPasswordDerivedAESKey,
              WalletManager.aesInitialisationVector());

      // Check the encryption is reversible
      byte[] decryptedRebornBackupAESKey = AESUtils.decrypt(
              encryptedNewBackupAESKey,
              newWalletPasswordDerivedAESKey,
              WalletManager.aesInitialisationVector());

      if (!Arrays.equals(decryptedOldBackupAESKey, decryptedRebornBackupAESKey)) {
        throw new IllegalStateException("The encryption of the backup AES key was not reversible. Aborting change of wallet credentials");
      }

      // Encrypt the new credentials with an the decryptedOldBackupAESKey
      // Pad the new credentials
      byte[] newPasswordBytes = newPassword.getBytes(Charsets.UTF_8);
      byte[] paddedNewPassword = WalletManager.padPasswordBytes(newPasswordBytes);
      byte[] encryptedPaddedNewPassword = AESUtils.encrypt(
              paddedNewPassword,
              new KeyParameter(decryptedOldBackupAESKey),
              WalletManager.aesInitialisationVector());

      // Check the encryption is reversible
      byte[] decryptedRebornPaddedNewPassword = AESUtils.decrypt(
              encryptedPaddedNewPassword,
              new KeyParameter(decryptedOldBackupAESKey),
              WalletManager.aesInitialisationVector());

      if (!Arrays.equals(newPasswordBytes, WalletManager.unpadPasswordBytes(decryptedRebornPaddedNewPassword))) {
        throw new IllegalStateException("The encryption of the new credentials was not reversible. Aborting change of wallet credentials");
      }

      // Change the password on all the non-wallet files, save them to disk but don't do the "rename existing + rename new + delete old" commit
      List<EncryptedFileListItem> newFiles = EncryptedFileReaderWriter.changeEncryptionPrepare(filesToChangePassword, oldPassword, newPassword);

      // Change the credentials used to encrypt the wallet
      wallet.decrypt(oldPassword);
      walletSummary.setWalletPassword(new WalletPassword(newPassword, walletId));
      walletSummary.setEncryptedPassword(encryptedPaddedNewPassword);
      wallet.encrypt(newPassword);

      // WALLET WAS ENCRYPTED OK - SAVE EVERYTHING WITH NEW PASSWORD

      // Save the new encrypted backup key using the new password
      walletSummary.setEncryptedBackupKey(encryptedNewBackupAESKey);

      // Save the wallet summary file
      WalletManager.updateWalletSummary(currentWalletSummaryFile, walletSummary);
      WalletManager.INSTANCE.setCurrentWalletSummary(walletSummary);
      WalletManager.INSTANCE.saveWallet();

      // Do the commit of the changed non-wallet files by "rename existing + rename new + delete old"
      EncryptedFileReaderWriter.changeEncryptionCommit(filesToChangePassword, newFiles);

      // Restart Contacts / Payments / Bitcoin network services
      CoreServices.bootstrap();
      CoreServices.getOrCreateBackupService();
      CoreServices.getOrCreateWalletService(walletId);
      BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
      CoreServices.getOrCreateContactService(new WalletPassword(newPassword, walletId));

      // Replay the wallet
      bitcoinNetworkService.replayWallet(
              applicationDataDirectory,
              Optional.<DateTime>absent(), // No checkpoints
              false, // No fast catch up
              false // Do not clear mempool
      );

      CoreEvents.fireChangePasswordResultEvent(new ChangePasswordResultEvent(true, CoreMessageKey.CHANGE_PASSWORD_SUCCESS, null));
    } catch (RuntimeException | NoSuchAlgorithmException | IOException e) {
      log.error("Failed to change password", e);
      CoreEvents.fireChangePasswordResultEvent(new ChangePasswordResultEvent(false, CoreMessageKey.CHANGE_PASSWORD_ERROR, new Object[]{e.getMessage()}));
    }
  }

  private static List<EncryptedFileListItem> createListOfFilesToChangePassword(File applicationDataDirectory, WalletId walletId) {
    String currentWalletDirectoryPath = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, WalletManager.createWalletRoot(walletId)).getAbsolutePath();

    WalletService walletService = CoreServices.getOrCreateWalletService(walletId);
    List<PaymentRequestData> paymentRequestDataList = walletService.getPaymentRequestDataList();

    // Create a List of all the non-wallet files that need to have their password changed
    List<EncryptedFileListItem> filesToChangePassword = Lists.newArrayList();

    // Contacts
    filesToChangePassword.add(new EncryptedContactsFile(currentWalletDirectoryPath + File.separator + ContactService.CONTACTS_DIRECTORY_NAME + File.separator + ContactService.CONTACTS_DATABASE_NAME));

    // Payments
    filesToChangePassword.add(new EncryptedPaymentsFile(currentWalletDirectoryPath + File.separator + PAYMENTS_DIRECTORY_NAME + File.separator + PAYMENTS_DATABASE_NAME));

    // BIP70 Payment requests
    if (paymentRequestDataList != null) {
      for (PaymentRequestData paymentRequestData : paymentRequestDataList) {
        UUID uuid = paymentRequestData.getUuid();
        File paymentBackingStore = walletService.getPaymentDatabaseFile();

        EncryptedBIP70PaymentRequestFile paymentRequestFile = walletService.getPaymentRequestFile(uuid, paymentBackingStore);
        filesToChangePassword.add(paymentRequestFile);

        EncryptedBIP70PaymentFile paymentFile = walletService.getPaymentFile(uuid, paymentBackingStore);
        filesToChangePassword.add(paymentFile);

        EncryptedBIP70PaymentACKFile paymentACKFile = walletService.getPaymentACKFile(uuid, paymentBackingStore);
        filesToChangePassword.add(paymentACKFile);
      }
    }
    return filesToChangePassword;
  }

  /**
   * <p>When a transaction is seen by the network, ensure there is a transaction info available storing the exchange rate</p>
   *
   * @param transactionSeenEvent The event (very high frequency during synchronisation)
   */
  @Subscribe
  public void onTransactionSeenEvent(TransactionSeenEvent transactionSeenEvent) {

    // If not in the transaction info map create on and add
    if (transactionInfoMap.get(transactionSeenEvent.getTransactionId()) == null) {

      // Create a new transaction info
      TransactionInfo transactionInfo = new TransactionInfo();
      transactionInfo.setHash(transactionSeenEvent.getTransactionId());

      // Create the fiat payment
      FiatPayment amountFiat = new FiatPayment();
      amountFiat.setExchangeName(Optional.of(ExchangeKey.current().getExchangeName()));

      if (CoreServices.getApplicationEventService() != null) {
        Optional<ExchangeRateChangedEvent> exchangeRateChangedEvent = CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent();
        if (exchangeRateChangedEvent.isPresent() && exchangeRateChangedEvent.get().getRate() != null) {

          amountFiat.setRate(Optional.of(exchangeRateChangedEvent.get().getRate().toString()));
          BigDecimal localAmount = Coins.toLocalAmount(
                  transactionSeenEvent.getAmount(),
                  exchangeRateChangedEvent.get().getRate()
          );

          if (localAmount.compareTo(BigDecimal.ZERO) != 0) {
            amountFiat.setAmount(Optional.of(localAmount));
          } else {
            amountFiat.setAmount(Optional.<BigDecimal>absent());
          }

          amountFiat.setCurrency(Optional.of(exchangeRateChangedEvent.get().getCurrency()));

        } else {
          amountFiat.setRate(Optional.<String>absent());
          amountFiat.setAmount(Optional.<BigDecimal>absent());
          amountFiat.setCurrency(Optional.<Currency>absent());
        }

        transactionInfo.setAmountFiat(amountFiat);

        // Use the atomic putIfAbsent to ensure we don't overwrite
        if (transactionInfoMap.putIfAbsent(transactionSeenEvent.getTransactionId(), transactionInfo) == null) {
          log.debug("Created TransactionInfo: {}", transactionInfo);
        } else {
          log.debug("Not adding transactionInfo - another process has already added transactionInfo: {}", transactionInfo);
        }
      }
    }
  }

  static class PaymentComparator implements Comparator<PaymentData>, Serializable {
    @Override
    public int compare(PaymentData o1, PaymentData o2) {
      if (o1.getDate() == null) {
        return -1;
      }
      if (o2.getDate() == null) {
        return 1;
      }
      int dateSort = o2.getDate().compareTo(o1.getDate()); // note inverse sort
      if (dateSort != 0) {
        return dateSort;
      } else {
        return o1.getAmountCoin().or(Coin.ZERO).compareTo(o2.getAmountCoin().or(Coin.ZERO));
      }
    }
  }

  public File getPaymentDatabaseFile() {
    return paymentDatabaseFile;
  }
}
