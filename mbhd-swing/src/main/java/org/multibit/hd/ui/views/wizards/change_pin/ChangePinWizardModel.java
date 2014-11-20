package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
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
import org.multibit.hd.hardware.core.messages.ButtonRequestType;
import org.multibit.hd.hardware.core.messages.PinMatrixRequest;
import org.multibit.hd.hardware.core.messages.PinMatrixRequestType;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.multibit.hd.ui.views.wizards.use_trezor.UseTrezorState;
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
   * The "change PIN" panel model
   */
  private ChangePinEnterPinPanelModel changePinPanelModel;

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
   * @return The PIN the user entered (must be able to unlock the current device)
   */
  public String getCurrentPin() {
    return changePinPanelModel.getEnterPinModel().getValue();
  }

  /**
   * @return The new PIN
   */
  public String getNewPin() {
    return changePinPanelModel.getEnterPinModel().getValue();
  }

  /**
   * @return The confirmed PIN
   */
  public String getConfirmedPin() {
    return changePinPanelModel.getEnterPinModel().getValue();
  }

  /**
   * @return True if the PIN is to be removed
   */
  public boolean isRemovePin() {
    return removePin;
  }

  public void setRemovePin(boolean removePin) {
    this.removePin = removePin;
  }

  public ChangePinRequestChangePinPanelView getRequestChangePinPanelView() {
    return requestChangePinPanelView;
  }

  public void setRequestChangePinPanelView(ChangePinRequestChangePinPanelView requestChangePinPanelView) {
    this.requestChangePinPanelView = requestChangePinPanelView;
  }

  public ChangePinRequestRemovePinPanelView getRequestRemovePinPanelView() {
    return requestRemovePinPanelView;
  }

  public void setRequestRemovePinPanelView(ChangePinRequestRemovePinPanelView requestRemovePinPanelView) {
    this.requestRemovePinPanelView = requestRemovePinPanelView;
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param changePinPanelModel The "enter PIN" panel model
   */
  void setChangePinPanelModel(ChangePinEnterPinPanelModel changePinPanelModel) {
    this.changePinPanelModel = changePinPanelModel;
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
        state = isRemovePin() ? ChangePinState.REQUEST_REMOVE_PIN: ChangePinState.REQUEST_CHANGE_PIN;
        break;
      case REQUEST_REMOVE_PIN:
        state = ChangePinState.SHOW_REPORT;
        break;
      case REQUEST_CHANGE_PIN:
        break;
      case ENTER_CURRENT_PIN:
        state = ChangePinState.ENTER_NEW_PIN;
        break;
      case ENTER_NEW_PIN:
        break;
      case CONFIRM_NEW_PIN:
        break;
      case SHOW_REPORT:
        state = ChangePinState.SHOW_REPORT;
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }

  @Override
  public void showButtonPress(HardwareWalletEvent event) {

    // Determine if this is the first or second PIN entry
    ButtonRequest request = (ButtonRequest) event.getMessage().get();
    ButtonRequestType requestType = request.getButtonRequestType();

    // The button request could have come about from many possible paths
    switch (state) {
      case SELECT_OPTION:
        break;
      case REQUEST_REMOVE_PIN:
        // Must be the device asking to confirm
        getRequestRemovePinPanelView().setDeviceText(Languages.safeText(MessageKey.TREZOR_REMOVE_PIN_TEXT));
        break;
      case REQUEST_CHANGE_PIN:
        break;
      case ENTER_CURRENT_PIN:
        break;
      case ENTER_NEW_PIN:
        break;
      case CONFIRM_NEW_PIN:
        break;
      case SHOW_REPORT:
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }

  }

  @Override
  public void showPINEntry(HardwareWalletEvent event) {

    // Determine if this is the first or second PIN entry
    PinMatrixRequest request = (PinMatrixRequest) event.getMessage().get();
    PinMatrixRequestType requestType = request.getPinMatrixRequestType();

    // The PIN entry could have come about from many possible paths
    switch (state) {
      case SELECT_OPTION:
        break;
      case REQUEST_REMOVE_PIN:
        // User has confirmed the removal and has a current PIN
        state = ChangePinState.ENTER_CURRENT_PIN;
        break;
      case REQUEST_CHANGE_PIN:
        break;
      case ENTER_CURRENT_PIN:
        break;
      case ENTER_NEW_PIN:
        break;
      case CONFIRM_NEW_PIN:
        break;
      case SHOW_REPORT:
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }


  }


  /**
   * Request a change or removal of the device PIN
   */
  public void requestChangeOrRemovePin() {

    // Communicate with the device off the EDT
    trezorRequestService.submit(
      new Runnable() {
        @Override
        public void run() {
          log.debug("Performing a request PIN to Trezor");

          // A 'requestPin' is performed in which the user provides a new PIN
          Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

          // Check if there is a wallet present
          if (hardwareWalletService.isPresent()) {

            // Request a PIN
            // AbstractHardwareWalletWizard will deal with the responses
            hardwareWalletService.get().changePIN(isRemovePin());

          } else {
            // TODO Require MessageKey
            getRequestChangePinPanelView().setMessage("No wallet is present on the device");
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

          // We successfully requested the deterministic hierarchy so throw a ComponentChangedEvent for the UI to update
          ViewEvents.fireComponentChangedEvent(UseTrezorState.USE_TREZOR_REPORT_PANEL.name(), Optional.absent());

        }

        @Override
        public void onFailure(Throwable t) {

          // Have a failure
          ExceptionHandler.handleThrowable(t);
        }

      });

  }

}
