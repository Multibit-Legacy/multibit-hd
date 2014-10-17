package org.multibit.hd.ui.views.wizards;

import org.multibit.hd.hardware.core.events.HardwareWalletEvent;

/**
 * <p>Abstract base class wizard model:</p>
 * <ul>
 * <li>Access to standard implementations of required methods to support hardware wallets</li>
 * </ul>
 *
 * @param <S> The state object type
 *
 * @since 0.0.1
 */
public abstract class AbstractHardwareWalletWizardModel<S> extends AbstractWizardModel<S> {

  protected AbstractHardwareWalletWizardModel(S state) {
    super(state);

  }

  /**
   * Handles state transition to a "PIN entry" panel
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void showPINEntry(HardwareWalletEvent event) {
    // Do nothing
  }

  /**
   * Handles state transition to a "button press" panel
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void showButtonPress(HardwareWalletEvent event) {
    // Do nothing
  }

  /**
   * Handles state transition to an "operation succeeded" panel
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void showOperationSucceeded(HardwareWalletEvent event) {
    // Do nothing
  }

  /**
   * Handles state transition to an "operation failed" panel
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void showOperationFailed(HardwareWalletEvent event) {
    // Do nothing
  }

  /**
   * Handles state transition to a "word entry" panel
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void showWordEntry(HardwareWalletEvent event) {
    // Do nothing
  }

  /**
   * Handles state transition to a "provide entropy" panel
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void showProvideEntropy(HardwareWalletEvent event) {
    // Do nothing

  }

  /**
   * Handles provision of an Address from the device
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void receivedAddress(HardwareWalletEvent event) {
    // Do nothing
  }

  /**
   * Handles provision of a public key from the device
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void receivedPublicKey(HardwareWalletEvent event) {
    // Do nothing
  }

  /**
   * Handles provision of a message signature from the device
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void receivedMessageSignature(HardwareWalletEvent event) {
    // Do nothing
  }

}
