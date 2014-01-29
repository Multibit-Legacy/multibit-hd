package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.io.File;
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
    List<String> seedPhrase = model.getRestoreWalletSeedPhrase();
    if (WelcomeWizardState.RESTORE_WALLET_BACKUP.equals(getWizardModel().getSelectWalletChoice())) {
      //
      String restoreLocation = model.getRestoreLocation();
      Preconditions.checkNotNull(restoreLocation, "'restoreLocation' must be present");

      // Actually create the wallet

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

      // Determine if the create wallet status is valid
    }

    // TODO Implement this
    boolean walletCreatedStatus = false;
    if (walletCreatedStatus) {
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    } else {
      AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, walletCreatedStatusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    }

    ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.CREATE_WALLET_REPORT.name(), WizardButton.FINISH, true);

    return true;
  }

}
