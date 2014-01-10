package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
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

  // Model
  private String model;

  // View
  private JLabel seedPhraseCreatedStatus;
  private JLabel walletPasswordCreatedStatus;
  private JLabel backupLocationStatus;

  /**
   * @param wizard The wizard managing the states
   */
  public CreateWalletReportView(AbstractWizard<WelcomeWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.CREATE_WALLET_REPORT_TITLE);

    PanelDecorator.addExitCancelPreviousFinish(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    model = "TODO replace with a proper model";
    setPanelModel(model);

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constrains
      "[][][]", // Column constraints
      "[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Initialise to failure
    seedPhraseCreatedStatus = Labels.newSeedPhraseCreatedStatus(false);
    walletPasswordCreatedStatus = Labels.newWalletPasswordCreatedStatus(false);
    backupLocationStatus = Labels.newBackupLocationStatus(false);

    panel.add(seedPhraseCreatedStatus, "wrap");
    panel.add(walletPasswordCreatedStatus, "wrap");
    panel.add(backupLocationStatus, "wrap");

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

    String backupLocation = model.getBackupLocation();

    Preconditions.checkNotNull(backupLocation, "'backupLocation' must be present");

    File backupLocationFile = new File(backupLocation);

    // Determine if the backup location is valid
    boolean result = backupLocationFile.exists()
      && backupLocationFile.isDirectory()
      && backupLocationFile.canRead()
      && backupLocationFile.canWrite();

    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, seedPhraseCreatedStatus, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, walletPasswordCreatedStatus, true, AwesomeDecorator.NORMAL_ICON_SIZE);

    if (result) {
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, backupLocationStatus, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    } else {
      AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, backupLocationStatus, true, AwesomeDecorator.NORMAL_ICON_SIZE);
    }

    ViewEvents.fireWizardButtonEnabledEvent(WelcomeWizardState.CREATE_WALLET_REPORT.name(), WizardButton.FINISH, result);

  }


}
