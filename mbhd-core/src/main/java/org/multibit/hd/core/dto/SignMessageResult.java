package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>DTO describing success or failure of signing a message

 */
public class SignMessageResult {

  private final Optional<String> signature;

  private final boolean signingWasSuccessful;

  private final CoreMessageKey signatureKey;

  /**
   * TODO Consider a List<String> instead
   */
  private final Object[] signatureData;

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP2"})
  public SignMessageResult(Optional<String> signature, boolean signingWasSuccessful, CoreMessageKey signatureKey, Object[] signatureData) {
    this.signature = signature;
    this.signingWasSuccessful = signingWasSuccessful;
    this.signatureKey = signatureKey;
    this.signatureData = signatureData;
  }

  public boolean isSigningWasSuccessful() {
    return signingWasSuccessful;
  }

  public CoreMessageKey getSignatureKey() {
    return signatureKey;
  }

  // The fix to this would introduce more problems than the danger
  // it potentially presents
  @SuppressFBWarnings({"EI_EXPOSE_REP"})
  public Object[] getSignatureData() {
    return signatureData;
  }

  public Optional<String> getSignature() {
    return signature;
  }
}
