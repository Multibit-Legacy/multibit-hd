package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
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
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public CreateWalletReportPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.CREATE_WALLET_REPORT_TITLE);

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void newPanelModel() {

    String model = "TODO replace with a proper model";
    setPanelModel(model);

    // No need to bind this to the wizard model

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.GLOBE);

    panel.setLayout(new MigLayout(
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
    backupLocationStatusLabel = Labels.newBackupLocationStatus(false);

    panel.add(backupLocationStatusLabel, "wrap");
    panel.add(seedPhraseCreatedStatusLabel, "wrap");
    panel.add(walletPasswordCreatedStatusLabel, "wrap");
    panel.add(walletCreatedStatusLabel, "wrap");

    return panel;
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
  public boolean beforeShow() {

    WelcomeWizardModel model = getWizardModel();

    // TODO Check all required data is valid
    List<String> seedPhrase = model.getCreateWalletSeedPhrase();
    String password = model.getCreateWalletUserPassword();
    String backupLocation = model.getBackupLocation();
    SeedPhraseGenerator seedPhraseGenerator = getWizardModel().getSeedPhraseGenerator();

    Preconditions.checkNotNull(backupLocation, "'backupLocation' must be present");

    // Actually create the wallet
    boolean walletCreatedStatus = false;
    try {
      // Attempt to create the wallet (the manager will track the ID etc)
      WalletManager walletManager = WalletManager.INSTANCE;
      byte[] seed = seedPhraseGenerator.convertToSeed(seedPhrase);
      walletManager.createWallet(seed, password);

      // Must be OK to be here
      walletCreatedStatus = true;
    } catch (IOException ioe) {
      ExceptionHandler.handleThrowable(ioe);
    }

    File backupLocationFile = new File(backupLocation);

    // Seed phrase and password are always OK
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, seedPhraseCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletPasswordCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);

    // Determine if the backup location is valid
    boolean exists = backupLocationFile.exists();
    boolean isDirectory =  backupLocationFile.isDirectory();
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
    } else {
      AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, walletCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
    }

    // Enable the finish button on the report page
    ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.CREATE_WALLET_REPORT.name(), WizardButton.FINISH, true);

    return true;
  }

}
