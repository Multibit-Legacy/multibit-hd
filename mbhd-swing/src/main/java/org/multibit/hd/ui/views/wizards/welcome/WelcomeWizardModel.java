package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import org.multibit.hd.brit.exceptions.SeedPhraseException;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseSize;
import org.multibit.hd.core.dto.BackupSummary;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.fsm.HardwareWalletContext;
import org.multibit.hd.hardware.core.messages.ButtonRequest;
import org.multibit.hd.hardware.core.messages.Failure;
import org.multibit.hd.hardware.core.messages.PinMatrixRequest;
import org.multibit.hd.hardware.core.messages.PinMatrixRequestType;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.select_backup_summary.SelectBackupSummaryModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.multibit.hd.ui.views.wizards.welcome.create_trezor_wallet.CreateTrezorWalletConfirmWordPanelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState.*;

/**
 * <p>Model object to provide the following to welcome wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class WelcomeWizardModel extends AbstractHardwareWalletWizardModel<WelcomeWizardState> {

  private static final Logger log = LoggerFactory.getLogger(WelcomeWizardModel.class);

  /**
   * The "select wallet" radio button choice (as a state)
   */
  private WelcomeWizardState selectWalletChoice = WelcomeWizardState.CREATE_WALLET_SEED_PHRASE;

  /**
   * The optional "selected wallet ID" choice
   */
  private Optional<WalletId> walletId = Optional.absent();

  /**
   * The "restore method" indicates if a backup location or timestamp was selected
   */
  private WelcomeWizardState restoreMethod = WelcomeWizardState.RESTORE_WALLET_SELECT_BACKUP_LOCATION;

  /**
   * The seed phrase generator
   */
  private final SeedPhraseGenerator seedPhraseGenerator;

  /**
   * The confirm credentials model
   */
  private ConfirmPasswordModel confirmPasswordModel;

  private SelectFileModel cloudBackupLocationSelectFileModel;
  private SelectFileModel restoreLocationSelectFileModel;

  private EnterSeedPhraseModel createWalletEnterSeedPhraseModel;
  private EnterSeedPhraseModel restorePasswordEnterSeedPhraseModel;
  private EnterSeedPhraseModel restoreWalletEnterSeedPhraseModel;
  private EnterSeedPhraseModel restoreWalletBackupSeedPhraseModel;

  private List<String> createWalletSeedPhrase = Lists.newArrayList();
  private List<String> restoreWalletSeedPhrase = Lists.newArrayList();

  private final Random random = new Random();
  private final boolean restoring;
  private final WelcomeWizardMode mode;

  private String actualSeedTimestamp;
  // Backup summaries for restoring a wallet
  private List<BackupSummary> backupSummaries = Lists.newArrayList();
  private SelectBackupSummaryModel selectBackupSummaryModel;
  private EnterSeedPhraseModel restoreWalletEnterTimestampModel;

  private ConfirmPasswordModel restoreWalletConfirmPasswordModel;
  private String trezorWalletLabel = "multibit.org " + random.nextInt(1000);
  private SeedPhraseSize trezorSeedSize = SeedPhraseSize.TWELVE_WORDS;

  private String mostRecentPin;
  private CreateTrezorWalletConfirmWordPanelView trezorConfirmWordPanelView;

  private int trezorWordCount = 0;
  private boolean trezorChecking = false;

  /**
   * @param state The state object
   * @param mode  The mode (e.g. standard, Trezor etc)
   */
  public WelcomeWizardModel(WelcomeWizardState state, WelcomeWizardMode mode) {
    super(state);

    log.debug("Welcome wizard starting in state '{}'", state.name());

    this.seedPhraseGenerator = CoreServices.newSeedPhraseGenerator();
    this.restoring = WelcomeWizardState.WELCOME_SELECT_WALLET.equals(state);
    this.mode = mode;

  }

  @Override
  public void showNext() {
    Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

    switch (state) {
      case WELCOME_LICENCE:
        state = WELCOME_SELECT_LANGUAGE;
        break;
      case WELCOME_SELECT_LANGUAGE:
        state = WELCOME_SELECT_WALLET;
        break;
      case WELCOME_SELECT_WALLET:
        if (RESTORE_WALLET_SELECT_BACKUP.equals(selectWalletChoice)) {
          if (hardwareWalletService.isPresent() && hardwareWalletService.get().isWalletPresent()) {
            // User has selected restore wallet - see if wallet is hard Trezor wallet
            // If so no need to enter a seed phrase - use the rootNode from the master public key to work out the wallet id
            HardwareWalletContext context = hardwareWalletService.get().getContext();
            // Create a wallet id from the rootNode to work out the wallet root directory
            if (context.getDeterministicKey().isPresent()) {
              walletId = Optional.of(new WalletId(context.getDeterministicKey().get().getIdentifier()));
              String walletRoot = WalletManager.createWalletRoot(walletId.get());
              log.debug("Hardware wallet root : {}", walletRoot);
            }
            // Ensure Trezor is cancelled
            hardwareWalletService.get().requestCancel();

            restoreMethod = RESTORE_WALLET_HARD_TREZOR;
            if (!isLocalZipBackupPresent()) {
              // Next ask for the cloud backup location
              state = RESTORE_WALLET_SELECT_BACKUP_LOCATION;
            } else {
              // Next select one of the local backups
              state = RESTORE_WALLET_SELECT_BACKUP;
            }
            break;
          }
        } else {
          // Ensure Trezor is reset if it is attached and initialised
          if (hardwareWalletService.isPresent()
            && hardwareWalletService.get().getContext().getFeatures().isPresent()) {
            hardwareWalletService.get().requestCancel();
            hardwareWalletService.get().getContext().resetToConnected();
          }
          state = selectWalletChoice;
        }
        break;
      case SELECT_WALLET_HARDWARE:
        state = selectWalletChoice;
        break;
      case CREATE_WALLET_PREPARATION:
        state = CREATE_WALLET_SELECT_BACKUP_LOCATION;
        break;
      case CREATE_WALLET_SELECT_BACKUP_LOCATION:
        state = CREATE_WALLET_SEED_PHRASE;
        break;
      case CREATE_WALLET_SEED_PHRASE:
        state = CREATE_WALLET_CONFIRM_SEED_PHRASE;
        // Fail safe to ensure that the generator hasn't gone screwy
        Preconditions.checkState(SeedPhraseSize.isValid(getCreateWalletSeedPhrase().size()), "'actualSeedPhrase' is not a valid length");
        break;
      case CREATE_WALLET_CONFIRM_SEED_PHRASE:
        state = CREATE_WALLET_CREATE_PASSWORD;
        break;
      case CREATE_WALLET_CREATE_PASSWORD:
        state = CREATE_WALLET_REPORT;
        break;
      case CREATE_WALLET_REPORT:
        throw new IllegalStateException("'Next' is not permitted here");
      case TREZOR_CREATE_WALLET_PREPARATION:
        state = TREZOR_CREATE_WALLET_SELECT_BACKUP_LOCATION;
        break;
      case TREZOR_CREATE_WALLET_SELECT_BACKUP_LOCATION:
        state = TREZOR_CREATE_WALLET_ENTER_DETAILS;
        break;
      case TREZOR_CREATE_WALLET_ENTER_DETAILS:
        state = TREZOR_CREATE_WALLET_REQUEST_CREATE_WALLET;
        break;
      case TREZOR_CREATE_WALLET_REQUEST_CREATE_WALLET:
        state = TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET;
        break;
      case TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET:
        state = TREZOR_CREATE_WALLET_CONFIRM_ENTROPY;
        break;
      case TREZOR_CREATE_WALLET_CONFIRM_ENTROPY:
        state = TREZOR_CREATE_WALLET_ENTER_NEW_PIN;
        break;
      case TREZOR_CREATE_WALLET_ENTER_NEW_PIN:
        state = TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN;
        break;
      case TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN:
        state = TREZOR_CREATE_WALLET_CONFIRM_WORD;
        break;
      case TREZOR_CREATE_WALLET_CONFIRM_WORD:
        state = TREZOR_CREATE_WALLET_REPORT;
        break;
      case TREZOR_CREATE_WALLET_REPORT:
        break;
      case RESTORE_PASSWORD_SEED_PHRASE:
        state = RESTORE_PASSWORD_REPORT;
        break;
      case RESTORE_PASSWORD_REPORT:
        break;
      case RESTORE_WALLET_SEED_PHRASE:
        if (!isLocalZipBackupPresent()) {
          restoreMethod = RESTORE_WALLET_SELECT_BACKUP_LOCATION;
        } else {
          restoreMethod = RESTORE_WALLET_SELECT_BACKUP;
        }
        state = restoreMethod;
        break;
      case RESTORE_WALLET_SELECT_BACKUP_LOCATION:
        if (isCloudBackupPresent()) {
          restoreMethod = RESTORE_WALLET_SELECT_BACKUP;
        } else {
          restoreMethod = RESTORE_WALLET_TIMESTAMP;
        }
        state = restoreMethod;
        break;
      case RESTORE_WALLET_SELECT_BACKUP:
        state = RESTORE_WALLET_REPORT;
        break;
      case RESTORE_WALLET_TIMESTAMP:
        state = RESTORE_WALLET_REPORT;
        break;
      case RESTORE_WALLET_REPORT:
        throw new IllegalStateException("'Next' is not permitted here");
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }

  }

  @Override
  public void showPrevious() {

    switch (state) {
      case WELCOME_LICENCE:
        state = WELCOME_LICENCE;
        break;
      case WELCOME_SELECT_LANGUAGE:
        state = WELCOME_LICENCE;
        break;
      case WELCOME_SELECT_WALLET:
        state = WELCOME_SELECT_LANGUAGE;
        break;
      case CREATE_WALLET_PREPARATION:
        state = WELCOME_SELECT_WALLET;
        break;
      case CREATE_WALLET_SELECT_BACKUP_LOCATION:
        state = CREATE_WALLET_PREPARATION;
        break;
      case CREATE_WALLET_SEED_PHRASE:
        state = CREATE_WALLET_SELECT_BACKUP_LOCATION;
        break;
      case CREATE_WALLET_CONFIRM_SEED_PHRASE:
        state = CREATE_WALLET_SEED_PHRASE;
        break;
      case CREATE_WALLET_REPORT:
        state = CREATE_WALLET_SEED_PHRASE;
      case TREZOR_CREATE_WALLET_PREPARATION:
        state = WELCOME_SELECT_WALLET;
        break;
      case TREZOR_CREATE_WALLET_SELECT_BACKUP_LOCATION:
        state = TREZOR_CREATE_WALLET_PREPARATION;
        break;
      case TREZOR_CREATE_WALLET_ENTER_DETAILS:
        state = TREZOR_CREATE_WALLET_SELECT_BACKUP_LOCATION;
        break;
      case TREZOR_CREATE_WALLET_REQUEST_CREATE_WALLET:
        throw new IllegalStateException("'Previous' is not permitted here - user is committed to creating wallet");
      case TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET:
        throw new IllegalStateException("'Previous' is not permitted here - user is committed to creating wallet");
      case TREZOR_CREATE_WALLET_CONFIRM_ENTROPY:
        throw new IllegalStateException("'Previous' is not permitted here - user is committed to creating wallet");
      case TREZOR_CREATE_WALLET_ENTER_NEW_PIN:
        throw new IllegalStateException("'Previous' is not permitted here - user is committed to creating wallet");
      case TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN:
        throw new IllegalStateException("'Previous' is not permitted here - user is committed to creating wallet");
      case TREZOR_CREATE_WALLET_CONFIRM_WORD:
        throw new IllegalStateException("'Previous' is not permitted here - user is committed to creating wallet");
      case TREZOR_CREATE_WALLET_REPORT:
        throw new IllegalStateException("'Previous' is not permitted here - user is committed to creating wallet");
      case RESTORE_PASSWORD_SEED_PHRASE:
        state = WELCOME_SELECT_WALLET;
        break;
      case RESTORE_PASSWORD_REPORT:
        state = RESTORE_PASSWORD_SEED_PHRASE;
        break;
      case RESTORE_WALLET_SEED_PHRASE:
        state = WELCOME_SELECT_WALLET;
        break;
      case RESTORE_WALLET_SELECT_BACKUP_LOCATION:
        state = RESTORE_WALLET_SEED_PHRASE;
        break;
      case RESTORE_WALLET_SELECT_BACKUP:
        state = RESTORE_WALLET_SELECT_BACKUP_LOCATION;
        break;
      case RESTORE_WALLET_TIMESTAMP:
        state = RESTORE_WALLET_SELECT_BACKUP_LOCATION;
        break;
      case RESTORE_WALLET_REPORT:
        state = restoreMethod;
        break;
      case SELECT_WALLET_HARDWARE:
        state = WELCOME_SELECT_WALLET;
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }

  /**
   * Request that the Trezor begin the process of creating a wallet
   */
  public void requestCreateWallet() {

    // Reset the Trezor seed phrase flags in case we're coming back through
    trezorWordCount = 0;
    trezorChecking = false;

    // Start the request
    ListenableFuture future = hardwareWalletRequestService.submit(
            new Callable<Boolean>() {

              @Override
              public Boolean call() throws Exception {
                log.debug("Performing a request for secure wallet creation to Trezor");

                // Provide a short delay to allow UI to update
                Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

                Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

                // We deliberately ignore the passphrase option to ensure
                // only the seed phrase needs to be secured to protect the wallet
                hardwareWalletService.get().secureCreateWallet(
                        "english", // For now the Trezor UI is fixed at English
                        getTrezorWalletLabel(),
                        false, // For now we ignore supplied entropy (too confusing for mainstream)
                        true, // A PIN is mandatory for mainstream
                        getSeedPhraseSize().getStrength()
                );

                // Must have successfully sent the message to be here
                return true;

              }
            });
    Futures.addCallback(
            future, new FutureCallback() {
              @Override
              public void onSuccess(@Nullable Object result) {

                // We successfully made the request so wait for the result

              }

              @Override
              public void onFailure(Throwable t) {

                // Have a failure
                switch (state) {

                  case TREZOR_CREATE_WALLET_ENTER_NEW_PIN:
                  case TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN:
                    state = TREZOR_CREATE_WALLET_REPORT;
                    setReportMessageKey(MessageKey.TREZOR_INCORRECT_PIN_FAILURE);
                    setReportMessageStatus(false);
                    break;
                  default:
                    throw new IllegalStateException("Should not reach here from " + state.name());
                }
              }

            });


  }

  /**
   * @param pinPositions The PIN positions providing some obfuscation
   */
  public void providePin(final String pinPositions) {

    // Start the request
    ListenableFuture future = hardwareWalletRequestService.submit(
            new Callable<Boolean>() {

              @Override
              public Boolean call() throws Exception {

                Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

                log.debug("Provide a PIN");
                hardwareWalletService.get().providePIN(pinPositions);

                // Must have successfully sent the message to be here
                return true;

              }

            });
    Futures.addCallback(
            future, new FutureCallback() {
              @Override
              public void onSuccess(@Nullable Object result) {

                // We successfully made the request so wait for the result

              }

              @Override
              public void onFailure(Throwable t) {

                // Have a failure
                switch (state) {
                  case TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET:
                    state = TREZOR_CREATE_WALLET_REPORT;
                    setReportMessageKey(MessageKey.TREZOR_INCORRECT_PIN_FAILURE);
                    setReportMessageStatus(false);
                    break;
                  default:
                    throw new IllegalStateException("Should not reach here from " + state.name());
                }
              }

            });

  }

  @Override
  public void showButtonPress(HardwareWalletEvent event) {

    ButtonRequest buttonRequest = (ButtonRequest) event.getMessage().get();

    switch (state) {
      case TREZOR_CREATE_WALLET_REQUEST_CREATE_WALLET:
        switch (buttonRequest.getButtonRequestType()) {
          case WIPE_DEVICE:
            // Device requires confirmation to wipe device
            state = TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET;
            break;
          default:
            throw new IllegalStateException("Unexpected button: " + buttonRequest.getButtonRequestType().name());
        }
        break;
      case TREZOR_CREATE_WALLET_CONFIRM_WORD:
        switch (buttonRequest.getButtonRequestType()) {
          case CONFIRM_WORD:
            trezorWordCount++;
            if (trezorWordCount > trezorSeedSize.getSize()) {
              log.debug("Reset word count for confirm phase");
              trezorWordCount = 1;
              trezorChecking = true;
            }
            // Device requires confirmation of word in seed phrase
            // See "operation succeeded" for next state transition
            state = TREZOR_CREATE_WALLET_CONFIRM_WORD;
            trezorConfirmWordPanelView.updateDisplay(trezorWordCount, trezorChecking);
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
  public void showOperationFailed(HardwareWalletEvent event) {

    Failure failure = (Failure) event.getMessage().get();

    switch (state) {

      case TREZOR_CREATE_WALLET_REQUEST_CREATE_WALLET:
        switch (failure.getType()) {
          case UNEXPECTED_MESSAGE:
            // Device was in the middle of something and needs to be re-initialised
            state = WELCOME_SELECT_WALLET;
            break;
          default:
            // User does not want to create a new wallet
            state = WELCOME_SELECT_WALLET;
        }
        break;
      case TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET:
        // User does not want to create a new wallet
        state = WELCOME_SELECT_WALLET;
        break;
      default:
    }

  }

  @Override
  public void showOperationSucceeded(HardwareWalletEvent event) {

    switch (state) {

      case TREZOR_CREATE_WALLET_CONFIRM_WORD:
        // User does not want to create a new wallet
        state = TREZOR_CREATE_WALLET_REPORT;
        setReportMessageKey(MessageKey.USE_TREZOR_REPORT_MESSAGE_SUCCESS);
        setReportMessageStatus(true);
        break;
      default:
    }

  }

  @Override
  public void showPINEntry(HardwareWalletEvent event) {

    // Determine if this is the first or second PIN entry
    PinMatrixRequest request = (PinMatrixRequest) event.getMessage().get();
    PinMatrixRequestType requestType = request.getPinMatrixRequestType();

    // The PIN entry could have come about from many possible paths
    switch (state) {
      case TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET:
        // User has confirmed the wipe and should next enter new PIN
        switch (requestType) {
          case NEW_FIRST:
            state = TREZOR_CREATE_WALLET_ENTER_NEW_PIN;
            break;
          default:
            throw new IllegalStateException("Should not reach here from " + requestType.name());
        }
        break;
      case TREZOR_CREATE_WALLET_ENTER_NEW_PIN:
        // User has entered the new PIN and should next confirm it
        switch (requestType) {
          case NEW_SECOND:
            state = TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN;
            break;
          default:
            throw new IllegalStateException("Should not reach here from " + requestType.name());
        }
        break;
      case TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN:
        // User has confirmed the new PIN
        switch (requestType) {
          case NEW_SECOND:
            state = TREZOR_CREATE_WALLET_CONFIRM_WORD;
            break;
          default:
            throw new IllegalStateException("Should not reach here from " + requestType.name());
        }
        break;
      default:
        throw new IllegalStateException("Should not reach here from " + state.name());
    }


  }

  @Override
  public void showProvideEntropy(HardwareWalletEvent event) {

    // Progress the state
    state = TREZOR_CREATE_WALLET_CONFIRM_WORD;

    // Start the request
    ListenableFuture future = hardwareWalletRequestService.submit(
            new Callable<Boolean>() {

              @Override
              public Boolean call() throws Exception {

                Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

                byte[] entropy = hardwareWalletService.get().generateEntropy();

                log.debug("Provide entropy");
                hardwareWalletService.get().provideEntropy(entropy);

                // Must have successfully sent the message to be here
                return true;

              }

            });
    Futures.addCallback(
            future, new FutureCallback() {
              @Override
              public void onSuccess(@Nullable Object result) {

                // We successfully made the request so wait for the result

              }

              @Override
              public void onFailure(Throwable t) {

                // Have a failure
                state = TREZOR_CREATE_WALLET_REPORT;
                setReportMessageKey(MessageKey.TREZOR_FAILURE_OPERATION);
                setReportMessageStatus(false);
              }

            });

  }

  private SeedPhraseSize getSeedPhraseSize() {
    return SeedPhraseSize.TWELVE_WORDS;
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  @Subscribe
  public void onVerificationStatusChangedEvent(VerificationStatusChangedEvent event) {

    ViewEvents.fireWizardButtonEnabledEvent(event.getPanelName(), WizardButton.NEXT, event.isOK());

  }

  /**
   * @return True if backups are present in the local zip backup location
   */
  private boolean isLocalZipBackupPresent() {

    // Ensure we start from a fresh list
    backupSummaries.clear();

    // Get the local backups
    try {
      // If no walletid is set (done with Trezor wallets) then work it out from the entered seed
      if (!walletId.isPresent()) {
        EnterSeedPhraseModel restoreWalletEnterSeedPhraseModel = getRestoreWalletEnterSeedPhraseModel();
        SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
        byte[] seed = seedGenerator.convertToSeed(restoreWalletEnterSeedPhraseModel.getSeedPhrase());
        walletId = Optional.of(new WalletId(seed));
      }
      backupSummaries = BackupManager.INSTANCE.getLocalZipBackups(walletId.get());

      return !backupSummaries.isEmpty();
    } catch (SeedPhraseException spe) {
      log.error("The seed phrase is incorrect.");
      return false;
    }
  }

  /**
   * @return True if backups are present in the cloud backup location given by the user
   */
  private boolean isCloudBackupPresent() {

    // Ensure we start from a fresh list
    backupSummaries.clear();

    // Get the cloud backups matching the entered seed
    try {
      // If no walletid is set (done with Trezor wallets) then work it out from the entered seed
      if (!walletId.isPresent()) {
        EnterSeedPhraseModel restoreWalletEnterSeedPhraseModel = getRestoreWalletEnterSeedPhraseModel();
        SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
        byte[] seed = seedGenerator.convertToSeed(restoreWalletEnterSeedPhraseModel.getSeedPhrase());
        walletId = Optional.of(new WalletId(seed));
      }
      backupSummaries = BackupManager.INSTANCE.getCloudBackups(walletId.get(), new File(getRestoreLocation()));

      return !backupSummaries.isEmpty();
    } catch (SeedPhraseException spe) {
      log.error("The seed phrase is incorrect.");
      return false;
    }
  }

  /**
   * @return The "select wallet" radio button choice
   */
  public WelcomeWizardState getSelectWalletChoice() {
    return selectWalletChoice;
  }

  /**
   * @return The "create wallet" seed phrase (generated)
   */
  public List<String> getCreateWalletSeedPhrase() {
    return createWalletSeedPhrase;
  }

  /**
   * The seed phrase entered on the RestoreWalletSeedPhrasePanelView
   */
  public EnterSeedPhraseModel getRestoreWalletEnterSeedPhraseModel() {
    return restoreWalletEnterSeedPhraseModel;
  }

  public WelcomeWizardState getRestoreMethod() {
    return restoreMethod;
  }

  /**
   * @return The actual generated seed timestamp (e.g. "1850/07")
   */
  public String getActualSeedTimestamp() {
    return actualSeedTimestamp;
  }

  /**
   * @return The user entered credentials for the creation process
   */
  public String getCreateWalletUserPassword() {
    return confirmPasswordModel.getValue();
  }

  /**
   * @return The user entered "cloud backup" location
   */
  public String getCloudBackupLocation() {
    return cloudBackupLocationSelectFileModel.getValue();
  }

  /**
   * @return The user entered restore location
   */
  public String getRestoreLocation() {
    return restoreLocationSelectFileModel.getValue();
  }

  /**
   * @return The seed phrase generator
   */
  public SeedPhraseGenerator getSeedPhraseGenerator() {
    return seedPhraseGenerator;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param confirmPasswordModel The "confirm credentials" model
   */
  public void setConfirmPasswordModel(ConfirmPasswordModel confirmPasswordModel) {
    this.confirmPasswordModel = confirmPasswordModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param cloudBackupLocationSelectFileModel The "cloud backup location" select file model
   */
  public void setCloudBackupLocationSelectFileModel(SelectFileModel cloudBackupLocationSelectFileModel) {
    this.cloudBackupLocationSelectFileModel = cloudBackupLocationSelectFileModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param restoreLocationSelectFileModel The "restore location" select file model
   */
  public void setRestoreLocationSelectFileModel(SelectFileModel restoreLocationSelectFileModel) {
    this.restoreLocationSelectFileModel = restoreLocationSelectFileModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param selectWalletChoice The wallet selection from the radio buttons
   */
  void setSelectWalletChoice(WelcomeWizardState selectWalletChoice) {
    this.selectWalletChoice = selectWalletChoice;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param createWalletEnterSeedPhraseModel The "create wallet enter seed phrase" model
   */
  public void setCreateWalletEnterSeedPhraseModel(EnterSeedPhraseModel createWalletEnterSeedPhraseModel) {
    this.createWalletEnterSeedPhraseModel = createWalletEnterSeedPhraseModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param restoreWalletEnterSeedPhraseModel The "restore wallet enter seed phrase" model
   */
  public void setRestoreWalletEnterSeedPhraseModel(EnterSeedPhraseModel restoreWalletEnterSeedPhraseModel) {
    this.restoreWalletEnterSeedPhraseModel = restoreWalletEnterSeedPhraseModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param createWalletSeedPhrase The actual seed phrase generated by the panel model
   */
  public void setCreateWalletSeedPhrase(List<String> createWalletSeedPhrase) {
    this.createWalletSeedPhrase = createWalletSeedPhrase;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param actualSeedTimestamp The actual seed timestamp generated by the panel model
   */
  public void setActualSeedTimestamp(String actualSeedTimestamp) {
    this.actualSeedTimestamp = actualSeedTimestamp;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param selectBackupSummaryModel The selected backup summary
   */
  public void setSelectBackupSummaryModel(SelectBackupSummaryModel selectBackupSummaryModel) {
    this.selectBackupSummaryModel = selectBackupSummaryModel;
  }

  public SelectBackupSummaryModel getSelectBackupSummaryModel() {
    return selectBackupSummaryModel;
  }

  /**
   * @return The discovered backup summaries
   */
  public List<BackupSummary> getBackupSummaries() {
    return backupSummaries;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param restoreWalletEnterTimestampModel The "enter seed phrase" model for the restore wallet panel
   */
  public void setRestoreWalletEnterTimestampModel(EnterSeedPhraseModel restoreWalletEnterTimestampModel) {
    this.restoreWalletEnterTimestampModel = restoreWalletEnterTimestampModel;
  }

  public EnterSeedPhraseModel getRestoreWalletEnterTimestampModel() {
    return restoreWalletEnterTimestampModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param restoreWalletConfirmPasswordModel The "enter credentials" model for the restore wallet panel
   */
  public void setRestoreWalletConfirmPasswordModel(ConfirmPasswordModel restoreWalletConfirmPasswordModel) {
    this.restoreWalletConfirmPasswordModel = restoreWalletConfirmPasswordModel;
  }

  public ConfirmPasswordModel getRestoreWalletConfirmPasswordModel() {
    return restoreWalletConfirmPasswordModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param enterSeedPhraseModel The "restore credentials" seed phrase mode
   */
  void setRestorePasswordEnterSeedPhraseModel(EnterSeedPhraseModel enterSeedPhraseModel) {
    this.restorePasswordEnterSeedPhraseModel = enterSeedPhraseModel;
  }

  public EnterSeedPhraseModel getRestorePasswordEnterSeedPhraseModel() {
    return restorePasswordEnterSeedPhraseModel;
  }

  /**
   * @return True if this wizard was created as the result of a restore operation
   */
  public boolean isRestoring() {
    return restoring;
  }

  /**
   * @return The welcome wizard mode (e.g. standard, Trezor etc)
   */
  public WelcomeWizardMode getMode() {
    return mode;
  }

  /**
   * @return The Trezor wallet label (appears on device startup)
   */
  public String getTrezorWalletLabel() {
    return trezorWalletLabel;
  }

  public void setTrezorWalletLabel(String trezorWalletLabel) {
    this.trezorWalletLabel = trezorWalletLabel;
  }

  /**
   * @return The Trezor wallet seed size (12, 18, 24 words)
   */
  public SeedPhraseSize getTrezorSeedSize() {
    return trezorSeedSize;
  }

  public void setTrezorSeedSize(SeedPhraseSize trezorSeedSize) {
    this.trezorSeedSize = trezorSeedSize;
  }

  /**
   * @return The most recent PIN
   */
  public String getMostRecentPin() {
    return mostRecentPin;
  }

  public void setMostRecentPin(String mostRecentPin) {
    this.mostRecentPin = mostRecentPin;
  }

  public void setTrezorConfirmWordPanelView(CreateTrezorWalletConfirmWordPanelView trezorConfirmWordPanelView) {
    this.trezorConfirmWordPanelView = trezorConfirmWordPanelView;
  }

}
