package org.multibit.hd.core.managers;

import org.bitcoinj.core.CheckpointManager;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

// TODO (JB) This class is not used - delete it?
public class MultiBitCheckpointManager extends CheckpointManager {


  public MultiBitCheckpointManager(NetworkParameters params, String checkpointFilename) throws IOException {

       // The created fileInputStream is closed in the super.
       super(params, new FileInputStream(checkpointFilename));
   }

    /**
     * Returns a {@link org.bitcoinj.core.StoredBlock} representing the last managers before the given block height, for example, normally
     * you would want to know the managers before the last block the wallet had seen.
     */
    public StoredBlock getCheckpointBeforeOrAtHeight(int height) {
        Map.Entry<Long, StoredBlock> highestCheckpointBeforeHeight = null;

        for (Map.Entry<Long, StoredBlock> loop : checkpoints.entrySet()) {
            if (loop.getValue().getHeight() < height) {
                // This managers is before the specified height.
                if (highestCheckpointBeforeHeight == null) {
                    highestCheckpointBeforeHeight = loop;
                } else {
                    if (highestCheckpointBeforeHeight.getValue().getHeight() < loop.getValue().getHeight()) {
                        // This entry is later.
                        highestCheckpointBeforeHeight = loop;
                    }
                }
            }
        }

        if (highestCheckpointBeforeHeight == null) {
            try {
                return new StoredBlock(params.getGenesisBlock(), params.getGenesisBlock().getWork(), 0);
            } catch (VerificationException e) {
                e.printStackTrace();
            }
        }
        return highestCheckpointBeforeHeight.getValue();
    }
}