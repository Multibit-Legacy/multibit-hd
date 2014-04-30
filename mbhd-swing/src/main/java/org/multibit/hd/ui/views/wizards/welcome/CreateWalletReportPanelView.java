package org.multibit.hd.ui.views.wizards.welcome;

import com.google.bitcoin.core.Wallet;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.core.crypto.AESUtils;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
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
import org.spongycastle.crypto.params.KeyParameter;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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

  // View
  private JLabel seedPhraseCreatedStatusLabel;
  private JLabel walletPasswordCreatedStatusLabel;
  private JLabel backupLocationStatusLabel;
  private JLabel walletCreatedStatusLabel;

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
    walletCreatedStatusLabel = Labels.newWalletCreatedStatus(false);
    backupLocationStatusLabel = Labels.newBackupLocationStatus(false);

    contentPanel.add(backupLocationStatusLabel, "wrap");
    contentPanel.add(seedPhraseCreatedStatusLabel, "wrap");
    contentPanel.add(walletPasswordCreatedStatusLabel, "wrap");
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
  public void afterShow() {

    getFinishButton().requestFocusInWindow();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Override
  public boolean beforeShow() {

    WelcomeWizardModel model = getWizardModel();

    // Locate the installation directory
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Work out the seed
    List<String> seedPhrase = model.getCreateWalletSeedPhrase();
    String password = model.getCreateWalletUserPassword();
    String backupLocation = model.getBackupLocation();
    SeedPhraseGenerator seedPhraseGenerator = getWizardModel().getSeedPhraseGenerator();

    Preconditions.checkNotNull(backupLocation, "'backupLocation' must be present");

    // Initialise backup (must be before Bitcoin network starts and on the main thread)
    BackupManager.INSTANCE.initialise(applicationDataDirectory, new File(backupLocation));

    // Actually create the wallet
    boolean walletCreatedStatus = false;
    byte[] seed = null;
    WalletSummary walletSummary = null;
    File walletDirectory = null;
    try {
      // Attempt to create the wallet (the manager will track the ID etc)
      WalletManager walletManager = WalletManager.INSTANCE;
      seed = seedPhraseGenerator.convertToSeed(seedPhrase);
      long nowInSeconds = (long)(DateTime.now().getMillis() * 0.001);
      walletSummary = walletManager.createWalletSummary(seed, nowInSeconds, password);

      Preconditions.checkNotNull(walletSummary.getWalletId(), "'walletId' must be present");

      String walletRoot = WalletManager.createWalletRoot(walletSummary.getWalletId());
      walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);

      // Save the wallet password, AES encrypted with a key derived from the wallet seed
      KeyParameter backupAESKey = AESUtils.createAESKey(seed, AESUtils.BACKUP_AES_KEY_SALT_USED_IN_SCRYPT);
      byte[] encryptedWalletPassword = org.multibit.hd.brit.crypto.AESUtils.encrypt(password.getBytes(Charsets.UTF_8), backupAESKey, AESUtils.BACKUP_AES_INITIALISATION_VECTOR);
      walletSummary.setEncryptedPassword(encryptedWalletPassword);

      File walletSummaryFile = WalletManager.getOrCreateWalletSummaryFile(walletDirectory);
      WalletManager.updateWalletSummary(walletSummaryFile, walletSummary);

      // Must be OK to be here
      walletCreatedStatus = true;

    } catch (IOException | NoSuchAlgorithmException ioe) {
      ExceptionHandler.handleThrowable(ioe);
    }

    File backupLocationFile = new File(backupLocation);

    // Seed phrase and password are always OK
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, seedPhraseCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletPasswordCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);

    // Determine if the backup location is valid
    boolean exists = backupLocationFile.exists();
    boolean isDirectory = backupLocationFile.isDirectory();
    boolean canRead = backupLocationFile.canRead();
    boolean canWrite = backupLocationFile.canWrite();
    boolean backupLocationStatus = exists && isDirectory && canRead && canWrite;

    if (backupLocationStatus) {
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, backupLocationStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
    } else {
      AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, backupLocationStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
    }

    // Determine if the create wallet status is valid
    if (walletCreatedStatus) {
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
      if (walletDirectory != null) {
        CoreServices.logHistory(Languages.safeText(MessageKey.HISTORY_WALLET_CREATED, walletDirectory.getAbsoluteFile()));
      }
    } else {
      AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, walletCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
    }

    // Once all the initial wallet creation is complete and stored to disk, perform a BRIT wallet exchange.
    // This saves the wallet creation date/ replay date and returns a list of Bitcoin addresses to use for BRIT fee payment
    if (walletCreatedStatus && seed != null && walletSummary.getWallet() != null) {
      performMatcherExchange(seed, walletSummary.getWallet());
    }

    // Enable the finish button on the report page
    ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.CREATE_WALLET_REPORT.name(), WizardButton.FINISH, true);

    return true;
  }

  private void performMatcherExchange(byte[] seed, Wallet wallet) {
    FeeService feeService = CoreServices.createFeeService();

    // Perform a BRIT exchange
    feeService.performExchangeWithMatcher(seed, wallet);
  }
}
