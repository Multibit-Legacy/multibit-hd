package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.KeyChain;
import org.joda.time.DateTime;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.fsm.HardwareWalletContext;
import org.multibit.hd.hardware.core.messages.*;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.ViewKey;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <p>Model object to provide the following to "credentials wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class CredentialsWizardModel extends AbstractHardwareWalletWizardModel<CredentialsState> {

  private static final Logger log = LoggerFactory.getLogger(CredentialsWizardModel.class);

  /**
   * The "enter password" panel model
   */
  private CredentialsEnterPasswordPanelModel enterPasswordPanelModel;

  /**
   * The type of credentials being requested password/ Trezor PIN / no Trezor PIN
   */
  private final CredentialsRequestType credentialsRequestType;

  /**
   * The "enter PIN" panel view
   */
  private CredentialsEnterPinPanelView enterPinPanelView;

  /**
   * The "request master public key" panel node
   */
  private CredentialsRequestMasterPublicKeyPanelView requestMasterPublicKeyPanelView;

  /**
   * The "request cipher key" panel view
   */
  private CredentialsRequestCipherKeyPanelView requestCipherKeyPanelView;

  /**
   * The "confirm cipher key" panel view
   */
  private CredentialsConfirmCipherKeyPanelView confirmCipherKeyPanelView;

  /**
   * The "enter password" panel view
   */
  private CredentialsEnterPasswordPanelView enterPasswordPanelView;

  /**
   * The unlock wallet executor service
   */
  private ListeningExecutorService unlockWalletService = SafeExecutors.newSingleThreadExecutor("unlock-wallet");

  /**
   * The entropy to be used for the wallet ID (result of encryption by the Trezor of fixed text)
   */
  Optional<byte[]> entropy = Optional.absent();

  /**
   * True if a Trezor failure has occurred that necessitates a switch to password entry
   */
  private boolean switchToPassword;

  public CredentialsWizardModel(CredentialsState credentialsState, CredentialsRequestType credentialsRequestType) {
    super(credentialsState);
    this.credentialsRequestType = credentialsRequestType;
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @param switchToPassword True if there is a need to switch to password entry through showNext()
   */
  public void setSwitchToPassword(boolean switchToPassword) {
    this.switchToPassword = switchToPassword;
  }

  @Override
  public void showNext() {

    switch (state) {
      case CREDENTIALS_ENTER_PASSWORD:
        break;
      case CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY:
        break;
      case CREDENTIALS_REQUEST_CIPHER_KEY:
        if (switchToPassword) {
          state = CredentialsState.CREDENTIALS_ENTER_PASSWORD;
        }
        break;
      case CREDENTIALS_ENTER_PIN:
        break;
      case CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK:
        break;
      case CREDENTIALS_RESTORE:
        break;
      default:
        throw new IllegalStateException("Cannot showNext with a state of " + state);
    }
  }

  @Override
  public void showButtonPress(HardwareWalletEvent event) {

    log.debug("Received hardwareWalletEvent {}", event);

    ButtonRequest buttonRequest = (ButtonRequest) event.getMessage().get();

    switch (state) {
      case CREDENTIALS_ENTER_PIN:
        // Should be catered for by finish
        state = CredentialsState.CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK;
        break;
      case CREDENTIALS_REQUEST_CIPHER_KEY:
        switch (buttonRequest.getButtonRequestType()) {
          case OTHER:
            // Device requires confirmation to provide cipher key
            state = CredentialsState.CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK;
            break;
          case PROTECT_CALL:
            // Device requires PIN before providing cipher key
            state = CredentialsState.CREDENTIALS_ENTER_PIN;
            break;
          default:
            throw new IllegalStateException("Unexpected button: " + buttonRequest.getButtonRequestType().name());
        }
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }

  }

  @Override
  public void showPINEntry(HardwareWalletEvent event) {

    // Device is PIN protected

    switch (state) {
      case CREDENTIALS_REQUEST_CIPHER_KEY:
        state = CredentialsState.CREDENTIALS_ENTER_PIN;
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }


  @Override
  public void showOperationSucceeded(HardwareWalletEvent event) {

    switch (state) {
      case CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY:
        // A successful get master public key has been performed
        log.debug("CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY was successful");
        break;
      case CREDENTIALS_ENTER_PIN:
        // Indicate a successful PIN
        getEnterPinPanelView().setPinStatus(true, true);

        // Fall through to "press confirm for unlock"
      case CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK:

        SwingUtilities.invokeLater(
          new Runnable() {
            @Override
            public void run() {

              confirmCipherKeyPanelView.getTrezorDisplayView().setOperationText(MessageKey.COMMUNICATING_WITH_TREZOR_OPERATION);
              confirmCipherKeyPanelView.setDisplayVisible(false);
              confirmCipherKeyPanelView.getTrezorDisplayView().setSpinnerVisible(true);

            }
          });

        if (event.getMessage().get() instanceof Success) {

          byte[] payload = ((Success) event.getMessage().get()).getPayload();
          String message = ((Success) event.getMessage().get()).getMessage();

          log.info(
            "Message:'{}'\nPayload length: {}",
            message,
            payload == null ? 0 : payload.length
          );

          log.debug("Using the payload as entropy");
          entropy = Optional.fromNullable(payload);

          // Ready to unlock the device wallet
          log.debug("Calling unlockWalletWithEntropy");
          unlockWalletWithEntropy();
        }
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

    final Failure failure = (Failure) event.getMessage().get();
    log.debug("A failure event has occurred {}", failure);

    switch (state) {
      case CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY:
        // An unsuccessful get master public key has been performed
        log.debug("CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY was unsuccessful");
        break;
      case CREDENTIALS_ENTER_PIN:
        // User entered incorrect PIN so should start again
        state = CredentialsState.CREDENTIALS_REQUEST_CIPHER_KEY;
        break;
      default:

        if (FailureType.ACTION_CANCELLED.equals(failure.getType())) {
          // User is backing out of using their device (switch to password)
          state = CredentialsState.CREDENTIALS_ENTER_PASSWORD;
        } else {
          // Something has gone wrong with the device
          state = CredentialsState.CREDENTIALS_REQUEST_CIPHER_KEY;
        }
    }

    setIgnoreHardwareWalletEventsThreshold(Dates.nowUtc().plusSeconds(1));

  }

  @Override
  public void showDeviceReady(HardwareWalletEvent event) {

    if (Dates.nowUtc().isAfter(getIgnoreHardwareWalletEventsThreshold())) {
      // User attached an operational device in place of whatever
      // they are currently doing so start again
      state = CredentialsState.CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY;
    }

  }

  @Override
  public void receivedDeterministicHierarchy(HardwareWalletEvent event) {

    switch (state) {
      case CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY:
        // A successful get master public key has been performed
        log.debug("CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY was successful");

        // Transition to request a cipher key (to provide entropy).
        // This will most likely trigger a PIN request
        state = CredentialsState.CREDENTIALS_REQUEST_CIPHER_KEY;;
        break;

      default:
        // TODO Fill in the other states and provide success feedback
        log.info(
          "Message:'Operation succeeded'\n{}",
          event.getMessage().get()
        );
    }

  }

  /**
   * Request a cipher key from the device
   */
  public void requestCipherKey() {

    // Communicate with the device off the EDT
    ListenableFuture<Boolean> requestCipherKeyFuture = hardwareWalletRequestService.submit(
      new Callable<Boolean>() {
        @Override
        public Boolean call() {
          log.debug("Performing a request cipher key to Trezor");

          // Provide a short delay to allow UI to update
          Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

          // A 'requestCipherKey' is performed in which the user presses the OK button to encrypt a set text
          // (the result of which will be used to decrypt the wallet)
          Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

          // Check if there is a wallet present
          if (hardwareWalletService.get().isWalletPresent()) {

            // Use this layout to ensure line wrapping occurs on a V1 Trezor
            byte[] key = "MultiBit HD     Unlock".getBytes();
            byte[] keyValue = "0123456789abcdef".getBytes();

            // Request a cipher key against 0'/0/0
            // AbstractHardwareWalletWizard will deal with the responses
            hardwareWalletService.get().requestCipherKey(
              0,
              KeyChain.KeyPurpose.RECEIVE_FUNDS,
              0,
              key,
              keyValue,
              true,
              true,
              true
            );

          }

          // Completed
          return true;

        }
      });
    Futures.addCallback(
      requestCipherKeyFuture, new FutureCallback<Boolean>() {

        @Override
        public void onSuccess(Boolean result) {

          // Do nothing - message was successfully relayed to the device

        }

        @Override
        public void onFailure(Throwable t) {

          // Failed to send the message
          requestCipherKeyPanelView.setOperationText(MessageKey.TREZOR_FAILURE_OPERATION);
        }
      }
    );

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

          // Failed to send the message
          enterPinPanelView.failedPin();
        }
      }
    );

  }

  /**
   * Request the root node for the Trezor HD wallet as a deterministic hierarchy
   */
  public void requestRootNode() {

    // Start the requestRootNode
    ListenableFuture future = hardwareWalletRequestService.submit(
      new Callable<Boolean>() {

        @Override
        public Boolean call() throws Exception {

          Optional<HardwareWalletService> hardwareWalletServiceOptional = CoreServices.getOrCreateHardwareWalletService();

          if (hardwareWalletServiceOptional.isPresent()) {

            HardwareWalletService hardwareWalletService = hardwareWalletServiceOptional.get();

            if (hardwareWalletService.isWalletPresent()) {

              log.debug("Request the deterministic hierarchy for the Trezor account");
              hardwareWalletService.requestDeterministicHierarchy(
                Lists.newArrayList(
                  new ChildNumber(44 | ChildNumber.HARDENED_BIT),
                  ChildNumber.ZERO_HARDENED,
                  ChildNumber.ZERO_HARDENED
                ));

              log.debug("Request deterministic hierarchy has been performed");

              // The "receivedDeterministicHierarchy" response is dealt with in the wizard model

            } else {
              log.debug("No wallet present");
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

          // Succeeded in sending the root node message

        }

        @Override
        public void onFailure(Throwable t) {

          // Failed to send the message
          requestCipherKeyPanelView.setOperationText(MessageKey.TREZOR_FAILURE_OPERATION);
        }

      });
  }

  /**
   * <p>Continue the hide process after user has confirmed entropy and we have a deterministic hierarchy</p>
   */
  private void unlockWalletWithEntropy() {

    // Start the spinner (we are deferring the hide)
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Hide the header view (switching back on is done in MainController#onBitcoinNetworkChangedEvent
          ViewEvents.fireViewChangedEvent(ViewKey.HEADER, false);

          // Ensure the view shows the spinner and disables components
          confirmCipherKeyPanelView.disableForUnlock();

        }
      });

    // Check the password (might take a while so do it asynchronously while showing a spinner)
    ListenableFuture<Optional<WalletSummary>> passwordFuture = unlockWalletService.submit(
      new Callable<Optional<WalletSummary>>() {

        @Override
        public Optional<WalletSummary> call() {

          // Need a very short delay here to allow the UI thread to update
          Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

          return getOrCreateTrezorWallet();

        }
      });
    Futures.addCallback(
      passwordFuture, new FutureCallback<Optional<WalletSummary>>() {

        @Override
        public void onSuccess(Optional<WalletSummary> result) {
          log.debug("Result: {}", result);
          // Check the result
          if (result.isPresent()) {

            // Maintain the spinner while the initialisation continues

            // Trigger the deferred hide
            ViewEvents.fireWizardDeferredHideEvent(getPanelName(), false);

          } else {

            // Wait just long enough to be annoying (anything below 2 seconds is comfortable)
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

            // Failed
            Sounds.playBeep();

            // Ensure the view hides the spinner and enables components
            SwingUtilities.invokeLater(
              new Runnable() {
                @Override
                public void run() {

                  confirmCipherKeyPanelView.incorrectEntropy();
                  confirmCipherKeyPanelView.enableForFailedUnlock();

                }
              });

          }

        }

        @Override
        public void onFailure(Throwable t) {
          t.printStackTrace();

          // Ensure the view hides the spinner and enables components
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                confirmCipherKeyPanelView.incorrectEntropy();
                confirmCipherKeyPanelView.enableForFailedUnlock();

              }
            });

        }
      }
    );

  }


  /**
   * <p>Continue the hide process after user has entered a password and clicked unlock</p>
   */
  public void unlockWalletWithPassword() {

    // Start the spinner (we are deferring the hide)
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          // Hide the header view (switching back on is done in MainController#onBitcoinNetworkChangedEvent
          ViewEvents.fireViewChangedEvent(ViewKey.HEADER, false);

          // Ensure the view shows the spinner and disables components
          enterPasswordPanelView.disableForUnlock();

        }
      });

    // Check the password (might take a while so do it asynchronously while showing a spinner)
    // Tar pit (must be in a separate thread to ensure UI updates)
    ListenableFuture<Boolean> passwordFuture = unlockWalletService.submit(
      new Callable<Boolean>() {

        @Override
        public Boolean call() {

          // Need a very short delay here to allow the UI thread to update
          Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

          return checkPasswordAndLoadWallet();

        }
      });
    Futures.addCallback(
      passwordFuture, new FutureCallback<Boolean>() {

        @Override
        public void onSuccess(Boolean result) {
          // Check the result
          if (result) {

            // Maintain the spinner while the initialisation continues

            // Trigger the deferred hide
            ViewEvents.fireWizardDeferredHideEvent(getPanelName(), false);

          } else {

            // Wait just long enough to be annoying (anything below 2 seconds is comfortable)
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

            // Failed
            Sounds.playBeep();

            // Ensure the view hides the spinner and enables components
            SwingUtilities.invokeLater(
              new Runnable() {
                @Override
                public void run() {

                  enterPasswordPanelView.incorrectPassword();
                  enterPasswordPanelView.enableForFailedUnlock();

                }
              });

          }

        }

        @Override
        public void onFailure(Throwable t) {


          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                // Ensure the view hides the spinner and enables components
                enterPasswordPanelView.enableForFailedUnlock();

              }
            });

        }
      }
    );

  }

  /**
   * Check the password and load the wallet
   *
   * @return True if the selected wallet can be opened with the given password
   */
  private boolean checkPasswordAndLoadWallet() {
    CharSequence password = enterPasswordPanelModel.getEnterPasswordModel().getValue();

    if (!"".equals(password)) {
      // Attempt to open the wallet to check the password
      WalletId walletId = enterPasswordPanelModel.getSelectWalletModel().getValue().getWalletId();
      Optional<WalletSummary> currentWalletSummary;
      try {
        currentWalletSummary = WalletManager.INSTANCE.openWalletFromWalletId(InstallationManager.getOrCreateApplicationDataDirectory(), walletId, password);
      } catch (WalletLoadException wle) {
        // Mostly this will be from a bad password
        log.error(wle.getMessage());
        // Assume bad credentials
        return false;
      }

      if (currentWalletSummary != null && currentWalletSummary.isPresent()) {

        // Store this wallet in the current configuration
        String walletRoot = WalletManager.createWalletRoot(walletId);
        Configurations.currentConfiguration.getWallet().setLastSoftWalletRoot(walletRoot);

        // Update the wallet data
        WalletSummary walletSummary = currentWalletSummary.get();
        walletSummary.setPassword(password);

        // Create the history service
        CoreServices.getOrCreateHistoryService(walletSummary.getWalletId());

        // Must have succeeded to be here
        CoreServices.logHistory(Languages.safeText(MessageKey.PASSWORD_VERIFIED));

        return true;
      }

    }

    // Must have failed to be here
    log.error("Failed attempt to open wallet");

    return false;

  }

  /**
   * <p>Get or create a Trezor wallet based on the entropy and deterministic hierarchy obtained earlier</p>
   *
   * @return The wallet summary, present if the wallet was created/opened successfully
   */
  private Optional<WalletSummary> getOrCreateTrezorWallet() {

    Optional<HardwareWalletService> hardwareWalletServiceOptional = CoreServices.getOrCreateHardwareWalletService();
    if (hardwareWalletServiceOptional.isPresent()) {

      HardwareWalletService hardwareWalletService = hardwareWalletServiceOptional.get();

      if (hardwareWalletService.isWalletPresent()) {

        HardwareWalletContext hardwareWalletContext = hardwareWalletService.getContext();
        // Parent key should be M/44'/0'/0'
        final DeterministicKey parentKey = hardwareWalletContext.getDeterministicKey().get();
        log.info("Parent key path: {}", parentKey.getPathAsString());

        // Verify the deterministic hierarchy can derive child keys
        // In this case 0/0 from a parent of M/44'/0'/0'
        DeterministicHierarchy hierarchy = hardwareWalletContext.getDeterministicHierarchy().get();
        DeterministicKey childKey = hierarchy.deriveChild(
          Lists.newArrayList(
            ChildNumber.ZERO
          ),
          true,
          true,
          ChildNumber.ZERO
        );

        // Calculate the address
        ECKey seedKey = ECKey.fromPublicOnly(childKey.getPubKey());
        Address walletKeyAddress = new Address(MainNetParams.get(), seedKey.getPubKeyHash());

        log.info("Path {}/0/0 has address: '{}'", parentKey.getPathAsString(), walletKeyAddress.toString());

        // Get the label of the Trezor from the features to use as the wallet name
        Optional<Features> features = hardwareWalletContext.getFeatures();
        log.debug("Features: {}", features);
        final String label;
        if (features.isPresent()) {
          label = features.get().getLabel();
        } else {
          label = "";
        }

        try {
          if (!entropy.isPresent()) {
            log.error("No entropy from Trezor so cannot create or load a wallet.");
            return Optional.absent();
          }

          // The entropy is used as the password of the Trezor wallet (so the user does not need to remember it
          log.debug("Running decrypt of Trezor wallet with entropy of length {}", entropy.get().length);

          String newWalletPassword = Hex.toHexString(entropy.get());

          // Locate the installation directory
          final File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

          // Work out if the wallet is a brand new Trezor wallet
          // if the label is the same and the data validity time is within a few minutes of now then we use the
          // data validity time as the replay date
          long replayDateInMillis = DateTime.parse(WalletManager.EARLIEST_HD_WALLET_DATE).getMillis();
          if (label.equals(Configurations.currentConfiguration.getWallet().getRecentWalletLabel())) {
            long now = System.currentTimeMillis();
            long dataValidityTime = Configurations.currentConfiguration.getWallet().getRecentWalletDataValidity();
            if (now - dataValidityTime <= WalletManager.MAXIMUM_WALLET_CREATION_DELTA) {
              replayDateInMillis = dataValidityTime;
              log.debug("Using a replayDate for brand new Trezor of {}", replayDateInMillis);
            }
          }

          // Must be OK to be here

          return Optional.fromNullable(
            WalletManager.INSTANCE.getOrCreateTrezorHardWalletSummaryFromRootNode(
              applicationDataDirectory,
              parentKey,
              // There is no reliable timestamp for a 'new' wallet as it could exist elsewhere
              replayDateInMillis / 1000,
              newWalletPassword,
              label, "Trezor"));

        } catch (Exception e) {

          log.error(e.getMessage(), e);

        }


      } else {
        log.debug("No wallet present");
      }
    } else {
      log.error("No hardware wallet service");
    }
    return Optional.absent();
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param enterPasswordPanelModel The "enter credentials" panel model
   */
  void setEnterPasswordPanelModel(CredentialsEnterPasswordPanelModel enterPasswordPanelModel) {
    this.enterPasswordPanelModel = enterPasswordPanelModel;
  }

  public CredentialsEnterPinPanelView getEnterPinPanelView() {
    return enterPinPanelView;
  }

  public void setEnterPinPanelView(CredentialsEnterPinPanelView enterPinPanelView) {
    this.enterPinPanelView = enterPinPanelView;
  }

  public void setRequestMasterPublicKeyPanelView(CredentialsRequestMasterPublicKeyPanelView requestMasterPublicKeyPanelView) {
    this.requestMasterPublicKeyPanelView = requestMasterPublicKeyPanelView;
  }

  public void setRequestCipherKeyPanelView(CredentialsRequestCipherKeyPanelView requestCipherKeyPanelView) {
    this.requestCipherKeyPanelView = requestCipherKeyPanelView;
  }

  public void setConfirmCipherKeyPanelView(CredentialsConfirmCipherKeyPanelView confirmCipherKeyPanelView) {
    this.confirmCipherKeyPanelView = confirmCipherKeyPanelView;
  }

  public void setEnterPasswordPanelView(CredentialsEnterPasswordPanelView enterPasswordPanelView) {
    this.enterPasswordPanelView = enterPasswordPanelView;
  }

}
