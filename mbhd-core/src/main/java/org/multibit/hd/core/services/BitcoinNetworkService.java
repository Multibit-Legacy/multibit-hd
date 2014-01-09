package org.multibit.hd.core.services;

import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.net.discovery.DnsDiscovery;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.api.BitcoinNetworkSummary;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.managers.BlockStoreManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.MultiBitCheckpointManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.network.MultiBitPeerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigInteger;

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
public class BitcoinNetworkService extends AbstractService implements ManagedService {

  public static final MainNetParams NETWORK_PARAMETERS = MainNetParams.get();
  public static final int MAXIMUM_NUMBER_OF_PEERS = 6;
  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkService.class);
  private WalletManager walletManager;
  private BlockStore blockStore;
  private PeerGroup peerGroup;  // May need to add listener as in MultiBitPeerGroup
  private BlockChain blockChain;
  private MultiBitCheckpointManager checkpointManager;
  private MultiBitPeerEventListener peerEventListener;

  @Override
  public void start() {

    CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newNetworkNotInitialised());

    requireSingleThreadExecutor();

    // Get the wallet manager.
    walletManager = WalletManager.INSTANCE;

    try {
      String walletRoot = WalletManager.INSTANCE.getCurrentWalletDirectory().get().getAbsolutePath();
      String blockchainFilename = walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX;
      String checkpointsFilename = walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX;

      // Load or create the blockStore..
      log.debug("Loading/ creating blockstore ...");
      blockStore = BlockStoreManager.createBlockStore(blockchainFilename, checkpointsFilename, null, false);
      log.debug("Blockstore is '{}'", blockStore);

      log.debug("Creating blockchain ...");
      blockChain = new BlockChain(NETWORK_PARAMETERS, blockStore);
      if (walletManager.getCurrentWallet().isPresent()) {
        blockChain.addWallet(walletManager.getCurrentWallet().get());
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

      if (walletManager.getCurrentWallet().isPresent()) {
          peerGroup.removeWallet(walletManager.getCurrentWallet().get());
        }

      peerGroup.stopAndWait();
      log.debug("Service peerGroup stopped");
    }

    // Shutdown any executor running a download
    if (getExecutorService() != null) {
      getExecutorService().shutdown();
    }

    // Remove the wallet from the blockChain
    if (walletManager.getCurrentWallet().isPresent()) {
      blockChain.removeWallet(walletManager.getCurrentWallet().get());
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
   * <p>The result of the operation is sent to the UIEventBus as a BitcoinSentEvent</p>
   *
   * @param sendAddress   The send address
   * @param sendAmount    The amount to send (in satoshis)
   * @param changeAddress The change address
   * @param feePerKB      The fee per Kb (in satoshis)
   * @param password      The wallet password
   */
  public void send(String sendAddress, BigInteger sendAmount, String changeAddress, BigInteger feePerKB, CharSequence password) {

  }

  /**
   * <p>Download the block chain</p>
   */
  public void downloadBlockChain() {
 //   getExecutorService().submit(new Runnable() {
 //     @Override
 //     public void run() {

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
//    });
//  }

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

    if (walletManager.getCurrentWallet().isPresent()) {
      peerGroup.addWallet(walletManager.getCurrentWallet().get());
    }
  }
}