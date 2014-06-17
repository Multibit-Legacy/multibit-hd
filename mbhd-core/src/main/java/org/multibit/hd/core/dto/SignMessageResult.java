package org.multibit.hd.core.dto;

import com.google.common.base.Optional;

/**
 *  <p>DTO describing success or failure of signing a message

 */
public class SignMessageResult {

  private final Optional<String> signature;

  private final boolean signingWasSuccessful;

  private final CoreMessageKey signatureKey;

  private final Object[] signatureData;

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

  public Object[] getSignatureData() {
    return signatureData;
  }

  public Optional<String> getSignature() {
    return signature;
  }
}
