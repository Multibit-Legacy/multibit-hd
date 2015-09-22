package org.multibit.hd.ui.views.wizards.use_hardware_wallet;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.multibit.hd.core.services.ApplicationEventService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.messages.ButtonRequest;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.swing.*;
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
public class UseHardwareWalletWizardModel extends AbstractHardwareWalletWizardModel<UseHardwareWalletState> {

  private static final Logger log = LoggerFactory.getLogger(UseHardwareWalletWizardModel.class);

  /**
   * The current selection option as a state
   */
  private UseHardwareWalletState currentSelection = UseHardwareWalletState.BUY_DEVICE;

  /**
   * The features of the attached Trezor
   */
  Optional<Features> featuresOptional = Optional.absent();

  /**
   * The "enter pin" panel view
   */
  private UseHardwareWalletEnterPinPanelView enterPinPanelView;

  private UseHardwareWalletRequestCipherKeyPanelView requestCipherKeyPanelView;

  private UseHardwareWalletReportPanelView reportPanelView;

  public UseHardwareWalletWizardModel(UseHardwareWalletState useHardwareWalletState) {
    super(useHardwareWalletState);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  public UseHardwareWalletEnterPinPanelView getEnterPinPanelView() {
    return enterPinPanelView;
  }

  public void setEnterPinPanelView(UseHardwareWalletEnterPinPanelView enterPinPanelView) {
    this.enterPinPanelView = enterPinPanelView;
  }

  public void setRequestCipherKeyPanelView(UseHardwareWalletRequestCipherKeyPanelView requestCipherKeyPanelView) {
    this.requestCipherKeyPanelView = requestCipherKeyPanelView;
  }

  @Override
  public void showNext() {
    log.debug("Current selection : {}", getCurrentSelection());
    switch (state) {
      case SELECT_HARDWARE_ACTION:
        switch (getCurrentSelection()) {
          case SELECT_HARDWARE_ACTION:
            break;
          case USE_TREZOR_WALLET:
            break;
          case BUY_DEVICE:
            state = UseHardwareWalletState.BUY_DEVICE;
            break;
          case VERIFY_DEVICE:
            state = UseHardwareWalletState.VERIFY_DEVICE;
            break;
          case REQUEST_WIPE_DEVICE:
            state = UseHardwareWalletState.REQUEST_WIPE_DEVICE;
            break;
          default:
            throw new IllegalStateException("Cannot showNext with a state of SELECT_TREZOR_ACTION and a selection of " + getCurrentSelection());
        }
        break;
      case BUY_DEVICE:
        state = UseHardwareWalletState.USE_HARDWARE_WALLET_REPORT_PANEL;
        break;
      case VERIFY_DEVICE:
        state = UseHardwareWalletState.USE_HARDWARE_WALLET_REPORT_PANEL;
        break;
      case REQUEST_WIPE_DEVICE:
        // Trezor must have failed and user is clicking through
        state = UseHardwareWalletState.USE_HARDWARE_WALLET_REPORT_PANEL;
        break;
      case CONFIRM_WIPE_DEVICE:
        state = UseHardwareWalletState.USE_HARDWARE_WALLET_REPORT_PANEL;
        break;
      case ENTER_PIN:
        break;
      case NO_PIN_REQUIRED:
        break;

      default:
        throw new IllegalStateException("Cannot showNext with a state of " + state);
    }
  }

  @Override
  public void showPrevious() {

    switch (state) {
      case BUY_DEVICE:
        state = UseHardwareWalletState.SELECT_HARDWARE_ACTION;
        break;

      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }


  @Override
  public void showPINEntry(HardwareWalletEvent event) {

    // Device is PIN protected
    switch (state) {
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
      case REQUEST_WIPE_DEVICE:
        switch (buttonRequest.getButtonRequestType()) {
          case WIPE_DEVICE:
            // Device requires confirmation to wipe
            state = UseHardwareWalletState.CONFIRM_WIPE_DEVICE;
            break;
          default:
            throw new IllegalStateException("Unexpected button: " + buttonRequest.getButtonRequestType().name());
        }
        break;
      case VERIFY_DEVICE:
        // Should be catered for by finish on Verify Trezor panel

      case USE_HARDWARE_WALLET_REPORT_PANEL:
        // Should be catered for by finish on Trezor report panel

        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }

  @Override
  public void showOperationSucceeded(HardwareWalletEvent event) {
    switch (state) {
      case CONFIRM_WIPE_DEVICE:
        // Indicate a successful wipe
        state= UseHardwareWalletState.USE_HARDWARE_WALLET_REPORT_PANEL;
        setReportMessageKey(MessageKey.HARDWARE_WIPE_DEVICE_SUCCESS);
        setReportMessageStatus(true);

        SwingUtilities.invokeLater(
          new Runnable() {
            @Override
            public void run() {
              // Let MainController know about this
              ViewEvents.fireComponentChangedEvent(UseHardwareWalletState.CONFIRM_WIPE_DEVICE.name(), Optional.of(Dates.nowUtc()));
            }
          });

        break;
      default:
        // Do nothing - see #603
        log.info("Message:'Operation succeeded'\n{}", event.getMessage().get());
    }
  }

  @Override
  public void showOperationFailed(HardwareWalletEvent event) {
    // In all cases move to the report panel with a failure message
    state= UseHardwareWalletState.USE_HARDWARE_WALLET_REPORT_PANEL;
    setReportMessageKey(MessageKey.HARDWARE_WIPE_DEVICE_FAILURE);
    setReportMessageStatus(false);

    // Ignore device reset messages
    ApplicationEventService.setIgnoreHardwareWalletEventsThreshold(Dates.nowUtc().plusSeconds(1));

  }

  public UseHardwareWalletState getCurrentSelection() {
    return currentSelection;
  }

  public void setCurrentSelection(UseHardwareWalletState currentSelection) {
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

          Optional<HardwareWalletService> hardwareWalletServiceOptional = CoreServices.getCurrentHardwareWalletService();
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
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              ViewEvents.fireComponentChangedEvent(UseHardwareWalletState.VERIFY_DEVICE.name(), Optional.absent());
            }
          });

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

          Optional<HardwareWalletService> hardwareWalletServiceOptional = CoreServices.getCurrentHardwareWalletService();
          if (hardwareWalletServiceOptional.isPresent()) {
            HardwareWalletService hardwareWalletService = hardwareWalletServiceOptional.get();
            if (hardwareWalletService.isDeviceReady() && hardwareWalletService.isWalletPresent()) {
              hardwareWalletService.wipeDevice();
            } else {
              log.debug("No wallet present so no need to wipe the device");
            }
          } else {
            log.error("Unexpected failure of hardware wallet service");
          }
          return true;

        }

      });
    Futures.addCallback(
      future, new FutureCallback() {
        @Override
        public void onSuccess(@Nullable Object result) {

          log.debug("Wipe device request has been performed successfully");

          // We now wiped the device so throw a ComponentChangedEvent for the UI to update
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              ViewEvents.fireComponentChangedEvent(UseHardwareWalletState.REQUEST_WIPE_DEVICE.name(), Optional.absent());
            }
          });

          setReportMessageKey(MessageKey.HARDWARE_WIPE_DEVICE_SUCCESS);
          setReportMessageStatus(true);
        }

        @Override
        public void onFailure(Throwable t) {

          // Have a failure
          log.error("Unexpected failure during request wipe", t);

          setReportMessageKey(MessageKey.HARDWARE_WIPE_DEVICE_FAILURE);
          setReportMessageStatus(false);
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
          Optional<HardwareWalletService> hardwareWalletService = CoreServices.getCurrentHardwareWalletService();
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
