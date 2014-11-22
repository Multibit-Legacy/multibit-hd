package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.messages.*;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * <p>Model object to provide the following to "change PIN wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class ChangePinWizardModel extends AbstractHardwareWalletWizardModel<ChangePinState> {

  private static final Logger log = LoggerFactory.getLogger(ChangePinWizardModel.class);

  /**
   * Change PIN requires a separate executor
   */
  private final ListeningExecutorService trezorRequestService = SafeExecutors.newSingleThreadExecutor("trezor-requests-change-pin");

  /**
   * True if the PIN should be removed
   */
  private boolean removePin = false;

  /**
   * The "request change PIN" view
   */
  private ChangePinRequestChangePinPanelView requestChangePinPanelView;

  /**
   * The "request remove PIN" view
   */
  private ChangePinRequestRemovePinPanelView requestRemovePinPanelView;

  /**
   * The "enter current PIN" view
   */
  private ChangePinEnterCurrentPinPanelView enterCurrentPinPanelView;

  /**
   * The "enter new PIN" view
   */
  private ChangePinEnterNewPinPanelView enterNewPinPanelView;

  /**
   * The "confirm new PIN" view
   */
  private ChangePinConfirmNewPinPanelView confirmNewPinPanelView;

  /**
   * The most recent PIN entered by the user
   */
  private String mostRecentPin;

  /**
   * True if the report message indicates a success
   */
  private boolean reportMessageStatus = false;

  /**
   * The report message key
   */
  private MessageKey reportMessageKey;
  /**
   * True if the device currently has PIN protection
   */
  private boolean hasPinProtection = false;

  /**
   * @param state The state object
   */
  public ChangePinWizardModel(ChangePinState state) {
    super(state);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @return The most recent PIN entered by the user
   */
  public String getMostRecentPin() {
    return mostRecentPin;
  }

  public void setMostRecentPin(String mostRecentPin) {
    this.mostRecentPin = mostRecentPin;
  }

  public void setRequestChangePinPanelView(ChangePinRequestChangePinPanelView requestChangePinPanelView) {
    this.requestChangePinPanelView = requestChangePinPanelView;
  }

  public void setRequestRemovePinPanelView(ChangePinRequestRemovePinPanelView requestRemovePinPanelView) {
    this.requestRemovePinPanelView = requestRemovePinPanelView;
  }

  public void setEnterCurrentPinPanelView(ChangePinEnterCurrentPinPanelView enterCurrentPinPanelView) {
    this.enterCurrentPinPanelView = enterCurrentPinPanelView;
  }

  public void setEnterNewPinPanelView(ChangePinEnterNewPinPanelView enterNewPinPanelView) {
    this.enterNewPinPanelView = enterNewPinPanelView;
  }

  public void setConfirmNewPinPanelView(ChangePinConfirmNewPinPanelView confirmNewPinPanelView) {
    this.confirmNewPinPanelView = confirmNewPinPanelView;
  }

  /**
   * @param removePin True if the user selected the "remove PIN" option
   */
  public void setRemovePin(boolean removePin) {
    this.removePin = removePin;
  }

  /**
   * @return True if the report message indicates success
   */
  public boolean isReportMessageStatus() {
    return reportMessageStatus;
  }

  /**
   * @return The key to the report message
   */
  public MessageKey getReportMessageKey() {
    return reportMessageKey;
  }

  /**
   * @return True if the device currently has PIN protection
   */
  public boolean hasPinProtection() {
    return hasPinProtection;
  }

  @Subscribe
  public void onVerificationStatusChangedEvent(VerificationStatusChangedEvent event) {

    if (ChangePinState.ENTER_CURRENT_PIN.name().equals(event.getPanelName())) {
      ViewEvents.fireWizardButtonEnabledEvent(event.getPanelName(), WizardButton.NEXT, event.isOK());
    }

  }


  @Override
  public void showNext() {

    switch (state) {
      case SELECT_OPTION:

        // Read the current features to establish PIN status
        Features features = CoreServices.getOrCreateHardwareWalletService().get().getContext().getFeatures().get();
        hasPinProtection = features.hasPinProtection();

        if (removePin) {
          state = ChangePinState.REQUEST_REMOVE_PIN;
        } else if (hasPinProtection()) {
          state = ChangePinState.REQUEST_CHANGE_PIN;
        } else {
          state = ChangePinState.REQUEST_ADD_PIN;
        }
        break;
      case REQUEST_ADD_PIN:
        state = ChangePinState.CONFIRM_NEW_PIN;
        break;
      case CONFIRM_NEW_PIN:
        state = ChangePinState.ENTER_NEW_PIN;
        break;
      case REQUEST_CHANGE_PIN:
        state = ChangePinState.CONFIRM_CHANGE_PIN;
        break;
      case CONFIRM_CHANGE_PIN:
        state = ChangePinState.SHOW_REPORT;
        break;
      case REQUEST_REMOVE_PIN:
        state = ChangePinState.CONFIRM_REMOVE_PIN;
        break;
      case CONFIRM_REMOVE_PIN:
        state = ChangePinState.SHOW_REPORT;
        break;
      case ENTER_CURRENT_PIN:
        // Provide PIN handled by ChangePinWizard overriding Next button behaviour
        break;
      case ENTER_NEW_PIN:
        // Provide PIN handled by ChangePinWizard overriding Next button behaviour
        break;
      case SHOW_REPORT:
        state = ChangePinState.SHOW_REPORT;
        break;
      default:
        throw new IllegalStateException("Should not reach here from " + state.name());
    }
  }

  @Override
  public void showButtonPress(HardwareWalletEvent event) {

    // The button request could have come about from many possible paths
    switch (state) {
      case SELECT_OPTION:
        break;
      case REQUEST_REMOVE_PIN:
        // Device is requesting PIN removal
        state = ChangePinState.CONFIRM_REMOVE_PIN;
        break;
      case REQUEST_ADD_PIN:
        // Device is requesting PIN addition
        state = ChangePinState.CONFIRM_ADD_PIN;
        break;
      case REQUEST_CHANGE_PIN:
        // Device is requesting a PIN change
        state = ChangePinState.CONFIRM_CHANGE_PIN;
        break;
      default:
        throw new IllegalStateException("Should not reach here from " + state.name());
    }

  }

  @Override
  public void showPINEntry(HardwareWalletEvent event) {

    // Determine if this is the first or second PIN entry
    PinMatrixRequest request = (PinMatrixRequest) event.getMessage().get();
    PinMatrixRequestType requestType = request.getPinMatrixRequestType();

    // The PIN entry could have come about from many possible paths
    switch (state) {
      case CONFIRM_ADD_PIN:
        // User has confirmed the change of the PIN
        switch (requestType) {
          case NEW_FIRST:
            state = ChangePinState.ENTER_NEW_PIN;
            break;
          default:
            throw new IllegalStateException("Should not reach here from " + requestType.name());
        }
        break;
      case CONFIRM_CHANGE_PIN:
        // User has confirmed the change of the PIN
        switch (requestType) {
          case CURRENT:
            state = ChangePinState.ENTER_CURRENT_PIN;
            break;
          case NEW_FIRST:
            state = ChangePinState.ENTER_NEW_PIN;
            break;
          default:
            throw new IllegalStateException("Should not reach here from " + requestType.name());
        }
        break;
      case CONFIRM_REMOVE_PIN:
        // User has confirmed the removal of the PIN
        state = ChangePinState.ENTER_CURRENT_PIN;
        break;
      case ENTER_CURRENT_PIN:
        // User has made another attempt at the current PIN
        switch (requestType) {
          case NEW_FIRST:
            state = ChangePinState.ENTER_NEW_PIN;
            break;
          default:
            throw new IllegalStateException("Should not reach here from " + requestType.name());
        }
        break;
      case ENTER_NEW_PIN:
        // User has confirmed the change of the PIN
        switch (requestType) {
          case NEW_SECOND:
            state = ChangePinState.CONFIRM_NEW_PIN;
            break;
          default:
            throw new IllegalStateException("Should not reach here from " + requestType.name());
        }
        break;
      case CONFIRM_NEW_PIN:
        break;
      case SHOW_REPORT:
        break;
      default:
        throw new IllegalStateException("Should not reach here from " + state.name());
    }


  }

  /**
   * Request a change or removal of the device PIN
   */
  public void requestRemovePin(final boolean removePin) {

    // Maintain state for the response
    this.removePin = removePin;

    // Communicate with the device off the EDT
    trezorRequestService.submit(
      new Runnable() {
        @Override
        public void run() {

          log.debug("Request '{}' PIN", removePin ? "remove" : "change");

          Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

          // Check if there is a wallet present
          if (hardwareWalletService.isPresent()) {

            // Request a PIN
            // AbstractHardwareWalletWizard will deal with the responses
            hardwareWalletService.get().changePIN(removePin);

          } else {
            if (removePin) {
              requestRemovePinPanelView.setOperationText(MessageKey.TREZOR_FAILURE_OPERATION);
            } else {
              requestChangePinPanelView.setOperationText(MessageKey.TREZOR_FAILURE_OPERATION);
            }
          }

        }
      });

  }

  /**
   * @param pinPositions The PIN positions providing some obfuscation
   */
  public void providePin(final String pinPositions) {

    // Start the requestRootNode
    ListenableFuture future = trezorRequestService.submit(
      new Callable<Boolean>() {

        @Override
        public Boolean call() throws Exception {

          Optional<HardwareWalletService> hardwareWalletServiceOptional = CoreServices.getOrCreateHardwareWalletService();

          if (hardwareWalletServiceOptional.isPresent()) {

            HardwareWalletService hardwareWalletService = hardwareWalletServiceOptional.get();

            if (hardwareWalletService.isWalletPresent()) {

              log.debug("Provide a PIN");
              hardwareWalletService.providePIN(pinPositions);

            } else {
              log.debug("No wallet present");
            }

          } else {
            log.error("No hardware wallet service");
          }

          // Must have successfully sent the message to be here
          return true;

        }

      });
    Futures.addCallback(
      future, new FutureCallback() {
        @Override
        public void onSuccess(@Nullable Object result) {

          // We successfully provided the PIN so wait for the result

        }

        @Override
        public void onFailure(Throwable t) {

          // Have a failure
          switch (state) {

            case ENTER_CURRENT_PIN:
            case ENTER_NEW_PIN:
            case CONFIRM_NEW_PIN:
              state = ChangePinState.SHOW_REPORT;
              reportMessageKey = MessageKey.TREZOR_INCORRECT_PIN_FAILURE;
              reportMessageStatus = false;
              break;
            default:
              throw new IllegalStateException("Should not reach here from " + state.name());
          }
        }

      });

  }

  @Override
  public void showOperationSucceeded(HardwareWalletEvent event) {

    switch (state) {

      case REQUEST_REMOVE_PIN:
        // No PIN present so fall through
      case ENTER_CURRENT_PIN:
        if (removePin) {
          state = ChangePinState.SHOW_REPORT;
          reportMessageKey = MessageKey.TREZOR_REMOVE_PIN_SUCCESS;
          reportMessageStatus = true;
        }
        // Update the features for next time
        CoreServices.getOrCreateHardwareWalletService().get().getContext().resetToAttached();
        break;
      case ENTER_NEW_PIN:
        state = ChangePinState.CONFIRM_NEW_PIN;
        break;
      case CONFIRM_NEW_PIN:
        state = ChangePinState.SHOW_REPORT;
        reportMessageKey = MessageKey.TREZOR_CHANGE_PIN_SUCCESS;
        reportMessageStatus = true;
        // Update the features for next time
        CoreServices.getOrCreateHardwareWalletService().get().getContext().resetToAttached();
        break;
      case SHOW_REPORT:
        break;
      default:
        throw new IllegalStateException("Should not reach here from " + state.name());

    }
  }

  @Override
  public void showOperationFailed(HardwareWalletEvent event) {

    switch (state) {

      case CONFIRM_ADD_PIN:
        state = ChangePinState.SELECT_OPTION;
        break;
      case CONFIRM_CHANGE_PIN:
        state = ChangePinState.SELECT_OPTION;
        break;
      case CONFIRM_REMOVE_PIN:
        state = ChangePinState.SELECT_OPTION;
        break;
      case ENTER_CURRENT_PIN:
      case ENTER_NEW_PIN:
      case CONFIRM_NEW_PIN:
        state = ChangePinState.SHOW_REPORT;
        reportMessageKey = MessageKey.TREZOR_INCORRECT_PIN_FAILURE;
        reportMessageStatus = false;
        break;
      default:
        throw new IllegalStateException("Should not reach here from " + state.name());

    }

  }
}
