package org.multibit.hd.core.managers;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoinj.core.CheckpointManager;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.joda.time.DateTime;
import org.multibit.commons.utils.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

/**
 * <p>Manager to provide the following to BitcoinNetworkService:</p>
 * <ul>
 * <li>Create a blockstore</li>
 * </ul>
 */
public class BlockStoreManager {

  // TODO copy checkpoints file from installation directory if does not exist
  // TODO use bigger of managers files if installed version is larger

  private static final Logger log = LoggerFactory.getLogger(BlockStoreManager.class);

  private final NetworkParameters networkParameters;

  /**
   * @param networkParameters The Bitcoin network parameters
   */
  public BlockStoreManager(NetworkParameters networkParameters) {

    Preconditions.checkNotNull(networkParameters, "'networkParameters' must be present");

    this.networkParameters = networkParameters;
  }

  /**
   * @param blockStoreFile  The file to use for the block store
   * @param checkpointsFile The file to use for the checkpoints
   * @param checkpointDate  Date to checkpoint the blockstore from
   * @param createNew       if true then create a new BlockStore
   *
   * @return The created blockStore
   *
   * @throws BlockStoreException
   * @throws IOException
   */
  @SuppressFBWarnings({"DM_GC"})
  public BlockStore createOrOpenBlockStore(File blockStoreFile, File checkpointsFile, DateTime checkpointDate, boolean createNew) throws BlockStoreException, IOException {

    boolean blockStoreCreatedNew = deleteBlockStoreIfRequired(createNew, blockStoreFile);

    log.debug("Get or create SPV block store:\n'{}'", blockStoreFile.getAbsolutePath());
    BlockStore blockStore;
    try {
      blockStore = new SPVBlockStore(networkParameters, blockStoreFile);
    } catch (BlockStoreException bse) {
      try {
        log.warn("Failed to get or create SPV block store", bse.getMessage());
        // If the block store creation failed, delete the block store file and try again.
        blockStoreCreatedNew = deleteBlockStoreIfRequired(createNew, blockStoreFile);

        blockStore = new SPVBlockStore(networkParameters, blockStoreFile);
      } catch (BlockStoreException bse2) {
        log.error("Unrecoverable failure in opening block store. This is bad.", bse2.getMessage());
        // Throw the exception so that it is indicated on the UI
        throw bse2;
      }
    }

    log.debug("Block store in place. Created new: {}", blockStoreCreatedNew);

    // Load the existing checkpoint file and checkpoint from today.
    if (checkpointsFile.exists()) {

      log.debug("Checkpoints exist attempting to stream from:\n'{}'", checkpointsFile.getAbsolutePath());

      try (FileInputStream checkpointsInputStream = new FileInputStream(checkpointsFile)) {

        Preconditions.checkNotNull(checkpointsInputStream, "'stream' must be present");

        if (checkpointDate == null) {
          if (blockStoreCreatedNew) {
            // Brand new block store
            CheckpointManager.checkpoint(networkParameters, checkpointsInputStream, blockStore, Dates.nowInSeconds());
          }
        } else {
          // Use manager's date (block replay).
          CheckpointManager.checkpoint(networkParameters, checkpointsInputStream, blockStore, checkpointDate.getMillis() / 1000);
        }
      }
    }

    return blockStore;
  }

  /**
   * @param blockStoreFile  The file to use for the block store
   * @param storedBlockStack   Snippet of chain to use
   * @param createNew       if true then create a new BlockStore
   *
   * @return The created blockStore
   *
   * @throws BlockStoreException
   * @throws IOException
   */
  @SuppressFBWarnings({"DM_GC"})
  public BlockStore createOrOpenBlockStore(File blockStoreFile, Stack<StoredBlock> storedBlockStack, boolean createNew) throws BlockStoreException, IOException {

    boolean blockStoreCreatedNew = deleteBlockStoreIfRequired(createNew, blockStoreFile);

    log.debug("Get or create SPV block store:\n'{}'", blockStoreFile.getAbsolutePath());
    log.debug("StoredBlockStack is size {}", storedBlockStack == null ? 0 : storedBlockStack.size());
    BlockStore blockStore;
    try {
      blockStore = new SPVBlockStore(networkParameters, blockStoreFile);

      if (storedBlockStack != null && !storedBlockStack.isEmpty()) {
        StoredBlock loopStoredBlock = null;
        while (!storedBlockStack.isEmpty()) {
          loopStoredBlock = storedBlockStack.pop();
          blockStore.put(loopStoredBlock);
        }

        // Set the chain head to be the first StoredBlock
        blockStore.setChainHead(loopStoredBlock);
      }
    } catch (BlockStoreException bse) {
      try {
        log.warn("Failed to get or create SPV block store", bse.getMessage());
        // If the block store creation failed, try to delete the block store file and try again.
        blockStoreCreatedNew = deleteBlockStoreIfRequired(createNew, blockStoreFile);

        blockStore = new SPVBlockStore(networkParameters, blockStoreFile);
        if (storedBlockStack != null && !storedBlockStack.isEmpty()) {
          StoredBlock loopStoredBlock = null;
          while (!storedBlockStack.isEmpty()) {
            loopStoredBlock = storedBlockStack.pop();
            blockStore.put(loopStoredBlock);
          }

          // Set the chain head to be the first StoredBlock
          blockStore.setChainHead(loopStoredBlock);
        }
      } catch (BlockStoreException bse2) {
        log.error("Unrecoverable failure in opening block store. This is bad.", bse2.getMessage());
        // Throw the exception so that it is indicated on the UI
        throw bse2;
      }
    }

    log.debug("Block store in place. Created new: {}", blockStoreCreatedNew);

    return blockStore;
  }

  private boolean deleteBlockStoreIfRequired(boolean createNew, File blockStoreFile) {
    // If the spvBlockStore is to be created new
    // or its size is 0 bytes delete the file so that it is recreated fresh
    // (fix for MultiBit Classic issue #165)
    if (createNew || blockStoreFile.length() == 0) {
      // Garbage collect any closed references to the block store file (required on Windows)
      System.gc();
      if (blockStoreFile.exists()) {
        boolean isWritable = blockStoreFile.setWritable(true);
        boolean isDeletedOk = blockStoreFile.delete();
        log.debug("Deleting SPV block store (pass 1) from file:\n'{}'", blockStoreFile.getAbsolutePath());
        log.debug("isWritable: '{}' isDeletedOK: '{}'", isWritable, isDeletedOk);
      }
      return true;

    } else {
      return false;
    }
  }
}
