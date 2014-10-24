package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.bitcoinj.core.Utils;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.messages.Failure;
import org.multibit.hd.hardware.core.messages.FailureType;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.Success;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

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
   * Request features requires a separate executor
   */
  private final ListeningExecutorService requestFeaturesService = SafeExecutors.newSingleThreadExecutor("request-features");

  /**
    * Wipe Trezor requires a separate executor
    */
   private final ListeningExecutorService wipeTrezorService = SafeExecutors.newSingleThreadExecutor("wipe-trezor");

   /**
   * The current selection option as a state
   */
  private UseTrezorState currentSelection = UseTrezorState.USE_TREZOR_WALLET;

  /**
   * The features of the attached Trezor
   */
  Optional<Features> featuresOptional;

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
    log.debug("Current selection : {}", getCurrentSelection());
    switch (state) {
      case SELECT_TREZOR_ACTION:
        if (UseTrezorState.USE_TREZOR_WALLET.equals(getCurrentSelection())) {
          state = UseTrezorState.REQUEST_CIPHER_KEY;
          break;
        } else if (UseTrezorState.BUY_TREZOR.equals(getCurrentSelection())) {
          state = UseTrezorState.BUY_TREZOR;
          break;
        } else if (UseTrezorState.WIPE_TREZOR.equals(getCurrentSelection())) {
          state = UseTrezorState.WIPE_TREZOR;
          break;
        } else if (UseTrezorState.VERIFY_TREZOR.equals(getCurrentSelection())) {
          state = UseTrezorState.VERIFY_TREZOR;
          break;
        } else {
          throw new IllegalStateException("Cannot showNext with a state of SELECT_TREZOR_ACTION and a selection of " + getCurrentSelection());
        }
      case BUY_TREZOR:
        state = UseTrezorState.USE_TREZOR_REPORT_PANEL;
        break;
      case VERIFY_TREZOR:
        state = UseTrezorState.USE_TREZOR_REPORT_PANEL;
        break;
      case WIPE_TREZOR:
        state = UseTrezorState.USE_TREZOR_REPORT_PANEL;
        break;
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
    public void showPrevious() {

      switch (state) {
        case BUY_TREZOR:
          state = UseTrezorState.SELECT_TREZOR_ACTION;
          break;

        default:
          throw new IllegalStateException("Unknown state: " + state.name());
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

    log.debug("Received hardwareWalletEvent {}", event);

    switch (state) {
      case REQUEST_CIPHER_KEY:
        state = UseTrezorState.ENTER_PIN;
        break;
      case ENTER_PIN:
      case NO_PIN_REQUIRED:
        // Require a button press to encrypt the message
        //    state = UseTrezorState.PRESS_CONFIRM_FOR_UNLOCK;
        break;
      case PRESS_CONFIRM_FOR_UNLOCK:
        // Should be catered for by finish
        break;
      case WIPE_TREZOR:
        // Should be catered for by finish on Wipe Trezor panel
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

  public UseTrezorState getCurrentSelection() {
    return currentSelection;
  }

  public void setCurrentSelection(UseTrezorState currentSelection) {
    this.currentSelection = currentSelection;
  }

  public Optional<Features> getFeaturesOptional() {
    return featuresOptional;
  }

  /**
   * <p>Request the Trezor features</p>
   * <p>Reduced visibility for panel view</p>
   */
  void requestFeatures() {

    // Start the features request
    ListenableFuture future = requestFeaturesService.submit(new Callable<Boolean>() {

      @Override
      public Boolean call() throws Exception {

        Optional<HardwareWalletService> hardwareWalletServiceOptional = CoreServices.getOrCreateHardwareWalletService();
        if (hardwareWalletServiceOptional.isPresent()) {
          HardwareWalletService hardwareWalletService = hardwareWalletServiceOptional.get();
          featuresOptional = hardwareWalletService.getContext().getFeatures();
          log.debug("Features : {}", featuresOptional);
        } else {
          log.error("No hardware wallet service");
        }
        return true;

      }

    });
    Futures.addCallback(future, new FutureCallback() {
      @Override
      public void onSuccess(@Nullable Object result) {

        // We now have the features so throw a ComponentChangedEvent for the UI to update
        ViewEvents.fireComponentChangedEvent(UseTrezorState.USE_TREZOR_WALLET.name(), Optional.absent());

      }

      @Override
      public void onFailure(Throwable t) {

        // Have a failure - add failure text to the text area

      }
    });

  }

  /**
   * <p>Wipe the Trezor device</p>
   * <p>Reduced visibility for panel view</p>
   */
  void wipeTrezor() {

    // Start the wipe Trezor
    ListenableFuture future = wipeTrezorService.submit(new Callable<Boolean>() {

      @Override
      public Boolean call() throws Exception {

        Optional<HardwareWalletService> hardwareWalletServiceOptional = CoreServices.getOrCreateHardwareWalletService();
        if (hardwareWalletServiceOptional.isPresent()) {
          HardwareWalletService hardwareWalletService = hardwareWalletServiceOptional.get();
          if (hardwareWalletService.isWalletPresent()) {
            hardwareWalletService.wipeDevice();
            log.debug("Wipe device request has been performed");
          } else {
            log.debug("No wallet present so no need to wipe the device");
          }
        } else {
          log.error("No hardware wallet service");
        }
        return true;

      }

    });
    Futures.addCallback(future, new FutureCallback() {
      @Override
      public void onSuccess(@Nullable Object result) {

        // We now wiped the device so throw a ComponentChangedEvent for the UI to update
        ViewEvents.fireComponentChangedEvent(UseTrezorState.WIPE_TREZOR.name(), Optional.absent());

      }

      @Override
      public void onFailure(Throwable t) {

        // Have a failure
        t.printStackTrace();
      }
    });

  }
}
