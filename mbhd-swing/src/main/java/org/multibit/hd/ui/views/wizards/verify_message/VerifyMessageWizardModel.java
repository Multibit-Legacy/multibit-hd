package org.multibit.hd.ui.views.wizards.verify_message;

import org.bitcoinj.core.Address;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "verify message" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class VerifyMessageWizardModel extends AbstractWizardModel<VerifyMessageState> {

  private Address verifyingAddress = null;
  private String message;
  private byte[] signature;

  /**
   * @param state The state object
   */
  public VerifyMessageWizardModel(VerifyMessageState state) {
    super(state);
  }

  /**
   * @return The message
   */
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return The verifying address
   */
  public Address getVerifyingAddress() {
    return verifyingAddress;
  }

  public void setVerifyingAddress(Address verifyingAddress) {
    this.verifyingAddress = verifyingAddress;
  }

  /**
   * @return The signature
   */
  public byte[] getSignature() {
    return signature;
  }

  public void setSignature(byte[] signature) {
    this.signature = signature;
  }
}
