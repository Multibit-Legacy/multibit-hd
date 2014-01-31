package org.multibit.hd.core.managers;

import com.google.bitcoin.core.CheckpointManager;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.SPVBlockStore;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 *  <p>Manager to provide the following to BitcoinNetworkService:<br>
 *  <ul>
 *  <li>Create a blockstore</li>
 *  </ul>
 */
public class BlockStoreManager {
  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkService.class);

  /**
   * @param blockchainFilename
   * @param checkpointsFilename
   * @param checkpointDate      Date to checkpoint the blockstore from
   * @param createNew           if true then create a new block store
   * @return The created blockStore
   * @throws BlockStoreException
   * @throws IOException
   */
  public static BlockStore createBlockStore(String blockchainFilename, String checkpointsFilename, Date checkpointDate, boolean createNew) throws BlockStoreException, IOException {
    Preconditions.checkState((new File(checkpointsFilename)).exists());
    BlockStore blockStore;

    File blockStoreFile = new File(blockchainFilename);
    boolean blockStoreCreatedNew = !blockStoreFile.exists();

    // Ensure there is a checkpoints file.
    File checkpointsFile = new File(checkpointsFilename);
    // TODO copy checkpoints file from installation directory if does not exist
    // TODO use bigger of managers files if installed version is larger

    // If the spvBlockStore is to be created new
    // or its size is 0 bytes delete the file so that it is recreated fresh (fix for MultiBit classic issue 165).
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
      blockStore = new SPVBlockStore(BitcoinNetworkService.NETWORK_PARAMETERS, blockStoreFile);
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

        blockStore = new SPVBlockStore(BitcoinNetworkService.NETWORK_PARAMETERS, blockStoreFile);
      } catch (BlockStoreException bse2) {
        bse2.printStackTrace();
        log.error("Unrecoverable failure in opening block store. This is bad.");
        // Throw the exception so that it is indicated on the UI.
        throw bse2;
      }
    }

    // Load the existing checkpoint file and checkpoint from today.
    if (blockStore != null && checkpointsFile.exists()) {
      try (FileInputStream stream = new FileInputStream(checkpointsFile)) {
        if (checkpointDate == null) {
          if (blockStoreCreatedNew) {
            // Brand new block store - managers from today. This
            // will go back to the last managers.
            CheckpointManager.checkpoint(BitcoinNetworkService.NETWORK_PARAMETERS, stream, blockStore, (new Date()).getTime() / 1000);
          }
        } else {
          // Use managers date (block replay).
          CheckpointManager.checkpoint(BitcoinNetworkService.NETWORK_PARAMETERS, stream, blockStore, checkpointDate.getTime() / 1000);
        }
      }
    }
    return blockStore;
  }
}
