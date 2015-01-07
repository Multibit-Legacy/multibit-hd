package org.multibit.hd.core.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.multibit.hd.core.dto.CoreMessageKey;

import java.util.Arrays;

/**
 * <p>Event to provide the following to UI event subscribers:</p>
 * <ul>
 * <li>Success or failure of change of a wallet credentials</li>
 * </ul>
 * <p>This is an infrequent event</p>
 */
public class ChangePasswordResultEvent implements CoreEvent {

  private final boolean changePasswordWasSuccessful;

  private final CoreMessageKey changePasswordResultKey;

  /**
   * TODO Consider using List instead
   */
  private final Object[] changePasswordResultData;

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP2"})
  public ChangePasswordResultEvent(
          boolean changePasswordWasSuccessful,
          CoreMessageKey changePasswordResultKey,
          Object[] changePasswordResultData
  ) {

    this.changePasswordWasSuccessful = changePasswordWasSuccessful;
    this.changePasswordResultKey = changePasswordResultKey;
    this.changePasswordResultData = changePasswordResultData;
  }

  public boolean isChangePasswordWasSuccessful() {
    return changePasswordWasSuccessful;
  }

  public CoreMessageKey getChangePasswordResultKey() {
    return changePasswordResultKey;
  }

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP"})
  public Object[] getChangePasswordResultData() {
    return changePasswordResultData;
  }

  @Override
  public String toString() {
    return "ChangePasswordResultEvent{" +
            "changePasswordWasSuccessful=" + changePasswordWasSuccessful +
            ", changePasswordResultKey='" + changePasswordResultKey + '\'' +
            ", changePasswordResultData=" + Arrays.toString(changePasswordResultData) +
            '}';
  }
}
