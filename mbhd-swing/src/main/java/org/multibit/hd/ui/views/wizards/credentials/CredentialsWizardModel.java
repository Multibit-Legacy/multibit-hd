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
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.fsm.HardwareWalletContext;
import org.multibit.hd.hardware.core.messages.ButtonRequest;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.Success;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
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

  public CredentialsWizardModel(CredentialsState credentialsState, CredentialsRequestType credentialsRequestType) {
    super(credentialsState);
    this.credentialsRequestType = credentialsRequestType;
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  @Override
  public void showNext() {

    switch (state) {
      case CREDENTIALS_ENTER_PASSWORD:
        break;
      case CREDENTIALS_REQUEST_CIPHER_KEY:
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
      case CREDENTIALS_ENTER_PIN:
        // Indicate a successful PIN
        getEnterPinPanelView().setPinStatus(true, true);

        // Fall through to "press confirm for unlock"
      case CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK:

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {

            confirmCipherKeyPanelView.getTrezorDisplayView().setOperationText(MessageKey.COMMUNICATING_WITH_TREZOR);
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

          log.debug("Request the root node of the device");
          requestRootNode();

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
  public void receivedDeterministicHierarchy(HardwareWalletEvent event) {

    switch (state) {
      case CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK:

        // Ready to unlock the device wallet
        unlockWalletWithEntropy();

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
    hardwareWalletRequestService.submit(
      new Runnable() {
        @Override
        public void run() {
          log.debug("Performing a request cipher key to Trezor");

          // A 'requestCipherKey' is performed in which the user presses the OK button to encrypt a set text
          // (the result of which will be used to decrypt the wallet)
          Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

          // Check if there is a wallet present
          if (hardwareWalletService.isPresent()) {

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

          } else {
            requestCipherKeyPanelView. setOperationText(MessageKey.TREZOR_NO_WALLET_OPERATION);
          }

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

  /**
   * TODO Pull this (and others) up into base class and use the ListenableFuture for responses
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
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Ensure the view shows the spinner and disables components
        confirmCipherKeyPanelView.disableForUnlock();

      }
    });

    // Check the password (might take a while so do it asynchronously while showing a spinner)
    ListenableFuture<Optional<WalletSummary>> passwordFuture = unlockWalletService.submit(new Callable<Optional<WalletSummary>>() {

      @Override
      public Optional<WalletSummary> call() {

        // Need a very short delay here to allow the UI thread to update
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

        return getOrCreateTrezorWallet();

      }
    });
    Futures.addCallback(passwordFuture, new FutureCallback<Optional<WalletSummary>>() {

        @Override
        public void onSuccess(Optional<WalletSummary> result) {

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
            SwingUtilities.invokeLater(new Runnable() {
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

          // Ensure the view hides the spinner and enables components
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

              enterPasswordPanelView.enableForFailedUnlock();

            }
          });

          // Should not have seen an error
          ExceptionHandler.handleThrowable(t);
        }
      }
    );

  }


  /**
   * <p>Continue the hide process after user has entered a password and clicked unlock</p>
   */
  public void unlockWalletWithPassword() {

    // Start the spinner (we are deferring the hide)
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Ensure the view shows the spinner and disables components
        enterPasswordPanelView.disableForUnlock();

      }
    });

    // Check the password (might take a while so do it asynchronously while showing a spinner)
    // Tar pit (must be in a separate thread to ensure UI updates)
    ListenableFuture<Boolean> passwordFuture = unlockWalletService.submit(new Callable<Boolean>() {

      @Override
      public Boolean call() {

        // Need a very short delay here to allow the UI thread to update
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

        return checkPassword();

      }
    });
    Futures.addCallback(passwordFuture, new FutureCallback<Boolean>() {

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
            SwingUtilities.invokeLater(new Runnable() {
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

          // Ensure the view hides the spinner and enables components
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

              enterPasswordPanelView.enableForFailedUnlock();

            }
          });

          // Should not have seen an error
          ExceptionHandler.handleThrowable(t);
        }
      }
    );

  }

  /**
   * @return True if the selected wallet can be opened with the given password
   */
  private boolean checkPassword() {

    CharSequence password = enterPasswordPanelModel.getEnterPasswordModel().getValue();

    if (!"".equals(password)) {
      // Attempt to open the wallet
      WalletId walletId = enterPasswordPanelModel.getSelectWalletModel().getValue().getWalletId();
      try {
        WalletManager.INSTANCE.open(InstallationManager.getOrCreateApplicationDataDirectory(), walletId, password);
      } catch (WalletLoadException wle) {
        // Mostly this will be from a bad password
        log.error(wle.getMessage());
        // Assume bad credentials
        return false;
      }
      Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
      if (currentWalletSummary.isPresent()) {

        // Store this wallet in the current configuration
        String walletRoot = WalletManager.createWalletRoot(walletId);
        Configurations.currentConfiguration.getWallet().setCurrentWalletRoot(walletRoot);

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

          // Must be OK to be here

          return Optional.fromNullable(WalletManager.INSTANCE.getOrCreateWalletSummary(
            applicationDataDirectory,
            parentKey,
            // TODO The wizard should provide a suitable timestamp field for new wallets
            DateTime.parse(WalletManager.EARLIEST_HD_WALLET_DATE).getMillis() / 1000,
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

  public void setRequestCipherKeyPanelView(CredentialsRequestCipherKeyPanelView requestCipherKeyPanelView) {
    this.requestCipherKeyPanelView = requestCipherKeyPanelView;
  }

  public void setConfirmCipherKeyPanelView(CredentialsConfirmCipherKeyPanelView confirmCipherKeyPanelView) {
    this.confirmCipherKeyPanelView = confirmCipherKeyPanelView;
  }

  public void setEnterPasswordPanelView(CredentialsEnterPasswordPanelView enterPasswordPanelView) {
    this.enterPasswordPanelView = enterPasswordPanelView;
  }

  /**
   * @return The entropy provided by the Trezor to allow identification of the wallet
   */
  public Optional<byte[]> getEntropy() {
    return entropy;
  }

}
