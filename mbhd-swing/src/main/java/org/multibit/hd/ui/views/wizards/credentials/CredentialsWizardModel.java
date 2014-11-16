package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.KeyChain;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.fsm.HardwareWalletContext;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.Success;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.io.File;
import java.util.concurrent.Callable;

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
   * The "enter pin" panel model
   */
  private CredentialsEnterPinPanelModel enterPinPanelModel;

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
  public void showOperationSucceeded(HardwareWalletEvent event) {

    switch (state) {
      case CREDENTIALS_ENTER_PIN:
        // Indicate a successful PIN
        getEnterPinPanelView().setPinStatus(true, true);

        // Fall through to "press confirm for unlock"
      case CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK:

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

  /**
   * Request a cipher key from the device
   */
  public void requestCipherKey() {

    // Communicate with the device off the EDT
    trezorRequestService.submit(
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
            // TODO Require MessageKey
            getRequestCipherKeyPanelView().setMessage("No wallet is present on the device");
          }

        }
      });

  }

  /**
   * @param pinPositions The PIN positions providing a level of obfuscation to protect the PIN
   */
  public void requestPinCheck(final String pinPositions) {

    ListenableFuture<Boolean> pinCheckFuture = trezorRequestService.submit(
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
   * <p>Create a Trezor wallet based on the </p>
   * @return True if the wallet was created successfully
   */
  private boolean handleTrezorCreateWallet() {

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
            return false;
          }

          // The entropy is used as the password of the Trezor wallet (so the user does not need to remember it
          log.debug("Running decrypt of Trezor wallet with entropy of length {}", entropy.get().length);

          // Locate the installation directory
          final File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

          // Must be OK to be here

          WalletManager.INSTANCE.getOrCreateWalletSummary(
            applicationDataDirectory,
            parentKey,
            // TODO The wizard should provide a suitable timestamp field for new wallets
            Dates.parseSeedTimestamp("2101/64").getMillis() / 1000,
            Hex.toHexString(entropy.get()),
            label, "Trezor");

          // Trigger the deferred hide
          ViewEvents.fireWizardDeferredHideEvent(getPanelName(), false);

          return true;

        } catch (Exception e) {

          e.printStackTrace();
        }


      } else {
        log.debug("No wallet present");
      }
    } else {
      log.error("No hardware wallet service");
    }
    return false;
  }


  /**
   * @return The credentials the user entered
   */
  public String getCredentials() {
    switch (credentialsRequestType) {
      case PASSWORD:
        return enterPasswordPanelModel.getEnterPasswordModel().getValue();
      case TREZOR_CIPHER_KEY:
        return enterPinPanelModel.getEnterPinModel().getValue();
      default:
        return "";
    }
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param enterPasswordPanelModel The "enter credentials" panel model
   */
  void setEnterPasswordPanelModel(CredentialsEnterPasswordPanelModel enterPasswordPanelModel) {
    this.enterPasswordPanelModel = enterPasswordPanelModel;
  }

  public CredentialsRequestType getCredentialsRequestType() {
    return credentialsRequestType;
  }

  public CredentialsEnterPinPanelModel getEnterPinPanelModel() {
    return enterPinPanelModel;
  }

  public void setEnterPinPanelModel(CredentialsEnterPinPanelModel enterPinPanelModel) {
    this.enterPinPanelModel = enterPinPanelModel;
  }

  public CredentialsEnterPasswordPanelModel getEnterPasswordPanelModel() {
    return enterPasswordPanelModel;
  }

  public CredentialsEnterPinPanelView getEnterPinPanelView() {
    return enterPinPanelView;
  }

  public void setEnterPinPanelView(CredentialsEnterPinPanelView enterPinPanelView) {
    this.enterPinPanelView = enterPinPanelView;
  }

  public CredentialsRequestCipherKeyPanelView getRequestCipherKeyPanelView() {
    return requestCipherKeyPanelView;
  }

  public void setRequestCipherKeyPanelView(CredentialsRequestCipherKeyPanelView requestCipherKeyPanelView) {
    this.requestCipherKeyPanelView = requestCipherKeyPanelView;
  }

  /**
   * @return The entropy provided by the Trezor to allow identification of the wallet
   */
  public Optional<byte[]> getEntropy() {
    return entropy;
  }

}
