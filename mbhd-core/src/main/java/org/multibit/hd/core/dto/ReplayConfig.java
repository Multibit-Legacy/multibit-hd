package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import org.bitcoinj.core.StoredBlock;
import org.joda.time.DateTime;

import java.util.Stack;

/**
 *  <p>Class to provide the following to BitcoinNetworkService:<br>
 *  <ul>
 *  <li>ReplayDateTime (if required)</li>
 *  <li>StoredBlock to use to replay from (if required)</li>
 *  </ul>
 *  
 */
public class ReplayConfig {

  /**
   * The date time to replay from, or absent
   */
  final Optional<DateTime> replayDate;

  /**
   * The Stack<StoredBlock></StoredBlock> to replay from, or absent
   */
  final Optional<Stack<StoredBlock>> replayStoredBlockStack;

  /**
   * Create a ReplayHints specifying no replayDate nor replayStoredBlock
   */
  public ReplayConfig() {
    replayDate = Optional.absent();
    replayStoredBlockStack = Optional.absent();
  }

  /**
   * Create a ReplayHints with a replayDate (no replayStoredBlock)
   */
  public ReplayConfig(DateTime replayDate) {
    this.replayDate = Optional.fromNullable(replayDate);
    replayStoredBlockStack = Optional.absent();
  }

  /**
   * Create a ReplayHints with a replayStoredBlockStack (no replayDate)
   */
  public ReplayConfig(Stack<StoredBlock> replayStoredBlockStack) {
    replayDate = Optional.absent();
    this.replayStoredBlockStack = Optional.fromNullable(replayStoredBlockStack);
  }

  public Optional<DateTime> getReplayDate() {
    return replayDate;
  }

  public Optional<Stack<StoredBlock>> getReplayStoredBlockStack() {
    return replayStoredBlockStack;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ReplayConfig that = (ReplayConfig) o;

    if (replayDate != null ? !replayDate.equals(that.replayDate) : that.replayDate != null) return false;
    if (replayStoredBlockStack != null ? !replayStoredBlockStack.equals(that.replayStoredBlockStack) : that.replayStoredBlockStack != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = replayDate != null ? replayDate.hashCode() : 0;
    result = 31 * result + (replayStoredBlockStack != null ? replayStoredBlockStack.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ReplayConfig{" +
            "replayDate=" + replayDate +
            ", replayStoredBlockStack=" + replayStoredBlockStack +
            '}';
  }
}
