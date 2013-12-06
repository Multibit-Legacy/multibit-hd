package org.multibit.hd.core.services;

import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.CheckpointManager;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.SPVBlockStore;
import org.multibit.hd.core.api.BitcoinNetworkSummary;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.managers.MultiBitCheckpointManager;
import org.multibit.hd.core.utils.MultiBitFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

/**
 * <p>Service to provide access to the Bitcoin netowrk, including:</p>
 * <ul>
 * <li>Ability to send bitcoin</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BitcoinNetworkService extends AbstractService implements ManagedService {

  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkService.class);

  public final MainNetParams NETWORK_PARAMETERS = MainNetParams.get();
  private String applicationDataDirectoryName;
  private String currentWalletFilename;

  private BlockStore blockStore;
  private PeerGroup peerGroup;  // May need to add listener as in MultiBitPeerGroup

  private String blockchainFilename;

  private BlockChain blockChain;

  private MultiBitCheckpointManager checkpointManager;
  private String checkpointsFilename;


  @Override
  public void initialise() {
    applicationDataDirectoryName = MultiBitFiles.createApplicationDataDirectory();
  }

  @Override
  public void start() {
    // Get the current wallet
    currentWalletFilename = Configurations.currentConfiguration.getApplicationConfiguration().getCurrentWalletFilename();
    log.debug("The current wallet filename is '" + currentWalletFilename + "'");

    // Check the application data directory and current wallet directories exist.
    if (applicationDataDirectoryName == null) {
      throw new IllegalArgumentException("applicationDataDirectory cannot be null");
    }

    File applicationDataDirectory = new File(applicationDataDirectoryName);
    if (!applicationDataDirectory.exists()) {
      throw new IllegalArgumentException("applicationDataDirectory does not exist");
    }
    if (!applicationDataDirectory.isDirectory()) {
      throw new IllegalArgumentException("applicationDataDirectory is not a directory");
    }

    if ("".equals(currentWalletFilename.trim())) {
      log.warn("Not starting BitcoinNetworkService as no wallet filename is available");
    } else {
      String currentWalletDirectoryName = applicationDataDirectoryName + File.separator + currentWalletFilename;
      File currentWalletDirectory = new File(currentWalletDirectoryName);

      if (!currentWalletDirectory.exists()) {
        throw new IllegalArgumentException("currentWalletDirectory does not exist");
      }
      if (!currentWalletDirectory.isDirectory()) {
        throw new IllegalArgumentException("currentWalletDirectory is not a directory");
      }

      try {
        // Load or create the blockStore..
        log.debug("Loading/ creating blockstore ...");
        blockStore = createBlockStore(currentWalletFilename, null, false);
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

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(checkpointsFilename);
            checkpointManager = new MultiBitCheckpointManager(NETWORK_PARAMETERS, stream);
        } catch (IOException e) {
            log.error("Error creating checkpointManager " + e.getClass().getName() + " " + e.getMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.error("Error tidying up checkpointManager creation" + e.getClass().getName() + " " + e.getMessage());
                }
            }
        }
      } catch (Exception e) {
        // TODO hook in error handling
        e.printStackTrace();
      }
      log.debug("Starting service");
    }
  }

  @Override
  public void stopAndWait() {
    if (peerGroup != null) {
      log.debug("Stopping service...");
      peerGroup.stopAndWait();
      log.debug("Service stopped");
    }
    if (blockStore != null) {
      try {
        blockStore.close();
      } catch (BlockStoreException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

  private BlockStore createBlockStore(String walletFilename, Date checkpointDate, boolean createNew) throws BlockStoreException, IOException {
    BlockStore blockStore;

    blockchainFilename = applicationDataDirectoryName + File.separator + walletFilename + File.separator
            + MultiBitFiles.MBHD_PREFIX + MultiBitFiles.SPV_BLOCKCHAIN_SUFFIX;
    checkpointsFilename = applicationDataDirectoryName + File.separator + walletFilename + File.separator
            + MultiBitFiles.MBHD_PREFIX + MultiBitFiles.CHECKPOINTS_SUFFIX;

    File blockStoreFile = new File(blockchainFilename);
    boolean blockStoreCreatedNew = !blockStoreFile.exists();

    // Ensure there is a checkpoints file.
    File checkpointsFile = new File(checkpointsFilename);
    // TODO copy checkpoints file from installation directory if does not exist
    // TODO use bigger of managers files if installed version is larger
    if (!checkpointsFile.exists()) {
      throw new IllegalArgumentException("checkpoints file '" + checkpointsFilename + "' does not exist.");
    }

    // If the spvBlockStore is to be created new
    // or its size is 0 bytes delete the file so that it is recreated fresh (fix for issue 165).
    if (createNew || blockStoreFile.length() == 0) {
      // Garbage collect any closed references to the blockchainFile.
      System.gc();
      blockStoreFile.setWritable(true);
      boolean deletedOk = blockStoreFile.delete();
      log.debug("Deleting SPV block store '{}' from disk.1", blockchainFilename + ", deletedOk = " + deletedOk);
      blockStoreCreatedNew = true;
    }

    log.debug("Opening / Creating SPV block store '{}' from disk", blockchainFilename);
    try {
      blockStore = new SPVBlockStore(NETWORK_PARAMETERS, blockStoreFile);
    } catch (BlockStoreException bse) {
      try {
        log.error("Failed to open/ create SPV block store '{}' from disk", blockchainFilename);
        // If the block store creation failed, delete the block store file and try again.

        // Garbage collect any closed references to the blockchainFile.
        System.gc();
        blockStoreFile.setWritable(true);
        boolean deletedOk = blockStoreFile.delete();
        log.debug("Deleting SPV block store '{}' from disk.2", blockchainFilename + ", deletedOk = " + deletedOk);
        blockStoreCreatedNew = true;

        blockStore = new SPVBlockStore(NETWORK_PARAMETERS, blockStoreFile);
      } catch (BlockStoreException bse2) {
        bse2.printStackTrace();
        log.error("Unrecoverable failure in opening block store. This is bad.");
        // Throw the exception so that it is indicated on the UI.
        throw bse2;
      }
    }

    // Load the existing managers file and managers from today.
    if (blockStore != null && checkpointsFile.exists()) {
      FileInputStream stream = null;
      try {
        stream = new FileInputStream(checkpointsFile);
        if (checkpointDate == null) {
          if (blockStoreCreatedNew) {
            // Brand new block store - managers from today. This
            // will go back to the last managers.
            CheckpointManager.checkpoint(NETWORK_PARAMETERS, stream, blockStore, (new Date()).getTime() / 1000);
          }
        } else {
          // Use managers date (block replay).
          CheckpointManager.checkpoint(NETWORK_PARAMETERS, stream, blockStore, checkpointDate.getTime() / 1000);
        }
      } finally {
        if (stream != null) {
          stream.close();
        }
      }
    }
    return blockStore;
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
      @SuppressWarnings("rawtypes")
      SwingWorker worker = new SwingWorker() {
          @Override
          protected Object doInBackground() throws Exception {
              log.debug("Downloading blockchain");
              peerGroup.downloadBlockChain();
              return null; // return not used
          }
      };
      worker.execute();
  }

  /**
   *
   * @return A snapshot of the Bitcoin network summary
   */
  public BitcoinNetworkSummary getNetworkSummary() {

    // TODO Implement this
    return null;
  }

}