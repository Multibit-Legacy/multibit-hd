package org.multibit.hd.ui.views.wizards;

import com.google.common.util.concurrent.ListeningExecutorService;
import org.multibit.hd.core.concurrent.SafeExecutors;
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

  /**
   * Trezor requests have their own executor service
   */
  protected final ListeningExecutorService hardwareWalletRequestService = SafeExecutors.newSingleThreadExecutor("trezor-requests");

  protected AbstractHardwareWalletWizardModel(S state) {
    super(state);

  }

  /**
   * Handles state transition to a "PIN entry" panel
   *
   * Usually this will be an "EnterCurrentPin" panel following a "Request" and the
   * panel will show a PIN matrix
   *
   * Clicking "Next" or "Unlock" will trigger the sending of PIN positions to the device
   * and subsequent state transitions
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void showPINEntry(HardwareWalletEvent event) {
    // Do nothing
  }

  /**
   * Handles state transition to a "button press" panel
   *
   * Usually this will be a "Confirm" panel following a "Request" and the
   * panel will show text mirroring the Trezor
   *
   * Clicking a button on the device will trigger further state transitions
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
   * Handles provision of a deterministic hierarchy from the device
   *
   * @param event The hardware wallet event containing payload and context
   */
  public void receivedDeterministicHierarchy(HardwareWalletEvent event) {
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
