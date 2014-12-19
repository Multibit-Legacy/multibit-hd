package org.multibit.hd.ui.views.wizards.welcome.restore_wallet;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.Uninterruptibles;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.crypto.AESUtils;
import org.multibit.hd.core.dto.BitcoinNetworkStatus;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.SSLManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.ViewKey;
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
  private JLabel walletCreatedStatusLabel;
  private JLabel cacertsInstalledStatusLabel;
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
        "[]10[]10[]10[]10[]" // Row constraints
      ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Initialise to failure
    walletCreatedStatusLabel = Labels.newWalletCreatedStatus(false);
    cacertsInstalledStatusLabel = Labels.newCACertsInstalledStatus(false);
    synchronizationStatusLabel = Labels.newSynchronizingStatus(false);

    // Start invisible (activates after CA certs completes)
    blocksLeftLabel = Labels.newValueLabel("0");
    blocksLeftStatusLabel = Labels.newBlocksLeft();

    // Make all labels invisible initially
    walletCreatedStatusLabel.setVisible(false);
    cacertsInstalledStatusLabel.setVisible(false);
    synchronizationStatusLabel.setVisible(false);
    blocksLeftLabel.setVisible(false);
    blocksLeftStatusLabel.setVisible(false);

    contentPanel.add(walletCreatedStatusLabel, "wrap");
    contentPanel.add(cacertsInstalledStatusLabel, "wrap");
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

    Preconditions.checkNotNull(restoreWalletExecutorService,"'restoreWalletExecutorService' must be present");

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

          // If the event is about peer group counts then ignore it - we are only interested in block count related events
          if (!summary.getPeerCount().isPresent()) {
            // Blocks left
            int blocksLeft = event.getSummary().getBlocksLeft();
            if (blocksLeft < 0) {
              blocksLeftLabel.setVisible(false);
              blocksLeftStatusLabel.setVisible(false);
            } else {
              // Synchronizing
              blocksLeftLabel.setVisible(true);
              blocksLeftStatusLabel.setVisible(true);
              AwesomeDecorator.applyIcon(
                AwesomeIcon.EXCHANGE,
                blocksLeftStatusLabel,
                true,
                MultiBitUI.NORMAL_ICON_SIZE
              );
              blocksLeftLabel.setText(String.valueOf(summary.getBlocksLeft()));
            }

            if (blocksLeft == 0) {

              // Completed

              // Update the status
              AwesomeDecorator.applyIcon(
                AwesomeIcon.CHECK,
                blocksLeftStatusLabel,
                true,
                MultiBitUI.NORMAL_ICON_SIZE
              );

              // Looks ugly but is semantically correct
              blocksLeftLabel.setText("0");

            }

            boolean currentEnabled = getFinishButton().isEnabled();

            final boolean newEnabled;

            // NOTE: Finish is kept disabled until fully synchronized
            switch (event.getSummary().getSeverity()) {
              case RED:
                // Always disabled on RED
                newEnabled = false;
                break;
              case AMBER:
                // Enable on AMBER only if unrestricted
                newEnabled = InstallationManager.unrestricted;
                break;
              case GREEN:
                // Enable on GREEN only if synchronized or unrestricted (to speed up FEST tests)
                newEnabled = BitcoinNetworkStatus.SYNCHRONIZED.equals(event.getSummary().getStatus()) || InstallationManager.unrestricted || blocksLeft == 0;
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

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          // Hide the header view (switching back on is done in MainController#onBitcoinNetworkChangedEvent
          ViewEvents.fireViewChangedEvent(ViewKey.HEADER, false);

        }
      });

    final boolean walletCreatedStatus;

    // Attempt to create the wallet
    switch (model.getRestoreMethod()) {

      case RESTORE_WALLET_SELECT_BACKUP:
        log.debug("Performing a restore from a seed phrase and a wallet backup.");

        EnterSeedPhraseModel restoreWalletEnterSeedPhraseModel = model.getRestoreWalletEnterSeedPhraseModel();

        // Determine if the create wallet status is valid
        // Starts the wallet synchronization if OK
        walletCreatedStatus = createWalletFromSeedPhrase(restoreWalletEnterSeedPhraseModel.getSeedPhrase());
        break;
      case RESTORE_WALLET_TIMESTAMP:
        log.debug("Performing a restore from a seed phrase and a timestamp.");

        EnterSeedPhraseModel restoreEnterSeedPhraseModel = model.getRestoreWalletEnterSeedPhraseModel();
        EnterSeedPhraseModel restoreEnterTimestampModel = model.getRestoreWalletEnterTimestampModel();
        ConfirmPasswordModel confirmPasswordModel = model.getRestoreWalletConfirmPasswordModel();
        log.debug("Timestamp: {}", restoreEnterTimestampModel.getSeedTimestamp());

        // Start the wallet replay if successful
        walletCreatedStatus = createWalletFromSeedPhraseAndTimestamp(
          restoreEnterSeedPhraseModel.getSeedPhrase(),
          restoreEnterSeedPhraseModel.isRestoreAsTrezor(),
          restoreEnterTimestampModel.getSeedTimestamp(),
          confirmPasswordModel.getValue()
        );
        break;
      default:
        log.error("Unknown welcome wizard state: {}", model.getRestoreMethod());
        // Create wallet from seed phrase should always be OK
        SwingUtilities.invokeLater(
          new Runnable() {
            @Override
            public void run() {
              Labels.decorateStatusLabel(walletCreatedStatusLabel, Optional.of(false));
              walletCreatedStatusLabel.setVisible(true);
            }
          });
        return;
    }

    log.debug("Wallet created...");

    ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.CREATE_WALLET_REPORT.name(), WizardButton.FINISH, true);

    // Create wallet from seed phrase should always be OK
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          Labels.decorateStatusLabel(walletCreatedStatusLabel, Optional.of(walletCreatedStatus));
          walletCreatedStatusLabel.setVisible(true);
        }
      });

    // Give the user the impression of work being done
    Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

    log.debug("Installing SSL certificates...");

    // Attempt to install the CA certifications for the exchanges and MultiBit.org
    // Configure SSL certificates without forcing
    SSLManager.INSTANCE.installCACertificates(
      InstallationManager.getOrCreateApplicationDataDirectory(),
      InstallationManager.CA_CERTS_NAME,
      false);

    // Update the UI after the BRIT exchange completes
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          // No errors so assume they are OK
          AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, cacertsInstalledStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
          cacertsInstalledStatusLabel.setVisible(true);

        }
      });

    // Give the user the impression of work being done
    Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

    log.debug("Synchronizing...");

    final boolean synchronizationStatus = CoreServices.getOrCreateBitcoinNetworkService().isStartedOk();

    // Let the user know that they're waiting for synchronization to complete
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          Labels.decorateStatusLabel(synchronizationStatusLabel, Optional.of(synchronizationStatus));
          synchronizationStatusLabel.setVisible(true);

        }
      });

  }

  /**
   * Create a wallet from a seed phrase, timestamp and credentials
   */
  private boolean createWalletFromSeedPhraseAndTimestamp(List<String> seedPhrase, boolean isRestoreTrezor, String timestamp, String password) {

    if (!verifySeedPhrase(seedPhrase)) {
      return false;
    }

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(seedPhrase);

    try {
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

      if (isRestoreTrezor) {
        // Create Trezor soft wallet
        WalletManager.INSTANCE.getOrCreateTrezorSoftWalletSummaryFromSeedPhrase(
          applicationDataDirectory,
          Joiner.on(" ").join(seedPhrase),
          Dates.thenInSeconds(replayDate),
          password,
          name,
          notes
        );
      } else {
        // Create MBHD soft wallet
        WalletManager.INSTANCE.getOrCreateMBHDSoftWalletSummaryFromSeed(applicationDataDirectory, seed, Dates.thenInSeconds(replayDate), password, name, notes);
      }

      // Must have succeeded to get here
      return true;

    } catch (Exception e) {
      e.printStackTrace();
      log.error("Failed to restore wallet. Error was '" + e.getMessage() + "'.");
    }

    // Must have failed to be here
    return false;

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

      KeyParameter backupAESKey = AESUtils.createAESKey(seed, WalletManager.SCRYPT_SALT);
      byte[] decryptedPaddedWalletPasswordBytes = org.multibit.hd.brit.crypto.AESUtils.decrypt(
        walletSummary.getEncryptedPassword(),
        backupAESKey,
        WalletManager.AES_INITIALISATION_VECTOR);
      byte[] decryptedWalletPasswordBytes = WalletManager.unpadPasswordBytes(decryptedPaddedWalletPasswordBytes);
      String decryptedWalletPassword = new String(decryptedWalletPasswordBytes, "UTF8");

      // Start the Bitcoin network and synchronize the wallet
      BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();

      // Open the wallet and synchronize the wallet
      WalletManager.INSTANCE.openWalletFromWalletId(
        InstallationManager.getOrCreateApplicationDataDirectory(),
        loadedWalletId,
        decryptedWalletPassword);

      // Start the Bitcoin network and synchronize the wallet
      return bitcoinNetworkService.isStartedOk();
    } catch (Exception e) {
      log.error("Failed to restore wallet. Error was '" + e.getMessage() + "'.");
      return false;
    }
  }

  private boolean verifySeedPhrase(List<String> seedPhrase) {

    if (seedPhrase == null || seedPhrase.size() == 0) {
      log.error("No seed phrase specified. Cannot restore wallet.");
      return false;
    }

    return true;
  }


}
