package org.multibit.hd.core.services;

import com.google.bitcoin.core.*;
import com.google.bitcoin.net.discovery.DnsDiscovery;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import org.multibit.hd.core.api.BitcoinNetworkSummary;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.BitcoinSentEvent;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.TransactionCreationEvent;
import org.multibit.hd.core.managers.BlockStoreManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.MultiBitCheckpointManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.network.MultiBitPeerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.File;
import java.math.BigInteger;
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

  public static final BigInteger DEFAULT_FEE_PER_KB = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE; // Currently 10,000 satoshi
  public static final MainNetParams NETWORK_PARAMETERS = MainNetParams.get();
  public static final int MAXIMUM_NUMBER_OF_PEERS = 6;
  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkService.class);
  private WalletManager walletManager;
  private BlockStore blockStore;
  private PeerGroup peerGroup;  // May need to add listener as in MultiBitPeerGroup
  private BlockChain blockChain;
  private MultiBitCheckpointManager checkpointManager;
  private MultiBitPeerEventListener peerEventListener;

  private NetworkParameters MAINNET = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);

  @Override
  public void start() {
    CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newNetworkNotInitialised());

    requireSingleThreadExecutor();

    // Get the wallet manager.
    walletManager = WalletManager.INSTANCE;

    try {
      String walletRoot = WalletManager.INSTANCE.getCurrentWalletFilename().get().getParentFile().getAbsolutePath();
      String blockchainFilename = walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX;
      String checkpointsFilename = walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX;

      // Load or create the blockStore..
      log.debug("Loading/ creating blockstore ...");
      blockStore = BlockStoreManager.createBlockStore(blockchainFilename, checkpointsFilename, null, false);
      log.debug("Blockstore is '{}'", blockStore);

      log.debug("Creating blockchain ...");
      blockChain = new BlockChain(NETWORK_PARAMETERS, blockStore);
      if (walletManager.getCurrentWalletData().isPresent()) {
        blockChain.addWallet(walletManager.getCurrentWalletData().get().getWallet());
      }
      log.debug("Created blockchain '{}' with height '{}'", blockChain, blockChain.getBestChainHeight());

      log.debug("Creating peergroup ...");
      createNewPeerGroup();
      log.debug("Created peergroup '{}'", peerGroup);

      log.debug("Starting peergroup ...");
      peerGroup.start();
      log.debug("Started peergroup.");

      log.debug("Creating checkpointmanager");
      checkpointManager = new MultiBitCheckpointManager(NETWORK_PARAMETERS, checkpointsFilename);
      log.debug("Created checkpointmanager");

    } catch (Exception e) {
      log.error(e.getClass().getName() + " " + e.getMessage());
      CoreEvents.fireBitcoinNetworkChangedEvent(
              BitcoinNetworkSummary.newNetworkStartupFailed(
                      MessageKey.START_NETWORK_CONNECTION_ERROR,
                      Optional.<Object[]>absent()
              ));
    }
  }

  @Override
  public void stopAndWait() {
    if (peerGroup != null) {
      log.debug("Stopping peerGroup service...");
      peerGroup.removeEventListener(peerEventListener);

      if (walletManager.getCurrentWalletData().isPresent()) {
        peerGroup.removeWallet(walletManager.getCurrentWalletData().get().getWallet());
      }

      peerGroup.stopAndWait();
      log.debug("Service peerGroup stopped");
    }

    // Shutdown any executor running a download
    if (getExecutorService() != null) {
      getExecutorService().shutdown();
    }

    // Remove the wallet from the blockChain
    if (walletManager.getCurrentWalletData().isPresent()) {
      blockChain.removeWallet(walletManager.getCurrentWalletData().get().getWallet());
    }

    // Close the blockstore
    if (blockStore != null) {
      try {
        blockStore.close();
      } catch (BlockStoreException e) {
        log.error("Blockstore not closed successfully, error was '" + e.getClass().getName() + " " + e.getMessage() + "'");
      }
    }
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
   * @param destinationAddress The destination address to send to
   * @param amount             The amount to send (in satoshis)
   * @param changeAddress      The change address
   * @param feePerKB           The fee per Kb (in satoshis)
   * @param password           The wallet password
   */
  public void send(String destinationAddress, BigInteger amount, String changeAddress, BigInteger feePerKB, CharSequence password) {
    if (!walletManager.getCurrentWalletData().isPresent()) {
      // Declare the transaction creation a failure - no wallet
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(null, amount, BigInteger.ZERO, destinationAddress, changeAddress,
              false, "no_active_wallet", new String[]{""}));
      return;
    }

    log.debug("Just about to create send transaction");
    Wallet wallet = walletManager.getCurrentWalletData().get().getWallet();
    KeyParameter aesKey = wallet.getKeyCrypter().deriveKey(password);

    Wallet.SendRequest sendRequest = null;
    boolean transactionCreatedOk = false;
    try {
      sendRequest = Wallet.SendRequest.to(new Address(MAINNET, destinationAddress), amount);
      sendRequest.aesKey = aesKey;
      sendRequest.fee = BigInteger.ZERO;
      sendRequest.feePerKb = feePerKB;
      sendRequest.changeAddress = new Address(MAINNET, changeAddress);

      // Complete it (works out fee and signs tx)
      wallet.completeTx(sendRequest);

      // Commit to the wallet
      wallet.commitTx(sendRequest.tx);
      transactionCreatedOk = true;
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(sendRequest.tx.getHashAsString(), amount, BigInteger.ZERO, destinationAddress, changeAddress,
              true, null, null));
    } catch (InsufficientMoneyException | VerificationException | AddressFormatException e1) {
      String message = e1.getMessage();
      log.error(message);

      // Declare the transaction creation a failure
      String transactionId = null;
      if (sendRequest.tx != null) {
        transactionId = sendRequest.tx.getHashAsString();
      }
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(transactionId, amount, BigInteger.ZERO, destinationAddress, changeAddress,
              false, "the_error_was", new String[]{message}));
    }

    log.debug("Just about to broadcast transaction");
    if (transactionCreatedOk) {
      try {
        // Ping the peers to check the bitcoin network connection
        if (!pingPeers()) {
          // Declare the send a failure
          CoreEvents.fireBitcoinSentEvent(new BitcoinSentEvent(amount, BigInteger.ZERO, destinationAddress, changeAddress,
                  false, "could_not_connect_to_bitcoin_network", new String[]{"All pings failed"}));
          return;
        }

        // Broadcast
        peerGroup.broadcastTransaction(sendRequest.tx);

        log.debug("Just broadcast transaction '" + Utils.bytesToHexString(sendRequest.tx.bitcoinSerialize()) + "'");

        // Declare the send a success
        CoreEvents.fireBitcoinSentEvent(new BitcoinSentEvent(amount, BigInteger.ZERO, destinationAddress, changeAddress,
                true, "bitcoin_sent_ok", null));
      } catch (VerificationException e1) {
        String message = e1.getMessage();
        log.error(message);

        // Declare the send a failure
        CoreEvents.fireBitcoinSentEvent(new BitcoinSentEvent(amount, BigInteger.ZERO, destinationAddress, changeAddress,
                false, "the_error_was", new String[]{message}));
      }

      log.debug("Send coins has completed");
    } else {
      log.debug("Not attempting to send the transaction as it was not constructed correctly.");
    }
  }

  /**
   * <p>Download the block chain</p>
   */
  public void downloadBlockChain() {
    getExecutorService().submit(new Runnable() {
      @Override
      public void run() {
        Preconditions.checkNotNull(peerGroup, "'peerGroup' must be present");
        log.debug("Downloading blockchain");

        // Issue a "network change" event
        CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newChainDownloadStarted());

        // Method will block until download completes
        peerGroup.downloadBlockChain();

        // Indicate 100% progress
        CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newChainDownloadProgress(100));

        // Issue a "network ready" event
        CoreEvents.fireBitcoinNetworkChangedEvent(
                BitcoinNetworkSummary.newNetworkReady(
                        peerEventListener.getNumberOfConnectedPeers()
                ));

      }
    });
  }

  /**
   * <p>Create a new peer group</p>
   */
  private void createNewPeerGroup() {
    peerGroup = new PeerGroup(NETWORK_PARAMETERS, blockChain);
    peerGroup.setFastCatchupTimeSecs(0); // genesis block
    peerGroup.setUserAgent(InstallationManager.MBHD_APP_NAME, Configurations.APP_VERSION);
    peerGroup.setMaxConnections(MAXIMUM_NUMBER_OF_PEERS);

    peerGroup.addPeerDiscovery(new DnsDiscovery(NETWORK_PARAMETERS));

    peerEventListener = new MultiBitPeerEventListener();
    peerGroup.addEventListener(peerEventListener);

    if (walletManager.getCurrentWalletData().isPresent()) {
      peerGroup.addWallet(walletManager.getCurrentWalletData().get().getWallet());
    }
  }

  /**
   * Get the next available change address
   * TODO- this should be worked out deterministically but just use the first address on the current wallet for now
   *
   * @return changeAddress The next change address as a string
   */
  public String getNextChangeAddress() {
    Preconditions.checkState(walletManager.getCurrentWalletData().isPresent());
    Preconditions.checkNotNull(walletManager.getCurrentWalletData().get().getWallet());
    Preconditions.checkState(walletManager.getCurrentWalletData().get().getWallet().getKeychainSize() > 0);

    Wallet wallet = walletManager.getCurrentWalletData().get().getWallet();
    ECKey firstKey = wallet.getKeys().get(0);
    return firstKey.toAddress(MAINNET).toString();
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
}