package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.messages.ButtonRequest;
import org.multibit.hd.hardware.core.messages.Failure;
import org.multibit.hd.hardware.core.messages.FailureType;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
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
 * <li>Handling of various Trezor requests</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class UseTrezorWizardModel extends AbstractHardwareWalletWizardModel<UseTrezorState> {

  private static final Logger log = LoggerFactory.getLogger(UseTrezorWizardModel.class);

  /**
   * Wallet creation has its own executor service
   */
  private final ListeningExecutorService trezorCreateWalletService = SafeExecutors.newSingleThreadExecutor("trezor-wallet-create");

  /**
   * The current selection option as a state
   */
  private UseTrezorState currentSelection = UseTrezorState.USE_TREZOR_WALLET;

  /**
   * The features of the attached Trezor
   */
  Optional<Features> featuresOptional = Optional.absent();

  /**
   * The "enter pin" panel view
   */
  private UseTrezorEnterPinPanelView enterPinPanelView;

  private UseTrezorRequestCipherKeyPanelView requestCipherKeyPanelView;

  private UseTrezorReportPanelView reportPanelView;
  private UseTrezorConfirmWipeDevicePanelView confirmWipeDevicePanelView;
  private UseTrezorRequestWipeDevicePanelView requestWipeDevicePanelView;
  private boolean showReportView;
  private MessageKey reportMessageKey;
  private boolean reportMessageStatus;

  public UseTrezorWizardModel(UseTrezorState useTrezorState) {
    super(useTrezorState);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  public UseTrezorEnterPinPanelView getEnterPinPanelView() {
    return enterPinPanelView;
  }

  public void setEnterPinPanelView(UseTrezorEnterPinPanelView enterPinPanelView) {
    this.enterPinPanelView = enterPinPanelView;
  }


  public UseTrezorRequestCipherKeyPanelView getRequestCipherKeyPanelView() {
    return requestCipherKeyPanelView;
  }

  public void setRequestCipherKeyPanelView(UseTrezorRequestCipherKeyPanelView requestCipherKeyPanelView) {
    this.requestCipherKeyPanelView = requestCipherKeyPanelView;
  }

  public UseTrezorReportPanelView getReportPanelView() {
    return reportPanelView;
  }

  public void setReportPanelView(UseTrezorReportPanelView reportPanelView) {
    this.reportPanelView = reportPanelView;
  }

  public void setConfirmWipeDevicePanelView(UseTrezorConfirmWipeDevicePanelView confirmWipeDevicePanelView) {
    this.confirmWipeDevicePanelView = confirmWipeDevicePanelView;
  }

  public void setRequestWipeDevicePanelView(UseTrezorRequestWipeDevicePanelView requestWipeDevicePanelView) {
    this.requestWipeDevicePanelView = requestWipeDevicePanelView;
  }

  public void setShowReportView(boolean showReportView) {
    this.showReportView = showReportView;
  }

  @Override
  public void showNext() {
    log.debug("Current selection : {}", getCurrentSelection());
    switch (state) {
      case SELECT_TREZOR_ACTION:
        switch (getCurrentSelection()) {
          case SELECT_TREZOR_ACTION:
            break;
          case USE_TREZOR_WALLET:
            //state = UseTrezorState.REQUEST_CIPHER_KEY;
            break;
          case BUY_TREZOR:
            state = UseTrezorState.BUY_TREZOR;
            break;
          case VERIFY_TREZOR:
            state = UseTrezorState.VERIFY_TREZOR;
            break;
          case REQUEST_WIPE_TREZOR:
            state = UseTrezorState.REQUEST_WIPE_TREZOR;
            break;
          default:
            throw new IllegalStateException("Cannot showNext with a state of SELECT_TREZOR_ACTION and a selection of " + getCurrentSelection());
        }
        break;
      case BUY_TREZOR:
        state = UseTrezorState.USE_TREZOR_REPORT_PANEL;
        break;
      case VERIFY_TREZOR:
        state = UseTrezorState.USE_TREZOR_REPORT_PANEL;
        break;
      case REQUEST_WIPE_TREZOR:
        // Trezor must have failed and user is clicking through
        state = UseTrezorState.USE_TREZOR_REPORT_PANEL;
        break;
      case CONFIRM_WIPE_TREZOR:
        state = UseTrezorState.USE_TREZOR_REPORT_PANEL;
        break;
      case ENTER_PIN:
//        state = UseTrezorState.PRESS_CONFIRM_FOR_UNLOCK;
        break;
      case NO_PIN_REQUIRED:
//        state = UseTrezorState.PRESS_CONFIRM_FOR_UNLOCK;
        break;
//      case PRESS_CONFIRM_FOR_UNLOCK:
//        state = UseTrezorState.USE_TREZOR_REPORT_PANEL;
//        break;
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
//      case REQUEST_CIPHER_KEY:
//        state = UseTrezorState.ENTER_PIN;
//        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }

  @Override
  public void showButtonPress(HardwareWalletEvent event) {

    log.debug("Received hardwareWalletEvent {}", event);

    ButtonRequest buttonRequest = (ButtonRequest) event.getMessage().get();

    switch (state) {
      case ENTER_PIN:
      case NO_PIN_REQUIRED:
        // Should be catered for by finish
        break;
      case REQUEST_WIPE_TREZOR:
        switch (buttonRequest.getButtonRequestType()) {
          case WIPE_DEVICE:
            // Device requires confirmation to wipe
            state = UseTrezorState.CONFIRM_WIPE_TREZOR;
            break;
          default:
            throw new IllegalStateException("Unexpected button: " + buttonRequest.getButtonRequestType().name());
        }
        break;
      case VERIFY_TREZOR:
        // Should be catered for by finish on Verify Trezor panel

      case USE_TREZOR_REPORT_PANEL:
        // Should be catered for by finish on Trezor report panel

        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }

  }

  @Override
  public void showOperationSucceeded(HardwareWalletEvent event) {

    switch (state) {
      case CONFIRM_WIPE_TREZOR:
        // Indicate a successful wipe
        state=UseTrezorState.USE_TREZOR_REPORT_PANEL;
        reportMessageKey = MessageKey.TREZOR_WIPE_DEVICE_SUCCESS;
        reportMessageStatus = true;
        break;
      default:
        // TODO Fill in the other states and provide success feedback
        log.info(
          "Message:'Operation succeeded'\n{}",
          event.getMessage().get()
        );
    }

  }

  @Override
  public void showOperationFailed(HardwareWalletEvent event) {

    switch (state) {
      case ENTER_PIN:
        // Indicate a failed PIN
        getEnterPinPanelView().setPinStatus(false, true);
        break;
      case REQUEST_WIPE_TREZOR:
        // Device communication has failed during requesting the wipe
      case CONFIRM_WIPE_TREZOR:
        // Indicate a cancelled wipe
        state=UseTrezorState.USE_TREZOR_REPORT_PANEL;
        reportMessageKey = MessageKey.TREZOR_WIPE_DEVICE_FAILURE;
        reportMessageStatus = false;
        break;
      default:
        // TODO Fill in the other states and provide failure feedback
        FailureType failureType = ((Failure) event.getMessage().get()).getType();
        log.info(
          "Message:'Failure'\nFailure type: {}",
          failureType.name()
          // TODO feed back to user if Failure type = PIN_INVALID
        );
    }


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
   */
  public void requestFeatures() {

    // Start the features request
    ListenableFuture future = hardwareWalletRequestService.submit(
      new Callable<Boolean>() {

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
    Futures.addCallback(
      future, new FutureCallback() {
        @Override
        public void onSuccess(@Nullable Object result) {

          // We now have the features so throw a ComponentChangedEvent for the UI to update
          ViewEvents.fireComponentChangedEvent(UseTrezorState.VERIFY_TREZOR.name(), Optional.absent());

        }

        @Override
        public void onFailure(Throwable t) {

          // Have a failure - add failure text to the text area

        }
      });

  }

  /**
   * <p>Wipe the Trezor device</p>
   */
  public void requestWipeDevice() {

    // Start the wipe Trezor
    ListenableFuture future = hardwareWalletRequestService.submit(
      new Callable<Boolean>() {

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
    Futures.addCallback(
      future, new FutureCallback() {
        @Override
        public void onSuccess(@Nullable Object result) {

          // We now wiped the device so throw a ComponentChangedEvent for the UI to update
          ViewEvents.fireComponentChangedEvent(UseTrezorState.REQUEST_WIPE_TREZOR.name(), Optional.absent());

        }

        @Override
        public void onFailure(Throwable t) {

          // Have a failure
          t.printStackTrace();
        }
      });

  }

  /**
   * @param pinPositions The PIN positions providing a level of obfuscation to protect the PIN
   */
  public void requestPinCheck(final String pinPositions) {

    ListenableFuture<Boolean> pinCheckFuture = hardwareWalletRequestService.submit(
      new Callable<Boolean>() {

        @Override
        public Boolean call() {

          log.debug("Performing a PIN check");

          // Talk to the Trezor and get it to check the PIN
          // This call to the Trezor will (sometime later) fire a
          // HardwareWalletEvent containing the encrypted text (or a PIN failure)
          // Expect a SHOW_OPERATION_SUCCEEDED or SHOW_OPERATION_FAILED
          Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();
          hardwareWalletService.get().providePIN(pinPositions);

          // Must have successfully send the message to be here
          return true;

        }
      });
    Futures.addCallback(
      pinCheckFuture, new FutureCallback<Boolean>() {

        @Override
        public void onSuccess(Boolean result) {

          // Do nothing - message was successfully relayed to the device

        }

        @Override
        public void onFailure(Throwable t) {

          // Device failed to receive the message

          getEnterPinPanelView().setPinStatus(false, true);

          // Should not have seen an error
          ExceptionHandler.handleThrowable(t);
        }
      }
    );

  }

}
