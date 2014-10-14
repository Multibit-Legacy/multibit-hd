package org.multibit.hd.ui.views.wizards.sign_message;

import org.bitcoinj.core.Address;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "sign message" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SignMessageWizardModel extends AbstractWizardModel<SignMessageState> {

  private Address signingAddress = null;
  private String message;
  private byte[] signature;

  /**
   * @param state The state object
   */
  public SignMessageWizardModel(SignMessageState state) {
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
   * @return The signing address
   */
  public Address getSigningAddress() {
    return signingAddress;
  }

  public void setSigningAddress(Address signingAddress) {
    this.signingAddress = signingAddress;
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
