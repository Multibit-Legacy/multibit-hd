package org.multibit.hd.core.blockexplorer;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

/**
 *  <p>Factory to provide the following to [related classes]:<br>
 *  <ul>
 *  <li>Provide block explorer information for lookup of transaction data for various blockexplorers</li>
 *  </ul>
 *  Example:<br>
 *  <pre>TransactionDetailPanelView
 *  </pre>
 *  </p>
 *  
 */
public class BlockExplorers {

  private static final List<BlockExplorer> allBlockExplorers = Lists.newArrayList();

  static {
    // Add all block explorers in alphabetic order
    allBlockExplorers.add(new BitEasyComBlockExplorer());
    allBlockExplorers.add(new BitPayComBlockExplorer());
    allBlockExplorers.add(new BlockChainInfoBlockExplorer());
    allBlockExplorers.add(new BlockrIoBlockExplorer());
    allBlockExplorers.add(new BlockTrailComBlockExplorer());
  }

  /**
   * Utility classes have private constructors
   */
  private BlockExplorers() {
  }

  public static List<BlockExplorer> getAll() {
    return allBlockExplorers;
  }

  /**
   * Get the blockexplorer for the given id.
   * @param id identifier of blockexplorer
   * @return the blockexplorer with this id. May return Optional.absent().
   */
  public static Optional<BlockExplorer> getBlockExplorerById(String id) {
    Preconditions.checkNotNull(id);

    for (BlockExplorer blockExplorer : allBlockExplorers) {
      if (blockExplorer.getId().equals(id)) {
        return Optional.of(blockExplorer);
      }
    }
    return Optional.absent();
  }

  /**
   * Get the default block explorer
   */
  public static BlockExplorer getDefaultBlockExplorer() {
    return new BlockChainInfoBlockExplorer();
  }
}
