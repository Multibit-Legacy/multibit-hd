package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardModelChangedEvent;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;
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
public class CreateWalletReportView extends AbstractWizardView<WelcomeWizardModel, String> {

  // View
  private JLabel seedPhraseCreatedStatusLabel;
  private JLabel walletPasswordCreatedStatusLabel;
  private JLabel backupLocationStatusLabel;
  private JLabel walletCreatedStatusLabel;

  /**
   * @param wizard The wizard managing the states
   */
  public CreateWalletReportView(AbstractWizard<WelcomeWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.CREATE_WALLET_REPORT_TITLE);

    PanelDecorator.addExitCancelPreviousFinish(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    String model = "TODO replace with a proper model";
    setPanelModel(model);

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constrains
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
  public void fireViewEvents() {
    ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.CREATE_WALLET_REPORT.name(), WizardButton.FINISH, false);
  }

  @Override
  public boolean updatePanelModel() {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
    return true;
  }

  /**
   * @param event The "wizard model changed" event
   */
  @Subscribe
  public void onWizardModelChangedEvent(WizardModelChangedEvent event) {

    // Check if this event applies to this panel
    if (!event.getPanelName().equals(getPanelName())) {
      return;
    }

    WelcomeWizardModel model = getWizardModel();

    // TODO Check all required data is valid
    List<String> seedPhrase = model.getActualSeedPhrase();
    String password = model.getUserPassword();
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
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, seedPhraseCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletPasswordCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);

    // Determine if the backup location is valid
    boolean backupLocationStatus = backupLocationFile.exists()
      && backupLocationFile.isDirectory()
      && backupLocationFile.canRead()
      && backupLocationFile.canWrite();

    if (backupLocationStatus) {
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, backupLocationStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    } else {
      AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, backupLocationStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    }

    // Determine if the create wallet status is valid
    if (walletCreatedStatus) {
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    } else {
      AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, walletCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    }

    ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.CREATE_WALLET_REPORT.name(), WizardButton.FINISH, backupLocationStatus);

  }


}
