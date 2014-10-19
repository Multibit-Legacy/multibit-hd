package org.multibit.hd.ui.views.wizards.use_trezor;

import org.bitcoinj.core.Utils;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.messages.Failure;
import org.multibit.hd.hardware.core.messages.FailureType;
import org.multibit.hd.hardware.core.messages.Success;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Model object to provide the following to "use Trezor wizard":</p>
 * <ul>
 * <li>Storage of PIN entered</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class UseTrezorWizardModel extends AbstractHardwareWalletWizardModel<UseTrezorState> {

  private static final Logger log = LoggerFactory.getLogger(UseTrezorWizardModel.class);

  /**
   * The "enter pin" panel model
   */
  private UseTrezorEnterPinPanelModel enterPinPanelModel;

  public UseTrezorWizardModel(UseTrezorState useTrezorState) {
    super(useTrezorState);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  public UseTrezorEnterPinPanelModel getEnterPinPanelModel() {
    return enterPinPanelModel;
  }

  public void setEnterPinPanelModel(UseTrezorEnterPinPanelModel enterPinPanelModel) {
    this.enterPinPanelModel = enterPinPanelModel;
  }

  @Override
   public void showNext() {
     switch (state) {
       case ENTER_PIN:
         state = UseTrezorState.PRESS_CONFIRM_FOR_UNLOCK;
         break;
       case NO_PIN_REQUIRED:
         state = UseTrezorState.PRESS_CONFIRM_FOR_UNLOCK;
         break;
       case PRESS_CONFIRM_FOR_UNLOCK:
         state = UseTrezorState.USE_TREZOR_REPORT_PANEL;
         break;
       default:
         throw new IllegalStateException("Cannot showNext with a state of " + state);
     }
   }


  @Override
  public void showPINEntry(HardwareWalletEvent event) {

    // Device is PIN protected

    switch (state) {
      case REQUEST_CIPHER_KEY:
        state = UseTrezorState.ENTER_PIN;
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }

  @Override
  public void showButtonPress(HardwareWalletEvent event) {

    // Require a button press to encrypt the message
    switch (state) {
      case REQUEST_CIPHER_KEY:
      case ENTER_PIN:
      case NO_PIN_REQUIRED:
        state = UseTrezorState.PRESS_CONFIRM_FOR_UNLOCK;
        break;
      case PRESS_CONFIRM_FOR_UNLOCK:
        // Should be catered for by finish
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }

  }

  @Override
  public void showOperationSucceeded(HardwareWalletEvent event) {

    // Typically the response to a correct PIN leading to a cipher key payload
    byte[] payload = ((Success) event.getMessage().get()).getPayload();
    String message = ((Success) event.getMessage().get()).getMessage();

    log.info(
      "Message:'{}'\nPayload: {}",
      message,
      Utils.HEX.encode(payload)
    );
  }

  @Override
  public void showOperationFailed(HardwareWalletEvent event) {
    // Could be caused by wrong PIN
    FailureType failureType = ((Failure) event.getMessage().get()).getType();

    log.info(
      "Message:'Failure'\nFailure type: {}",
      failureType.name()
    );
  }
}
