package org.multibit.hd.core.services;

import com.google.bitcoin.core.*;
import com.google.bitcoin.crypto.KeyCrypterException;
import com.google.bitcoin.net.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.wallet.KeyChain;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.subgraph.orchid.TorClient;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.BitcoinSentEvent;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.TransactionCreationEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.BlockStoreManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.network.MultiBitPeerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>Service to provide access to the Bitcoin network, including:</p>
 * <ul>
 * <li>Initialisation of bitcoin network connection</li>
 * <li>Ability to send bitcoin</li>
 * </ul>
 * <p/>
 * <p>Emits the following events:</p>
 * <ul>
 * <li><code>BitcoinNetworkChangeEvent</code></li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class BitcoinNetworkService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkService.class);

  public static final BigInteger DEFAULT_FEE_PER_KB = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE; // Currently 10,000 satoshi
  public static final int MAXIMUM_NUMBER_OF_PEERS = 6;

  private BlockStore blockStore;
  private PeerGroup peerGroup;  // May need to add listener as in MultiBitPeerGroup
  private BlockChain blockChain;
  private MultiBitPeerEventListener peerEventListener;

  private final NetworkParameters networkParameters;

  private boolean startedOk = false;

  /**
   * @param networkParameters The Bitcoin network parameters
   */
  public BitcoinNetworkService(NetworkParameters networkParameters) {

    Preconditions.checkNotNull(networkParameters, "'networkParameters' must be present");

    log.debug("Bitcoin network service using '{}'", networkParameters.getId());

    this.networkParameters = networkParameters;
  }

  @Override
  public boolean start() {

    try {

      // Check if there is a wallet - if there is no wallet the network will not start (there's nowhere to put the blockchain)
      if (!WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
        log.warn("Not starting bitcoin network service as there is currently no wallet.");
        return true;
      }
      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
      String walletRoot = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get().getParentFile().getAbsolutePath();
      File blockStoreFile = new File(walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX);
      File checkpointsFile = new File(walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX);

      // Load or create the blockStore..
      log.debug("Create new block store");
      blockStore = new BlockStoreManager(networkParameters).createBlockStore(blockStoreFile, checkpointsFile, null, false);
      log.debug("Success. Blockstore is '{}'", blockStore);

      log.debug("Starting Bitcoin network...");

      restartNetwork();

    } catch (IOException | BlockStoreException | TimeoutException e) {

      log.error(e.getMessage(), e);

      CoreEvents.fireBitcoinNetworkChangedEvent(
        BitcoinNetworkSummary.newNetworkStartupFailed(
          CoreMessageKey.START_NETWORK_CONNECTION_ERROR,
          Optional.of(new Object[]{})
        )
      );

    }

    return true;

  }

  @Override
  public void stopAndWait() {

    startedOk = false;

    // Stop the peer group if it is running
    stopPeerGroup();

    // Close the block store
    closeBlockstore();

    // Save the current wallet
    saveWallet();

    // Close the wallet
    closeWallet();

    // Hand over to the superclass to finalise service executors
    super.stopAndWait();

    log.debug("Bitcoin network service shut down");

  }

  /**
   * @return True if the network has started and a peer group is available
   */
  public boolean isStartedOk() {
    return startedOk;
  }

  public void recalculateFastCatchupAndFilter() {

    if (peerGroup != null) {
      peerGroup.recalculateFastCatchupAndFilter(PeerGroup.FilterRecalculateMode.FORCE_SEND);
    }

  }

  /**
   * <p>Download the block chain in a new thread</p>
   */
  public void downloadBlockChainInBackground() {

    getExecutorService().submit(new Runnable() {
      @Override
      public void run() {
        Preconditions.checkNotNull(peerGroup, "'peerGroup' must be present");

        log.debug("Downloading block chain...");

        // This method blocks until completed but fires events along the way
        try {
          peerGroup.downloadBlockChain();
        } catch (RuntimeException re) {
          log.debug("Blockchain download was interrupted. Error was : '" + re.getMessage() + "'");
        }

        log.debug("Block chain downloaded.");

        CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newNetworkReady(peerGroup.numConnectedPeers()));
      }
    });

  }

  /**
   * Sync the current wallet from the date specified.
   * The blockstore is deleted and created anew, checkpointed and then the blockchain is downloaded.
   */
  public void replayWallet(DateTime dateToReplayFrom) throws IOException, BlockStoreException, TimeoutException {

    Preconditions.checkNotNull(dateToReplayFrom);
    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().isPresent());

    log.debug("Stopping any existing downloads");

    // Stop the peer group if it is running
    stopPeerGroup();

    // Close the block store
    closeBlockstore();

    log.info("Starting replay of wallet with id '" + WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletId()
      + "' from date " + dateToReplayFrom);

    // TODO (JB) The current best height should be remembered and used to generate percentage complete as
    // TODO (JB) then if the peer is replaced the percentage increases monotonically

    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    String walletRoot = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get().getParentFile().getAbsolutePath();

    File blockchainFile = new File(walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX);
    File checkpointsFile = new File(walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX);

    log.debug("Recreating blockstore with checkpoint date of " + dateToReplayFrom + " ...");
    blockStore = new BlockStoreManager(networkParameters).createBlockStore(blockchainFile, checkpointsFile, dateToReplayFrom.toDate(), true);
    log.debug("Blockstore is '{}'", blockStore);

    restartNetwork();

    downloadBlockChainInBackground();

    log.debug("Blockchain download started.");
  }

  /**
   * <p>Send bitcoin</p>
   * <p/>
   * <p>In the future will also need:</p>
   * <ul>
   * <li>the wallet to send from - when Trezor comes onstream</li>
   * <li>a CoinSelector - when HD subnodes are supported</li>
   * </ul>
   * <p>The result of the operation is sent to the CoreEventBus as a TransactionCreationEvent and, if the tx is created ok, a BitcoinSentEvent</p>
   *
   * @param sendRequestSummary The information required to send bitcoin
   */
  public void send(final SendRequestSummary sendRequestSummary) {

    getExecutorService().submit(new Runnable() {
      @Override
      public void run() {

        performSend(sendRequestSummary);
      }

    });
  }

  /**
   * @param sendRequestSummary The information required to send bitcoin
   *
   * @return The send request
   */
  private boolean performSend(SendRequestSummary sendRequestSummary) {

    log.debug("Starting the send process");

    // Verify the wallet summary
    if (!checkWalletSummary(sendRequestSummary)) {
      log.debug("Wallet summary check fail");
      return false;
    }

    // Get the current wallet
    Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();

    // Derive the key parameter to unlock the wallet
    if (!deriveKeyParameter(sendRequestSummary, wallet)) {
      return false;
    }

    // Build and append the client fee (if required)
    if (!appendClientFee(sendRequestSummary)) {
      return false;
    }

    // Build and append the send request
    appendSendRequest(sendRequestSummary);

    // Attempt to complete it
    if (!completeAndCommit(sendRequestSummary, wallet)) {
      return false;
    }

    // Attempt to broadcast it
    if (!broadcast(sendRequestSummary)) {
      return false;
    }

    // Must be OK to be here
    log.debug("Send coins has completed");
    return true;

  }

  /**
   * @param sendRequestSummary The information required to send bitcoin
   * @param wallet             The wallet
   *
   * @return True if the derivation process was successful
   */
  private boolean deriveKeyParameter(SendRequestSummary sendRequestSummary, Wallet wallet) {

    // Wrap it all up in a try-catch to ensure we fire a failure event
    try {

      log.debug("Deriving key parameter");

      Preconditions.checkNotNull(wallet, "'wallet' must be present");

      if (wallet.getKeyCrypter() == null) {
        throw new IllegalStateException("No key crypter in wallet when one is expected.");
      }
      sendRequestSummary.setKeyParameter(wallet.getKeyCrypter().deriveKey(sendRequestSummary.getPassword()));

      return true;

    } catch (IllegalStateException | KeyCrypterException e) {

      log.error(e.getMessage(), e);

      // Declare the transaction creation a failure
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        null,
        sendRequestSummary.getAmount(),
        BigInteger.ZERO,
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        false,
        CoreMessageKey.THE_ERROR_WAS.getKey(),
        new String[]{e.getClass().getCanonicalName() + " " + e.getMessage()}));

    }

    // Must have failed to be here
    return false;

  }

  /**
   * @param sendRequestSummary The information required to send bitcoin
   *
   * @return True if no error was encountered
   */
  private boolean appendClientFee(SendRequestSummary sendRequestSummary) {

    log.debug("Appending client fee (if required)");

    final boolean isClientFeeRequired;
    if (sendRequestSummary.getFeeState().isPresent()) {

      int currentNumberOfSends = sendRequestSummary.getFeeState().get().getCurrentNumberOfSends();
      int nextFeeSendCount = sendRequestSummary.getFeeState().get().getNextFeeSendCount();

      isClientFeeRequired = (currentNumberOfSends == nextFeeSendCount);

    } else {

      // Nothing more to be done
      return true;
    }

    // May need to add the client fee
    if (isClientFeeRequired) {

      Address feeAddress = sendRequestSummary.getFeeState().get().getNextFeeAddress();
      sendRequestSummary.setFeeAddress(feeAddress);

      log.debug("Added client fee to address: '{}'", feeAddress);

    }

    // Must be OK to be here
    return true;
  }

  /**
   * Handle missing wallet summary
   *
   * @param sendRequestSummary The information required to send bitcoin
   *
   * @return True if the wallet summary is present
   */
  private boolean checkWalletSummary(SendRequestSummary sendRequestSummary) {

    if (!WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {

      // Declare the transaction creation a failure - no wallet
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        null,
        sendRequestSummary.getAmount(),
        BigInteger.ZERO,
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        false,
        CoreMessageKey.NO_ACTIVE_WALLET.getKey(),
        new String[]{""}
      ));

      // Prevent fall-through to success
      return false;
    }

    // Must be OK to be here
    return true;
  }

  /**
   * <p>Build and append a Bitcoinj send request using the summary</p>
   *
   * @param sendRequestSummary The information required to send bitcoin
   */
  private void appendSendRequest(SendRequestSummary sendRequestSummary) {

    log.debug("Appending send request");

    final Wallet.SendRequest sendRequest = Wallet.SendRequest.to(
      sendRequestSummary.getDestinationAddress(),
      sendRequestSummary.getAmount()
    );
    sendRequest.aesKey = sendRequestSummary.getKeyParameter().get();
    sendRequest.fee = BigInteger.ZERO;
    sendRequest.feePerKb = sendRequestSummary.getFeePerKB();
    sendRequest.changeAddress = sendRequestSummary.getChangeAddress();

    // TODO (JB) Position of fee should be randomized
    if (sendRequestSummary.getFeeAddress().isPresent()) {
      sendRequest.tx.addOutput(
        sendRequestSummary.getFeeState().get().getFeeOwed(),
        sendRequestSummary.getFeeAddress().get()
      );
    }

    // Append the Bitcoinj send request to the summary
    sendRequestSummary.setSendRequest(sendRequest);

  }

  /**
   * @param sendRequestSummary The information required to send bitcoin
   * @param wallet             The wallet
   *
   * @return True if the complete and commit operations were successful
   */
  private boolean completeAndCommit(SendRequestSummary sendRequestSummary, Wallet wallet) {

    log.debug("Complete and commit send request...");

    Wallet.SendRequest sendRequest = sendRequestSummary.getSendRequest().get();

    try {

      // Complete it (works out fee and signs tx)
      wallet.completeTx(sendRequestSummary.getSendRequest().get());

      // Commit to the wallet (informs the wallet of the transaction)
      wallet.commitTx(sendRequest.tx);

      // Fire a successful transaction creation event (not yet broadcast)
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        sendRequest.tx.getHashAsString(),
        sendRequestSummary.getAmount(),
        sendRequest.fee /* the actual fee paid */,
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        true,
        null,
        null
      ));

    } catch (Exception e) {

      log.error(e.getMessage(), e);

      String transactionId = sendRequest.tx != null ? sendRequest.tx.getHashAsString() : "?";

      // Fire a failed transaction creation event
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        transactionId,
        sendRequestSummary.getAmount(),
        BigInteger.ZERO,
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        false,
        CoreMessageKey.THE_ERROR_WAS.getKey(),
        new String[]{e.getMessage()}));

      // We cannot proceed to broadcast
      return false;
    }

    // Must be OK to be here
    return true;
  }

  /**
   * <p>Attempt to broadcast the transaction in the Bitcoinj send request</p>
   *
   * @param sendRequestSummary The information required to send bitcoin
   *
   * @return True if the broadcast operation was successful
   */
  private boolean broadcast(SendRequestSummary sendRequestSummary) {

    log.debug("Attempting to broadcast transaction");

    Wallet.SendRequest sendRequest = sendRequestSummary.getSendRequest().get();

    try {

      // Ping the peers to check the Bitcoin network connection
      if (!pingPeers()) {

        // Declare the send a failure
        CoreEvents.fireBitcoinSentEvent(new BitcoinSentEvent(
          sendRequestSummary.getDestinationAddress(),
          sendRequestSummary.getAmount(),
          sendRequestSummary.getChangeAddress(),
          BigInteger.ZERO,
          false,
          CoreMessageKey.COULD_NOT_CONNECT_TO_BITCOIN_NETWORK.getKey(),
          new String[]{"Could not reach any Bitcoin nodes"}
        ));

        // Prevent a fall-through to success
        return false;
      }

      // Broadcast
      peerGroup.broadcastTransaction(sendRequest.tx);

      log.debug("Broadcast transaction: '{}'", Utils.bytesToHexString(sendRequest.tx.bitcoinSerialize()));

      // Declare the send a success
      CoreEvents.fireBitcoinSentEvent(new BitcoinSentEvent(
        sendRequestSummary.getDestinationAddress(), sendRequestSummary.getAmount(),
        sendRequestSummary.getChangeAddress(), BigInteger.ZERO,
        true,
        CoreMessageKey.BITCOIN_SENT_OK.getKey(),
        null
      ));

    } catch (VerificationException e) {

      log.error(e.getMessage(), e);

      // Declare the send a failure
      CoreEvents.fireBitcoinSentEvent(new BitcoinSentEvent(
        sendRequestSummary.getDestinationAddress(), sendRequestSummary.getAmount(),
        sendRequestSummary.getChangeAddress(), BigInteger.ZERO,
        false,
        CoreMessageKey.THE_ERROR_WAS.getKey(),
        new String[]{e.getMessage()}
      ));

      // Prevent a fall-through to success
      return false;

    }

    // Must be OK to be here
    return true;

  }

  /**
   * <p>Create a new peer group</p>
   */
  private void createNewPeerGroup() throws TimeoutException {

    if (Configurations.currentConfiguration.isTor()) {

      log.info("Creating new TOR peer group for '{}'", networkParameters);
      InstallationManager.removeCryptographyRestrictions();
      peerGroup = PeerGroup.newWithTor(networkParameters, blockChain, new TorClient());

    } else {

      log.info("Creating new DNS peer group for '{}'", networkParameters);
      peerGroup = new PeerGroup(networkParameters, blockChain);
      peerGroup.addPeerDiscovery(new DnsDiscovery(networkParameters));

    }

    peerGroup.setUserAgent(InstallationManager.MBHD_APP_NAME,
      Configurations.currentConfiguration.getApplication().getVersion());
    peerGroup.setFastCatchupTimeSecs(0); // genesis block
    peerGroup.setMaxConnections(MAXIMUM_NUMBER_OF_PEERS);

    peerEventListener = new MultiBitPeerEventListener();
    peerGroup.addEventListener(peerEventListener);

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();
      peerGroup.addWallet(wallet);
      peerGroup.setFastCatchupTimeSecs(wallet.getEarliestKeyCreationTime());
    }
  }

  /**
   * Get the next available change address
   *
   * @return changeAddress The next change address as an Address
   */
  public Address getNextChangeAddress() {

    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().isPresent());
    Preconditions.checkNotNull(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet());
    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet().getKeychainSize() > 0);

    Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();

    return wallet.freshKey(KeyChain.KeyPurpose.CHANGE).toAddress(networkParameters);

  }

  /**
   * Ping all connected peers to see if there is an active network connection
   *
   * @return true is one or more peers respond to the ping
   */
  public boolean pingPeers() {

    List<Peer> connectedPeers = peerGroup.getConnectedPeers();
    boolean atLeastOnePingWorked = false;
    if (connectedPeers != null) {
      for (Peer peer : connectedPeers) {

        log.debug("Ping: {}", peer.getAddress().toString());

        try {
          ListenableFuture<Long> result = peer.ping();
          result.get(4, TimeUnit.SECONDS);
          atLeastOnePingWorked = true;
          break;
        } catch (ProtocolException | InterruptedException | ExecutionException | TimeoutException e) {
          log.warn("Peer '" + peer.getAddress().toString() + "' failed ping test. Message was " + e.getMessage());
        }
      }
    }

    return atLeastOnePingWorked;
  }

  /**
   * @return True if at least one of the MainNet DNS seeds can be reached without error
   */
  private boolean isNetworkPresent() {

    final String[] dnsSeeds = networkParameters.getDnsSeeds();

    // Attempt to lookup each address - first success indicates working network
    for (String dnsSeed : dnsSeeds) {
      try {
        if (InetAddress.getAllByName(dnsSeed) != null) {
          return true;
        }
      } catch (UnknownHostException e) {
        log.warn("Could not resolve '{}'", dnsSeed);
      }
    }

    // All DNS seeds failed
    return false;

  }

  /**
   * Removes the current wallet from the block chain and closes the block store
   */
  private void closeBlockstore() {

    // Remove the wallet from the block chain
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent() && blockChain != null) {
      blockChain.removeWallet(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet());
    }

    // Close the blockstore
    if (blockStore != null) {
      try {
        blockStore.close();
      } catch (BlockStoreException e) {
        log.error("Blockstore not closed successfully: {}", e.getMessage(), e);
      }
    }

  }

  /**
   * Closes the wallet
   */
  private void closeWallet() {

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent() && blockChain != null) {
      WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet().shutdownAutosaveAndWait();
    }

  }

  /**
   * <p>Stops the current peer group blocking until complete</p>
   */
  private void stopPeerGroup() {

    if (peerGroup != null) {
      log.debug("Stopping peerGroup service...");
      peerGroup.removeEventListener(peerEventListener);

      // Remove the wallet from the peer group
      if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
        peerGroup.removeWallet(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet());
      }

      peerGroup.stopAsync();
      log.debug("Service peerGroup stopped");
    }

  }

  /**
   * Restart the network, using the current wallet (specifically the blockstore)
   *
   * @throws BlockStoreException                   If the block store fails
   * @throws IOException                           If the network fails
   * @throws java.util.concurrent.TimeoutException If the TOR connection fails
   */
  private void restartNetwork() throws BlockStoreException, IOException, TimeoutException {

    requireFixedThreadPoolExecutor(5, "bitcoin-network");

    // Check if there is a network connection
    if (!isNetworkPresent()) {
      return;
    }

    CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newNetworkNotInitialised());

    log.debug("Creating block chain ...");
    blockChain = new BlockChain(networkParameters, blockStore);

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();
      blockChain.addWallet(wallet);
    }
    log.debug("Created block chain '{}' with height '{}'", blockChain, blockChain.getBestChainHeight());

    log.debug("Creating peer group ...");
    createNewPeerGroup();
    log.debug("Created peer group '{}'", peerGroup);

    log.debug("Starting peer group ...");
    peerGroup.startAsync();
    log.debug("Started peer group.");

    startedOk = true;
  }

  /**
   * <p>Save the current wallet to application directory, create a rolling backup and a cloud backup</p>
   */
  private void saveWallet() {

    // Save the current wallet immediately
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {

      WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();
      WalletId walletId = walletSummary.getWalletId();
      log.debug("Saving wallet with id '" + walletId + "'.");

      try {
        File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
        File currentWalletFile = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get();
        walletSummary.getWallet().saveToFile(currentWalletFile);
        File encryptedAESCopy = EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(currentWalletFile, walletSummary.getPassword());
        log.debug("Created AES encrypted wallet as file '{}', size {}", encryptedAESCopy == null ? "null" : encryptedAESCopy.getAbsolutePath(),
          encryptedAESCopy == null ? "null" : encryptedAESCopy.length());

        BackupManager.INSTANCE.createRollingBackup(walletSummary, walletSummary.getPassword());
        BackupManager.INSTANCE.createLocalAndCloudBackup(walletId, walletSummary.getPassword());
      } catch (IOException ioe) {
        log.error("Could not write wallet and backups for wallet with id '" + walletId + "' successfully. The error was '" + ioe.getMessage() + "'");
      }
    }
  }
}