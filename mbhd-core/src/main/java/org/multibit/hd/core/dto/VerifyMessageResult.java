package org.multibit.hd.core.dto;

/**
 * <p>DTO describing success or failure of signing a message

 */
public class VerifyMessageResult {

  private final boolean verifyWasSuccessful;

  private final CoreMessageKey verifyKey;

  private final Object[] verifyData;

  public VerifyMessageResult(boolean verifyWasSuccessful, CoreMessageKey verifyKey, Object[] verifyData) {
    this.verifyWasSuccessful = verifyWasSuccessful;
    this.verifyKey = verifyKey;
    this.verifyData = verifyData;
  }

  public boolean isVerifyWasSuccessful() {
    return verifyWasSuccessful;
  }

  public CoreMessageKey getVerifyKey() {
    return verifyKey;
  }

  public Object[] getVerifyData() {
    return verifyData;
  }
}
