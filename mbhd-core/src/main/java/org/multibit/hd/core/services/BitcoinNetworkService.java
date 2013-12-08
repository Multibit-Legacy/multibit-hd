package org.multibit.hd.core.services;

import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.common.base.Optional;
import org.multibit.hd.core.api.BitcoinNetworkSummary;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.managers.BlockStoreManager;
import org.multibit.hd.core.managers.MultiBitCheckpointManager;
import org.multibit.hd.core.utils.MultiBitFiles;
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
 *
 * @since 0.0.1
 *         
 */
public class BitcoinNetworkService extends AbstractService implements ManagedService {

  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkService.class);

  public static final MainNetParams NETWORK_PARAMETERS = MainNetParams.get();
  private String applicationDataDirectoryName;

  private BlockStore blockStore;
  private PeerGroup peerGroup;  // May need to add listener as in MultiBitPeerGroup

  private BlockChain blockChain;

  private MultiBitCheckpointManager checkpointManager;

  private BitcoinNetworkSummary networkSummary;


  @Override
  public void start() {
    networkSummary = BitcoinNetworkSummary.newNetworkNotInitialised();
    requireSingleThreadExecutor();

    try {
      applicationDataDirectoryName = MultiBitFiles.createApplicationDataDirectory();
    } catch (IllegalStateException ise) {
      networkSummary = BitcoinNetworkSummary.newNetworkStartupFailed("bitcoin-network.configuration-error", Optional.<String[]>absent());
      return;
    }

    // Get the current wallet
    String currentWalletFilename = Configurations.currentConfiguration.getApplicationConfiguration().getCurrentWalletFilename();
    log.debug("The current wallet filename is '" + currentWalletFilename + "'");

    String currentWalletDirectoryName = applicationDataDirectoryName + File.separator + currentWalletFilename;
    File currentWalletDirectory = new File(currentWalletDirectoryName);

    if ("".equals(currentWalletFilename.trim()) || !currentWalletDirectory.exists() || !currentWalletDirectory.isDirectory()) {
      networkSummary = BitcoinNetworkSummary.newNetworkStartupFailed("bitcoin-network.wallet-directory-error", Optional.of(new String[]{currentWalletDirectoryName}));
      return;
    }

    try {
      String filenameRoot = currentWalletDirectoryName + File.separator + MultiBitFiles.MBHD_PREFIX +;
      String blockchainFilename =  filenameRoot + MultiBitFiles.SPV_BLOCKCHAIN_SUFFIX;
      String checkpointsFilename = filenameRoot + MultiBitFiles.CHECKPOINTS_SUFFIX;

      // Load or create the blockStore..
      log.debug("Loading/ creating blockstore ...");
      blockStore = BlockStoreManager.createBlockStore(blockchainFilename, checkpointsFilename, null, false);
      log.debug("Blockstore is '" + blockStore + "'");

      log.debug("Creating blockchain ...");
      blockChain = new BlockChain(NETWORK_PARAMETERS, blockStore);
      log.debug("Created blockchain '" + blockChain + "' with height " + blockChain.getBestChainHeight());

      log.debug("Creating peergroup ...");
      createNewPeerGroup();
      log.debug("Created peergroup '" + peerGroup + "'");

      log.debug("Starting peergroup ...");
      peerGroup.start();
      log.debug("Started peergroup.");

      log.debug("Creating checkpointmanager");
      checkpointManager = new MultiBitCheckpointManager(NETWORK_PARAMETERS, checkpointsFilename);
      log.debug("Created checkpointmanager");

    } catch (Exception e) {
      log.error(e.getClass().getName() + " " + e.getMessage());
      networkSummary = BitcoinNetworkSummary.newNetworkStartupFailed("bitcoin-network.start-network-connection-error", Optional.<String[]>absent());
    }
  }

  @Override
  public void stopAndWait() {
    // Shutdown any running download.
    super.stopAndWait();

    if (peerGroup != null) {
      log.debug("Stopping peerGroup service...");
      peerGroup.stopAndWait();
      log.debug("Service peerGroup stopped");
    }

    if (blockStore != null) {
      try {
        blockStore.close();
      } catch (BlockStoreException e) {
        log.error("Blockstore not closed successfully, error was '" + e.getClass().getName() + " " + e.getMessage() + "'");
      }
    }
  }

  /**
   * Send bitcoin with the following parameters:
   *
   * @param sendAddress
   * @param sendAmount
   * @param changeAddress
   * @param feePerKB
   * @param password      In the future will also need:
   *                      the wallet to send from - when Trezor comes onstream
   *                      a CoinSelector - when HD subnodes are supported
   *                      <p/>
   *                      The result of the operation is sent to the UIEventBus as a BitcoinSentEvent
   */
  public void send(String sendAddress, BigInteger sendAmount, String changeAddress, BigInteger feePerKB, CharSequence password) {

  }

  public void createNewPeerGroup() {
    peerGroup = new PeerGroup(NETWORK_PARAMETERS, blockChain);
    peerGroup.setFastCatchupTimeSecs(0); // genesis block
    peerGroup.setUserAgent(MultiBitFiles.MBHD_APP_NAME, Configurations.APP_VERSION);

    peerGroup.addPeerDiscovery(new DnsDiscovery(NETWORK_PARAMETERS));

    // TODO Add the controller as a PeerEventListener.
    // peerGroup.addEventListener(bitcoinController.getPeerEventListener());

    // TODO Add the wallet to the PeerGroup.
  }

  /**
   * Download the block chain.
   */
  public void downloadBlockChain() {
    getExecutorService().submit(new Runnable() {
      @Override
      public void run() {
        log.debug("Downloading blockchain");
        peerGroup.downloadBlockChain();
      }
    });
  }

  /**
   * @return A snapshot of the Bitcoin network summary
   */
  public BitcoinNetworkSummary getNetworkSummary() {
    return networkSummary;
  }

}