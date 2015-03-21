package org.multibit.hd.ui.views.wizards.welcome.restore_wallet;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.Uninterruptibles;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.crypto.MnemonicCode;
import org.joda.time.DateTime;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.crypto.AESUtils;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.HttpsManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.ViewKey;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.select_backup_summary.SelectBackupSummaryModel;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Hex;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to restore a wallet</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class RestoreWalletReportPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> {

  private static final Logger log = LoggerFactory.getLogger(RestoreWalletReportPanelView.class);

  // View
  private JLabel backupLocationStatusLabel;
  private JLabel walletCreatedStatusLabel;
  private JLabel caCertificateStatusLabel;
  private JLabel synchronizationStatusLabel;

  private JLabel blocksLeftLabel;
  private JLabel blocksLeftStatusLabel;

  private ListeningExecutorService restoreWalletExecutorService;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public RestoreWalletReportPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.RESTORE_WALLET_REPORT_TITLE, AwesomeIcon.FILE_TEXT);

  }

  @Override
  public void newPanelModel() {

    // No need to bind this to the wizard model

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    // Postpone the creation of the executor service to the last moment
    restoreWalletExecutorService = SafeExecutors.newSingleThreadExecutor("restore-wallet");

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[][][]", // Column constraints
        "10[24]10[24]10[24]10[24]10[24]10" // Row constraints
      ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Initialise to failure
    backupLocationStatusLabel = Labels.newBackupLocationStatus(false);
    walletCreatedStatusLabel = Labels.newWalletCreatedStatus(false);
    caCertificateStatusLabel = Labels.newCACertsInstalledStatus(false);
    synchronizationStatusLabel = Labels.newSynchronizingStatus(false);

    // Start invisible (activates after CA certs completes)
    blocksLeftLabel = Labels.newValueLabel("0");
    blocksLeftStatusLabel = Labels.newBlocksLeft();

    // Make all labels invisible initially
    backupLocationStatusLabel.setVisible(false);
    walletCreatedStatusLabel.setVisible(false);
    caCertificateStatusLabel.setVisible(false);
    synchronizationStatusLabel.setVisible(false);
    blocksLeftLabel.setVisible(false);
    blocksLeftStatusLabel.setVisible(false);

    contentPanel.add(backupLocationStatusLabel, "wrap");
    contentPanel.add(walletCreatedStatusLabel, "wrap");
    contentPanel.add(caCertificateStatusLabel, "wrap");
    contentPanel.add(synchronizationStatusLabel, "wrap");

    contentPanel.add(blocksLeftStatusLabel, "");
    contentPanel.add(blocksLeftLabel, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the finish button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, false);

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Override
  public void afterShow() {

    Preconditions.checkNotNull(restoreWalletExecutorService, "'restoreWalletExecutorService' must be present");

    restoreWalletExecutorService.submit(
      new Runnable() {
        @Override
        public void run() {

          handleRestoreWallet();

        }
      });
  }

  /**
   * @param event The "Bitcoin network changed" event - one per block downloaded during synchronization
   */
  @Subscribe
  public void onBitcoinNetworkChangeEvent(final BitcoinNetworkChangedEvent event) {

    if (!isInitialised()) {
      return;
    }

    final BitcoinNetworkSummary summary = event.getSummary();

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          switch (summary.getStatus()) {
            case NOT_CONNECTED:
              blocksLeftLabel.setVisible(false);
              blocksLeftStatusLabel.setVisible(false);
              break;

            case CONNECTING:
              blocksLeftLabel.setVisible(false);
              blocksLeftStatusLabel.setVisible(false);
              break;

            case CONNECTED:
              break;

            case DOWNLOADING_BLOCKCHAIN:
              blocksLeftLabel.setVisible(true);
              blocksLeftStatusLabel.setVisible(true);
              blocksLeftLabel.setText(String.valueOf(summary.getBlocksLeft()));
              AwesomeDecorator.applyIcon(
                      AwesomeIcon.EXCHANGE,
                      blocksLeftStatusLabel,
                      true,
                      MultiBitUI.NORMAL_ICON_SIZE
              );
              break;
            case SYNCHRONIZED:
              blocksLeftLabel.setVisible(true);
              blocksLeftStatusLabel.setVisible(true);
              blocksLeftLabel.setText("0");

              AwesomeDecorator.applyIcon(
                      AwesomeIcon.CHECK,
                      blocksLeftStatusLabel,
                      true,
                      MultiBitUI.NORMAL_ICON_SIZE
              );
              break;
            default:
          }

          boolean currentEnabled = getFinishButton().isEnabled();

          final boolean newEnabled;

          // NOTE: Finish is kept disabled until fully synchronized
          switch (event.getSummary().getSeverity()) {
            case RED:
              // Enable on RED only if unrestricted (allows FEST tests without a network)
              newEnabled = InstallationManager.unrestricted;
              break;
            case AMBER:
              // Enable on AMBER only if unrestricted
              newEnabled = InstallationManager.unrestricted;
              break;
            case GREEN:
              // Enable on GREEN only if synchronized or unrestricted (to speed up FEST tests)
              newEnabled = BitcoinNetworkStatus.SYNCHRONIZED.equals(event.getSummary().getStatus()) || InstallationManager.unrestricted;
              break;
            case PINK:
            case EMPTY:
              // Maintain the status quo unless unrestricted
              newEnabled = currentEnabled || InstallationManager.unrestricted;
              break;
            default:
              // Unknown status
              throw new IllegalStateException("Unknown event severity " + event.getSummary().getStatus());
          }

          // Test for a change in condition
          if (currentEnabled != newEnabled) {

            getFinishButton().setEnabled(newEnabled);

            if (newEnabled) {
              // Stop the Bitcoin network to release resources
              CoreServices.stopBitcoinNetworkService();
            }
          }
        }
      });
  }

  /**
   * Handle the process of restoring a wallet
   */
  private void handleRestoreWallet() {

    WelcomeWizardModel model = getWizardModel();

    log.debug("The select wallet choice is {}", model.getSelectWalletChoice());
    log.debug("The restore method is {}", model.getRestoreMethod());

    // There are two sorts of restore wallet method:
    // RESTORE_WALLET_SEED_PHRASE = restore from a seed phrase and timestamp (MBHD soft wallet or Trezor soft wallet)
    // RESTORE_WALLET_BACKUP = restore from a seed phrase and wallet backup

    final boolean backupLocationStatus = handleBackupLocation();

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          LabelDecorator.applyStatusLabel(backupLocationStatusLabel, Optional.of(backupLocationStatus));
          backupLocationStatusLabel.setVisible(true);

          // Hide the header view (switching back on is done in MainController#onBitcoinNetworkChangedEvent
          ViewEvents.fireViewChangedEvent(ViewKey.HEADER, false);

        }
      });

    // Give the user the impression of work being done
    Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

    final boolean walletCreatedStatus = handleCreateWalletStatus(model);

    // Update created wallet status
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          LabelDecorator.applyStatusLabel(walletCreatedStatusLabel, Optional.of(walletCreatedStatus));
          walletCreatedStatusLabel.setVisible(true);
        }
      });

    // Give the user the impression of work being done
    Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

    final boolean caCertificatesStatus = handleCACertificateStatus();

    // Update the UI
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          LabelDecorator.applyStatusLabel(caCertificateStatusLabel, Optional.of(caCertificatesStatus));
          caCertificateStatusLabel.setVisible(true);

        }
      });

    // Give the user the impression of work being done
    Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

    // Allow the Finish button at this point since the Bitcoin network may fail and the user will be trapped
    ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.RESTORE_WALLET_REPORT.name(), WizardButton.FINISH, true);

    final boolean walletSynchronizationStatus = handleSynchronizationStatus();

    // Update the UI
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          LabelDecorator.applyStatusLabel(synchronizationStatusLabel, Optional.of(walletSynchronizationStatus));
          synchronizationStatusLabel.setVisible(true);

        }
      });

  }

  /**
   * @return True if the backup location was created successfully
   */
  private boolean handleBackupLocation() {

    try {
      // Locate the installation directory
      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

      log.debug("Cloud backup...");
      File cloudBackupLocation = null;
      if (Configurations.currentConfiguration != null) {
        String cloudBackupLocationString = Configurations.currentConfiguration.getAppearance().getCloudBackupLocation();
        if (cloudBackupLocationString != null && !"".equals(cloudBackupLocationString)) {
          cloudBackupLocation = new File(cloudBackupLocationString);
        }
      }

      log.debug("Backup manager...");
      // Initialise backup (must be before Bitcoin network starts and on the main thread)
      BackupManager.INSTANCE.initialise(applicationDataDirectory, cloudBackupLocation == null ? Optional.<File>absent() : Optional.of(cloudBackupLocation));

      return true;

    } catch (Exception e) {
      log.error("Failed to create backup location.", e);
    }

    // Must have failed to be here
    return false;

  }

  /**
   * @param model The wizard model
   *
   * @return True if the wallet creation was successful
   */
  private boolean handleCreateWalletStatus(WelcomeWizardModel model) {

    // Attempt to create the wallet
    switch (model.getRestoreMethod()) {

      case RESTORE_WALLET_SELECT_BACKUP:
        log.debug("Performing a restore from a seed phrase and a wallet backup.");

        EnterSeedPhraseModel restoreWalletEnterSeedPhraseModel = model.getRestoreWalletEnterSeedPhraseModel();

        // Create the wallet
        return createWalletFromSeedPhrase(restoreWalletEnterSeedPhraseModel.getSeedPhrase());
      case RESTORE_WALLET_TIMESTAMP:
        log.debug("Performing a restore from a seed phrase and a timestamp.");

        EnterSeedPhraseModel restoreEnterSeedPhraseModel = model.getRestoreWalletEnterSeedPhraseModel();
        EnterSeedPhraseModel restoreEnterTimestampModel = model.getRestoreWalletEnterTimestampModel();
        ConfirmPasswordModel confirmPasswordModel = model.getRestoreWalletConfirmPasswordModel();
        log.debug("Timestamp: {}", restoreEnterTimestampModel.getSeedTimestamp());
        log.debug("Wallet type: {}", restoreEnterSeedPhraseModel.getRestoreWalletType());

        // Create the wallet
        return createWalletFromSeedPhraseAndTimestamp(
          restoreEnterSeedPhraseModel.getSeedPhrase(),
          restoreEnterSeedPhraseModel.getRestoreWalletType(),
          restoreEnterTimestampModel.getSeedTimestamp(),
          confirmPasswordModel.getValue()
        );
      case RESTORE_WALLET_HARD_TREZOR:
        log.debug("Performing a restore of a hard trezor.");
        return createTrezorHardWallet();
      default:
        log.error("Unknown welcome wizard state: {}", model.getRestoreMethod());
        SwingUtilities.invokeLater(
          new Runnable() {
            @Override
            public void run() {
              LabelDecorator.applyStatusLabel(walletCreatedStatusLabel, Optional.of(false));
              walletCreatedStatusLabel.setVisible(true);
            }
          });
        return true;
    }

  }

  /**
   * @return True if the CA certificates were installed correctly
   */
  private boolean handleCACertificateStatus() {

    log.debug("Installing SSL certificates...");

    try {
      // Attempt to install the CA certifications for the exchanges and MultiBit.org
      // Configure SSL certificates without forcing
      HttpsManager.INSTANCE.installCACertificates(
        InstallationManager.getOrCreateApplicationDataDirectory(),
        InstallationManager.CA_CERTS_NAME,
        null, false);

      return true;

    } catch (Exception e) {
      log.error("Failed to create CA certificates.", e);
    }

    // Must have failed to be here
    return false;
  }

  /**
   * @return True if synchronization is occurring correctly
   */
  private boolean handleSynchronizationStatus() {

    log.debug("Synchronizing...");

    try {

      return CoreServices.getOrCreateBitcoinNetworkService().isStartedOk();

    } catch (Exception e) {
      log.error("Failed to start Bitcoin network.", e);
    }

    // Must have failed to be here
    return false;

  }

  /**
   * Create a wallet from a seed phrase, timestamp and credentials
   * @param seedPhrase the seed phrase to use in the restore
   * @param walletTypeToRestore the type of wallet to restore (one of the WalletType enum values)
   * @param timestamp the string format of the timestamp to use in the restore. May be blank.
   * @param password the password to use to secure the newly created wallet
   */
  private boolean createWalletFromSeedPhraseAndTimestamp(List<String> seedPhrase, WalletType walletTypeToRestore, String timestamp, String password) {

    if (!verifySeedPhrase(seedPhrase)) {
      return false;
    }

    try {
      byte[] entropy = MnemonicCode.INSTANCE.toEntropy(seedPhrase);

      SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
      byte[] seed = seedGenerator.convertToSeed(seedPhrase);

      // Locate the user data directory
      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

      DateTime replayDate = Dates.parseSeedTimestamp(timestamp);

      // Provide some default text
      String name = Languages.safeText(MessageKey.WALLET);

      // Display in the system timezone
      String notes = Languages.safeText(
        MessageKey.WALLET_DEFAULT_NOTES,
        Dates.formatDeliveryDateLocal(Dates.nowUtc(), Configurations.currentConfiguration.getLocale())
      );

      switch (walletTypeToRestore) {
        case TREZOR_SOFT_WALLET: {
        // Create Trezor soft wallet
        WalletManager.INSTANCE.getOrCreateTrezorSoftWalletSummaryFromSeedPhrase(
          applicationDataDirectory,
          Joiner.on(" ").join(seedPhrase),
          Dates.thenInSeconds(replayDate),
          password,
          name,
          notes,
          true);
          return true;
        }
        case MBHD_SOFT_WALLET_BIP32: {
          // BIP32 compliant soft wallet
          WalletManager.INSTANCE.getOrCreateMBHDSoftWalletSummaryFromEntropy(applicationDataDirectory, entropy, seed, Dates.thenInSeconds(replayDate), password, name, notes, true);

          return true;
        }
        case MBHD_SOFT_WALLET: {
          // Beta 7 MBHD wallet - not BIP32 compliant
          WalletManager.INSTANCE.badlyGetOrCreateMBHDSoftWalletSummaryFromSeed(applicationDataDirectory, seed, Dates.thenInSeconds(replayDate), password, name, notes, true);
          return true;
        }
        default: {
          throw new IllegalArgumentException("Cannot restore the wallet with unknown type " + walletTypeToRestore);
        }
      }
    } catch (Exception e) {
      log.error("Failed to restore wallet.", e);
      return false;
    }
  }

  /**
   * Create a wallet from a seed phrase and a backup summary (chosen by the user)
   */
  private boolean createWalletFromSeedPhrase(List<String> seedPhrase) {

    if (!verifySeedPhrase(seedPhrase)) {
      return false;
    }

    // Get the model that contains the selected wallet backup to use
    SelectBackupSummaryModel selectedBackupSummaryModel = getWizardModel().getSelectBackupSummaryModel();

    if (selectedBackupSummaryModel == null || selectedBackupSummaryModel.getValue() == null ||
      selectedBackupSummaryModel.getValue().getFile() == null) {
      log.debug("No wallet backup to use from the model");
      return false;
    }

    log.debug("Loading wallet backup '" + selectedBackupSummaryModel.getValue().getFile() + "'");
    try {

      WalletId loadedWalletId = BackupManager.INSTANCE.loadZipBackup(selectedBackupSummaryModel.getValue().getFile(), seedPhrase);

      // Locate the installation directory
      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

      // Work out what the wallet credentials was from the encrypted value stored in the WalletSummary
      SeedPhraseGenerator seedPhraseGenerator = new Bip39SeedPhraseGenerator();
      byte[] seed = seedPhraseGenerator.convertToSeed(seedPhrase);

      String walletRoot = applicationDataDirectory.getAbsolutePath() + File.separator + WalletManager.createWalletRoot(loadedWalletId);
      WalletSummary walletSummary = WalletManager.getOrCreateWalletSummary(new File(walletRoot), loadedWalletId);

      KeyParameter backupAESKey = AESUtils.createAESKey(seed, WalletManager.scryptSalt());
      byte[] decryptedPaddedWalletPasswordBytes = org.multibit.hd.brit.crypto.AESUtils.decrypt(
        walletSummary.getEncryptedPassword(),
        backupAESKey,
        WalletManager.aesInitialisationVector());
      byte[] decryptedWalletPasswordBytes = WalletManager.unpadPasswordBytes(decryptedPaddedWalletPasswordBytes);
      String decryptedWalletPassword = new String(decryptedWalletPasswordBytes, "UTF8");

      // Start the Bitcoin network and synchronize the wallet
      BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();

      // Open the wallet and synchronize the wallet
      WalletManager.INSTANCE.openWalletFromWalletId(
        InstallationManager.getOrCreateApplicationDataDirectory(),
        loadedWalletId,
        decryptedWalletPassword);

      return bitcoinNetworkService.isStartedOk();

    } catch (Exception e) {
      log.error("Failed to restore wallet from seed phrase.", e);
    }

    // Must have failed to be here
    return false;

  }

  /**
   * Create a Trezor hard wallet from a backup summary, decrypting it with a password created from the Trezor supplied entropy
   */
  private boolean createTrezorHardWallet() {

    // Get the model that contains the selected wallet backup to use
    SelectBackupSummaryModel selectedBackupSummaryModel = getWizardModel().getSelectBackupSummaryModel();

    if (selectedBackupSummaryModel == null || selectedBackupSummaryModel.getValue() == null ||
      selectedBackupSummaryModel.getValue().getFile() == null) {
      log.debug("No wallet backup to use from the model");
      return false;
    }

    log.debug("Loading wallet backup '" + selectedBackupSummaryModel.getValue().getFile() + "'");
    try {
      // For Trezor hard wallets the backups are encrypted with the entropy derived password
      String walletPassword = null;
      Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();
      if (hardwareWalletService.isPresent() && hardwareWalletService.get().getContext().getEntropy().isPresent()) {
        walletPassword = Hex.toHexString(hardwareWalletService.get().getContext().getEntropy().get());
      }

      // Check there is a wallet password - if not then cannot decrypt backup
      if (walletPassword == null) {
        log.debug("Cannot work out the password to decrypt the backup - there is no entropy from the Trezor");
        return false;
      }
      KeyParameter backupAESKey = AESUtils.createAESKey(walletPassword.getBytes(Charsets.UTF_8), WalletManager.scryptSalt());

      WalletId loadedWalletId = BackupManager.INSTANCE.loadZipBackup(selectedBackupSummaryModel.getValue().getFile(), backupAESKey);

      // Start the Bitcoin network and synchronize the wallet
      BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();

      // Open the wallet and synchronize the wallet
      WalletManager.INSTANCE.openWalletFromWalletId(
        InstallationManager.getOrCreateApplicationDataDirectory(),
        loadedWalletId,
        walletPassword);

      return bitcoinNetworkService.isStartedOk();

    } catch (Exception e) {
      log.error("Failed to restore Trezor hard wallet.", e);
    }

    // Must have failed to be here
    return false;

  }

  private boolean verifySeedPhrase(List<String> seedPhrase) {

    if (seedPhrase == null || seedPhrase.size() == 0) {
      log.error("No seed phrase specified. Cannot restore wallet.");
      return false;
    }

    return true;
  }
}
