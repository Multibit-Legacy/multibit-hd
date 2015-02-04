package org.multibit.hd.core.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>DTO describing success or failure of signing a message

 */
public class VerifyMessageResult {

  private final boolean verifyWasSuccessful;

  private final CoreMessageKey verifyKey;

  /**
   * TODO Consider a List<String> instead
   */
  private final Object[] verifyData;

  // The fix to this would introduce more problems than the danger
  // it potentially presents
  @SuppressFBWarnings({"EI_EXPOSE_REP2"})
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

  // The fix to this would introduce more problems than the danger
  // it potentially presents
  @SuppressFBWarnings({"EI_EXPOSE_REP"})
  public Object[] getVerifyData() {
    return verifyData;
  }
}
