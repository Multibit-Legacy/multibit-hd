package org.multibit.hd.ui.views.wizards.welcome.create_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.Uninterruptibles;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.HttpsManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BackupService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
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

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to create a wallet</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class CreateWalletReportPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> {

  private static final Logger log = LoggerFactory.getLogger(CreateWalletReportPanelView.class);

  // View
  private JLabel seedPhraseCreatedStatusLabel;
  private JLabel walletPasswordCreatedStatusLabel;
  private JLabel backupLocationStatusLabel;
  private JLabel walletCreatedStatusLabel;
  private JLabel cacertsInstalledStatusLabel;

  private ListeningExecutorService createWalletExecutorService;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public CreateWalletReportPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.CREATE_WALLET_REPORT_TITLE, AwesomeIcon.FILE_TEXT);

  }

  @Override
  public void newPanelModel() {

    String model = "TODO replace with a proper model";
    setPanelModel(model);

    // No need to bind this to the wizard model

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    // Postpone the creation of the executor service to the last moment
    createWalletExecutorService = SafeExecutors.newSingleThreadExecutor("create-wallet");

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[]10[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Initialise to failure
    seedPhraseCreatedStatusLabel = Labels.newSeedPhraseCreatedStatus(false);
    walletPasswordCreatedStatusLabel = Labels.newWalletPasswordCreatedStatus(false);
    backupLocationStatusLabel = Labels.newBackupLocationStatus(false);
    cacertsInstalledStatusLabel = Labels.newCACertsInstalledStatus(false);
    walletCreatedStatusLabel = Labels.newWalletCreatedStatus(false);

    // Make all labels invisible initially
    seedPhraseCreatedStatusLabel.setVisible(false);
    walletPasswordCreatedStatusLabel.setVisible(false);
    backupLocationStatusLabel.setVisible(false);
    cacertsInstalledStatusLabel.setVisible(false);
    walletCreatedStatusLabel.setVisible(false);

    contentPanel.add(seedPhraseCreatedStatusLabel, "wrap");
    contentPanel.add(walletPasswordCreatedStatusLabel, "wrap");
    contentPanel.add(backupLocationStatusLabel, "wrap");
    contentPanel.add(cacertsInstalledStatusLabel, "wrap");
    contentPanel.add(walletCreatedStatusLabel, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Disable the finish button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, false);

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Override
  public void afterShow() {

    getFinishButton().requestFocusInWindow();

    createWalletExecutorService.submit(new Runnable() {
      @Override
      public void run() {

        handleCreateWallet();

      }
    });
  }

  /**
   * Handles the process of creating the wallet
   */
  private void handleCreateWallet() {

    WelcomeWizardModel model = getWizardModel();

    // Locate the installation directory
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Work out the seed
    List<String> seedPhrase = model.getCreateWalletSeedPhrase();
    String password = model.getCreateWalletUserPassword();
    String cloudBackupLocation = model.getCloudBackupLocation();
    log.debug("cloudBackupLocation = " + cloudBackupLocation);

    if (Configurations.currentConfiguration != null) {
      Configurations.currentConfiguration.getAppearance().setCloudBackupLocation(cloudBackupLocation);
    }
    SeedPhraseGenerator seedPhraseGenerator = getWizardModel().getSeedPhraseGenerator();

    Preconditions.checkNotNull(cloudBackupLocation, "'backupLocation' must be present");

    // Actually create the wallet
    final byte[] seed;
    final WalletSummary walletSummary;
    final File walletDirectory;

    try {

      // Attempt to create the wallet (the manager will track the ID etc)
      WalletManager walletManager = WalletManager.INSTANCE;
      byte[] entropy = MnemonicCode.INSTANCE.toEntropy(seedPhrase);

      seed = seedPhraseGenerator.convertToSeed(seedPhrase);

      // Seed phrase always OK
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, seedPhraseCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
          seedPhraseCreatedStatusLabel.setVisible(true);
        }
      });

      // Give the user the impression of work being done
      Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

      String name = Languages.safeText(MessageKey.WALLET);

      // Display in the system timezone
      String notes = Languages.safeText(
        MessageKey.WALLET_DEFAULT_NOTES,
        // Provide a precise local creation time
        Dates.formatTransactionDateLocal(Dates.nowUtc(), Configurations.currentConfiguration.getLocale())
      );
      walletSummary = walletManager.getOrCreateMBHDSoftWalletSummaryFromEntropy(
              applicationDataDirectory,
              entropy,
              seed,
              Dates.nowInSeconds(),
              password,
              name,
              notes,
              true);

      Preconditions.checkNotNull(walletSummary.getWalletId(), "'walletId' must be present");

      String walletRoot = WalletManager.createWalletRoot(walletSummary.getWalletId());
      walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);

      WalletManager.writeEncryptedPasswordAndBackupKey(walletSummary, seed, password);

      File walletSummaryFile = WalletManager.getOrCreateWalletSummaryFile(walletDirectory);
      WalletManager.updateWalletSummary(walletSummaryFile, walletSummary);

      File cloudBackupLocationFile = new File(cloudBackupLocation);

      // Password always OK
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletPasswordCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
          walletPasswordCreatedStatusLabel.setVisible(true);

        }
      });

      // Give the user the impression of work being done
      Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

      // Determine if the backup location is valid
      final boolean exists = cloudBackupLocationFile.exists();
      final boolean isDirectory = cloudBackupLocationFile.isDirectory();
      final boolean canRead = cloudBackupLocationFile.canRead();
      final boolean canWrite = cloudBackupLocationFile.canWrite();
      final boolean cloudBackupLocationStatus = exists && isDirectory && canRead && canWrite;

      // Attempt to create a backup
      if (cloudBackupLocationStatus) {
        BackupManager.INSTANCE.initialise(applicationDataDirectory, Optional.of(new File(cloudBackupLocation)));
      } else {
        BackupManager.INSTANCE.initialise(applicationDataDirectory, Optional.<File>absent());
      }
      // Remember the walletSummary and credentials so that it will be used for the next rolling backup
      BackupService backupService = CoreServices.getOrCreateBackupService();
      backupService.rememberWalletSummaryAndPasswordForRollingBackup(walletSummary, password);
      backupService.rememberWalletIdAndPasswordForLocalZipBackup(walletSummary.getWalletId(), password);
      backupService.rememberWalletIdAndPasswordForCloudZipBackup(walletSummary.getWalletId(), password);

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (cloudBackupLocationStatus) {
            AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, backupLocationStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
          } else {
            AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, backupLocationStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
          }
          backupLocationStatusLabel.setVisible(true);

        }
      });

      // Give the user the impression of work being done
      Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

      // Attempt to install the CA certifications for the exchanges and MultiBit.org
      // Configure SSL certificates without forcing
      HttpsManager.INSTANCE.installCACertificates(
        InstallationManager.getOrCreateApplicationDataDirectory(),
        InstallationManager.CA_CERTS_NAME,
        null, // Use default host list
        false // Do not force loading if they are already present
      );

      // Update the UI after the BRIT exchange completes
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          // Determine if the CA certificates are valid
          AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, cacertsInstalledStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
          cacertsInstalledStatusLabel.setVisible(true);

        }
      });

      // Give the user the impression of work being done
      Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

      // Once all the initial wallet creation is complete and stored to disk, perform a BRIT wallet exchange.
      // This saves the wallet creation date/ replay date and returns a list of Bitcoin addresses to use for BRIT fee payment
      if (seed != null && walletSummary.getWallet() != null) {

        // Perform a BRIT exchange
        FeeService feeService = CoreServices.createFeeService();
        feeService.performExchangeWithMatcher(seed, walletSummary.getWallet());

      }

      // Update the UI after the BRIT exchange completes
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          // Determine if the create wallet status is valid
          AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
          walletCreatedStatusLabel.setVisible(true);

        }
      });

      // Give the user the impression of work being done
      Uninterruptibles.sleepUninterruptibly(250, TimeUnit.MILLISECONDS);

      // Write out configuration changes
      Configurations.persistCurrentConfiguration();
      if (Configurations.currentConfiguration != null) {
        log.debug("Persisted configuration with cloudBackup set to " + Configurations.currentConfiguration.getAppearance().getCloudBackupLocation());
      } else {
        log.debug("No current configuration so nothing to write out");
      }

      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            // Enable the finish button on the report page
            ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.CREATE_WALLET_REPORT.name(), WizardButton.FINISH, true);
          }
        });

    } catch (MnemonicException | IOException | NoSuchAlgorithmException e) {
      // Handing over to the exception handler means a hard shutdown
      ExceptionHandler.handleThrowable(e);
    }
  }
}
