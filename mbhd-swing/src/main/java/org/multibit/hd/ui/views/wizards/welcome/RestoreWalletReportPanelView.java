package org.multibit.hd.ui.views.wizards.welcome;

import com.google.bitcoin.store.BlockStoreException;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.api.WalletId;
import org.multibit.hd.core.api.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to restore a wallet</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RestoreWalletReportPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> {

  private static final Logger log = LoggerFactory.getLogger(RestoreWalletReportPanelView.class);

  // View
  private JLabel seedPhraseCreatedStatusLabel;
  private JLabel walletPasswordCreatedStatusLabel;
  private JLabel restoreLocationStatusLabel;
  private JLabel walletCreatedStatusLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public RestoreWalletReportPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.RESTORE_WALLET_REPORT_TITLE);

    PanelDecorator.addExitCancelPreviousFinish(this, wizard);

  }

  @Override
  public void newPanelModel() {

    String model = "TODO replace with a proper model";
    setPanelModel(model);

    // No need to bind this to the wizard model

  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
            "fill,insets 0", // Layout constraints
            "[][][]", // Column constraints
            "[]10[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Initialise to failure
    seedPhraseCreatedStatusLabel = Labels.newSeedPhraseCreatedStatus(false);
    walletPasswordCreatedStatusLabel = Labels.newWalletPasswordCreatedStatus(false);
    walletCreatedStatusLabel = Labels.newWalletCreatedStatus(false);
    restoreLocationStatusLabel = Labels.newBackupLocationStatus(false);

    panel.add(restoreLocationStatusLabel, "wrap");
    panel.add(seedPhraseCreatedStatusLabel, "wrap");
    panel.add(walletPasswordCreatedStatusLabel, "wrap");
    panel.add(walletCreatedStatusLabel, "wrap");

    return panel;
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the finish button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Override
  public boolean beforeShow() {

    boolean walletCreatedStatus = false;

    WelcomeWizardModel model = getWizardModel();

    log.debug("The select wallet choice is " + getWizardModel().getSelectWalletChoice());
    log.debug("The restore method is " + getWizardModel().getRestoreMethod());

    // TODO Check all required data is valid
    // There are two sorts of restore wallet method:
    // RESTORE_WALLET_SEED_PHRASE = restore from a seed phrase and timestamp
    // RESTORE_WALLET_BACKUP = restore from a seed phrase and wallet backup

    if (WelcomeWizardState.RESTORE_WALLET_SELECT_BACKUP.equals(getWizardModel().getRestoreMethod())) {
      log.debug("Performing a restore from a seed phrase and a wallet backup.");
      String restoreLocation = model.getRestoreLocation();
      Preconditions.checkNotNull(restoreLocation, "'restoreLocation' must be present");

      File restoreLocationFile = new File(restoreLocation);

      // TODO Implement this
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, seedPhraseCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletPasswordCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);

      // Determine if the restore location is valid
      boolean restoreLocationStatus = restoreLocationFile.exists()
              && restoreLocationFile.isDirectory()
              && restoreLocationFile.canRead();

      if (restoreLocationStatus) {
        AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, restoreLocationStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
      } else {
        AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, restoreLocationStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
      }

      EnterSeedPhraseModel restoreWalletEnterSeedPhraseModel = model.getRestoreWalletEnterSeedPhraseModel();

      // Determine if the create wallet status is valid
      walletCreatedStatus = createWallet(restoreWalletEnterSeedPhraseModel.getSeedPhrase(), restoreLocationFile);

    } else if (WelcomeWizardState.RESTORE_WALLET_TIMESTAMP.equals(getWizardModel().getRestoreMethod())) {
      log.debug("Performing a restore from a seed phrase and a timestamp.");
      EnterSeedPhraseModel restoreEnterSeedPhraseModel = model.getRestoreWalletEnterSeedPhraseModel();
      EnterSeedPhraseModel restoreEnterTimestampModel = model.getRestoreWalletEnterTimestampModel();
      log.debug("Timestamp = " + restoreEnterTimestampModel.getSeedTimestamp());

      // TODO also need a wallet password to encrypt the wallet with - using "password" for now
      walletCreatedStatus = createWallet(restoreEnterSeedPhraseModel.getSeedPhrase(), restoreEnterTimestampModel.getSeedTimestamp(), "password");
    } else {
      log.error("Cannot perform a restore - unknown method of restore = '" + getWizardModel().getRestoreMethod() + "'.");
    }

    if (walletCreatedStatus) {
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    } else {
      AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, walletCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    }

    ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.CREATE_WALLET_REPORT.name(), WizardButton.FINISH, true);

    return true;
  }

  /**
   * Create a wallet from a seed phrase, timestamp and password
   */
  private boolean createWallet(List<String> seedPhrase, String timestamp, CharSequence password) {
    if (!checkSeedPhrase(seedPhrase)) return false;

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(seedPhrase);

    try {
      DateTime replayDate = Dates.parseSeedTimestamp(timestamp);
      // TODO necessary to backup any existing wallet with the same seed before creation/ overwrite ?
      WalletManager.INSTANCE.createWallet(seed, password);

      CoreServices.newBitcoinNetworkService().replayWallet(replayDate);

      return true;
    } catch (IOException | BlockStoreException e) {
      log.error("Failed to restore wallet. Error was '" + e.getMessage() + "'.");
      return false;
    }
  }

  /**
   * Create a wallet from a seed phrase and a backup location
   */
  private boolean createWallet(List<String> seedPhrase, File restoreLocationFile) {
    if (!checkSeedPhrase(seedPhrase)) return false;

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(seedPhrase);

    WalletId walletId = new WalletId(seed);
    List<File>backupfiles = BackupManager.INSTANCE.getWalletBackups(walletId, restoreLocationFile);

    if (backupfiles.size() == 0) {
      // No backups to load
      return false;
    } else {
      // TODO user to specify which of the backups to load
      // TODO for now just choose the first one
      File backupToUse = backupfiles.get(0);
      try {
        BackupManager.INSTANCE.loadBackup(backupToUse);

        // TODO Replay wallet from the data of the last block seen
        return true;
      } catch (IOException ioe) {
        log.error("Failed to restore wallet. Error was '" + ioe.getMessage() + "'.");
        return false;
      }
    }
  }

  private boolean checkSeedPhrase(List<String> seedPhrase) {
    if (seedPhrase == null || seedPhrase.size() == 0) {
      log.error("No seed phrase specified. Cannot restore wallet.");
      return false;
    }
    return true;
  }
}
