package org.multibit.hd.core.events;

import org.multibit.hd.core.dto.CoreMessageKey;

import java.util.Arrays;

/**
 *  <p>Event to provide the following to UI event subscribers:</p>
 *  <ul>
 *  <li>Success or failure of change of a wallet password</li>
 *  </ul>
 */
public class ChangePasswordResultEvent implements CoreEvent {

  private final boolean changePasswordWasSuccessful;

  private final CoreMessageKey changePasswordResultKey;

  private final Object[] changePasswordResultData;

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
