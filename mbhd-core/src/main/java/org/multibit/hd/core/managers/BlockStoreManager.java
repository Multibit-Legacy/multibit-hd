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
 *  <p>Manager to provide the following to BitcoinNetworkService:</p>
 *  <ul>
 *  <li>Create a blockstore</li>
 *  </ul>
 */
public class BlockStoreManager {

  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkService.class);

  /**
   * @param blockStoreFile  The file to use for the block store
   * @param checkpointsFile The file to use for the checkpoints
   * @param checkpointDate  Date to checkpoint the blockstore from
   * @param createNew       if true then create a new block writeContacts
   *
   * @return The created blockStore
   *
   * @throws BlockStoreException
   * @throws IOException
   */
  public static BlockStore createBlockStore(File blockStoreFile, File checkpointsFile, Date checkpointDate, boolean createNew) throws BlockStoreException, IOException {

    boolean blockStoreCreatedNew = !blockStoreFile.exists();

    // TODO copy checkpoints file from installation directory if does not exist
    // TODO use bigger of managers files if installed version is larger

    // If the spvBlockStore is to be created new
    // or its size is 0 bytes delete the file so that it is recreated fresh
    // (fix for MultiBit Classic issue #165)
    if (createNew || blockStoreFile.length() == 0) {

      // Garbage collect any closed references to the block store file (required on Windows)
      System.gc();
      if (blockStoreFile.exists()) {
        boolean isWritable = blockStoreFile.setWritable(true);
        boolean isDeletedOk = blockStoreFile.delete();
        log.debug("Deleting SPV block store (first pass).\nFilename: '{}' isWritable: '{}' isDeletedOK: '{}'", blockStoreFile.getAbsolutePath(), isWritable, isDeletedOk);
      }
      blockStoreCreatedNew = true;

    }

    log.debug("Get or create SPV block store '{}'", blockStoreFile.getAbsolutePath());
    BlockStore blockStore;
    try {
      blockStore = new SPVBlockStore(BitcoinNetworkService.NETWORK_PARAMETERS, blockStoreFile);
    } catch (BlockStoreException bse) {

      try {
        log.warn("Failed to get or create SPV block store");
        // If the block store creation failed, delete the block store file and try again.

        // Garbage collect any closed references to the block store file (required on Windows)
        System.gc();
        boolean isWritable = blockStoreFile.setWritable(true);
        boolean isDeletedOk = blockStoreFile.delete();
        log.debug("Deleting SPV block store (second pass).\nFilename: '{}' isWritable: '{}' isDeletedOK: '{}'", blockStoreFile.getAbsolutePath(), isWritable, isDeletedOk);
        blockStoreCreatedNew = true;

        blockStore = new SPVBlockStore(BitcoinNetworkService.NETWORK_PARAMETERS, blockStoreFile);
      } catch (BlockStoreException bse2) {
        log.error("Unrecoverable failure in opening block store. This is bad.");
        // Throw the exception so that it is indicated on the UI
        throw bse2;
      }
    }

    log.debug("Block store in place. Created new: {}", blockStoreCreatedNew);

    // Load the existing checkpoint file and checkpoint from today.
    if (checkpointsFile.exists()) {

      log.debug("Checkpoints exist attempting to stream from '{}'", checkpointsFile.getAbsolutePath());

      try (FileInputStream checkpointsInputStream = new FileInputStream(checkpointsFile)) {

        Preconditions.checkNotNull(checkpointsInputStream, "'stream' must be present");

        if (checkpointDate == null) {
          if (blockStoreCreatedNew) {
            // Brand new block store - managers from today. This
            // will go back to the last managers.
            CheckpointManager.checkpoint(BitcoinNetworkService.NETWORK_PARAMETERS, checkpointsInputStream, blockStore, (new Date()).getTime() / 1000);
          }
        } else {
          // Use managers date (block replay).
          CheckpointManager.checkpoint(BitcoinNetworkService.NETWORK_PARAMETERS, checkpointsInputStream, blockStore, checkpointDate.getTime() / 1000);
        }
      }
    }

    return blockStore;

  }
}
