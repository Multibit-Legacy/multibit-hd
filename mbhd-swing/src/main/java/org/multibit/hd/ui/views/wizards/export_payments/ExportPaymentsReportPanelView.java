package org.multibit.hd.ui.views.wizards.export_payments;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.io.File;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to export payments</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ExportPaymentsReportPanelView extends AbstractWizardPanelView<ExportPaymentsWizardModel, String> {

  // View
  private JLabel seedPhraseCreatedStatusLabel;
  private JLabel walletPasswordCreatedStatusLabel;
  private JLabel backupLocationStatusLabel;
  private JLabel walletCreatedStatusLabel;

  private static final String SEPARATOR = "-";
  private static final String OPEN_BRACKET = "(";
  private static final String CLOSE_BRACKET = ")";

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public ExportPaymentsReportPanelView(AbstractWizard<ExportPaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.EXPORT_PAYMENTS_REPORT, AwesomeIcon.FILE_TEXT);

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
  protected void initialiseButtons(AbstractWizard<ExportPaymentsWizardModel> wizard) {

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
    ExportPaymentsWizardModel model = getWizardModel();

    String exportPaymentsLocation = model.getExportPaymentsLocation();

    Preconditions.checkNotNull(exportPaymentsLocation, "'exportPaymentsLocation' must be present");

    // Determine if the backup location is valid
    File exportPaymentsLocationFile = new File(exportPaymentsLocation);
    boolean exists = exportPaymentsLocationFile.exists();
    boolean isDirectory = exportPaymentsLocationFile.isDirectory();
    boolean canRead = exportPaymentsLocationFile.canRead();
    boolean canWrite = exportPaymentsLocationFile.canWrite();
    boolean exportPaymentsLocationStatus = exists && isDirectory && canRead && canWrite;

    if (exportPaymentsLocationStatus) {
      // Create the stems for the output files. These are localised and (in English) : exportedTransactions-20140314.csv
      // Where there is a file name collision they are called , say: exportedTransactions-20140312(2).csv
      String[] stems = createStems(exportPaymentsLocationFile);

      // Perform the export
      MultiBitHD.getWalletService().exportPayments(exportPaymentsLocationFile, stems[0], stems[1]);
      // Results of export are sent by an event
    } else {
      // TO DO report that export directory is no good
    }

    // AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, walletCreatedStatusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);

    // Enable the finish button on the report page
    ViewEvents.fireWizardButtonEnabledEvent(ExportPaymentsWizardState.EXPORT_PAYMENTS_REPORT.name(), WizardButton.FINISH, true);

    return true;
  }

  /**
   * Create localised filename stems to be used by the Wallet Service export
   * @return String[1] String[0] is the transactionExport stem, String[1] is the paymentRequestExport stem
   */
  private String[] createStems(File exportPaymentsLocationFile) {
    DateTime now = Dates.nowUtc();
    String nowAsString = Dates.formatBasicDateWithHyphens(now);

    String stem0= Languages.safeText(MessageKey.EXPORT_TRANSACTIONS_STEM);
    String stem1 = Languages.safeText(MessageKey.EXPORT_PAYMENT_REQUESTS_STEM);

    String candidate0 = stem0 + SEPARATOR + nowAsString + WalletService.CSV_SUFFIX;
    String candidate1 = stem1 + SEPARATOR + nowAsString + WalletService.CSV_SUFFIX;

    // If these files don't exist we are done
    if (!((new File(exportPaymentsLocationFile.getAbsolutePath() + File.separator + candidate0)).exists()) &&
            !((new File(exportPaymentsLocationFile.getAbsolutePath() + File.separator + candidate0)).exists())) {
      return new String[]{candidate0, candidate1};
    } else {
      int count = 2;
      // Arbitrary limit just to stop infinite loops
      while(count < 1000) {
        candidate0 = stem0 + SEPARATOR + nowAsString + OPEN_BRACKET + count + CLOSE_BRACKET + WalletService.CSV_SUFFIX;
        candidate1 = stem1 + SEPARATOR + nowAsString + OPEN_BRACKET + count + CLOSE_BRACKET + WalletService.CSV_SUFFIX;
        if (!((new File(exportPaymentsLocationFile.getAbsolutePath() + File.separator + candidate0)).exists()) &&
                !((new File(exportPaymentsLocationFile.getAbsolutePath() + File.separator + candidate0)).exists())) {
          return new String[]{candidate0, candidate1};
        }
        count++;
      }
    }
    // Should not happen except when arbitrary limit hit
    // TODO throw exception
    return null;
  }
}
