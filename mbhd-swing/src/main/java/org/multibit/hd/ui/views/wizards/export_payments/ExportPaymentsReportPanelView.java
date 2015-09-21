package org.multibit.hd.ui.views.wizards.export_payments;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ExportPerformedEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.export.*;
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

import javax.swing.*;
import java.io.File;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to export payments</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ExportPaymentsReportPanelView extends AbstractWizardPanelView<ExportPaymentsWizardModel, String> {

  // View
  private JLabel exportCompletedLabel;

  private JLabel transactionsExportFileLabel;
  private JLabel transactionsExportFileValue;

  private JLabel mbhdPaymentRequestsExportFileLabel;
  private JLabel mbhdPaymentRequestsExportFileValue;

  private JLabel paymentRequestsExportFileLabel;
  private JLabel paymentRequestsExportFileValue;

  private static final String SEPARATOR = "-";

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public ExportPaymentsReportPanelView(AbstractWizard<ExportPaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.FILE_TEXT, MessageKey.EXPORT_PAYMENTS_REPORT);

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
            "[]10[]", // Column constraints
            "[]10[]0[]10[]0[]10[]0[]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Initialise to failure
    exportCompletedLabel = Labels.newBlankLabel();
    exportCompletedLabel.setText(Languages.safeText(CoreMessageKey.CHANGE_PASSWORD_WORKING));

    transactionsExportFileLabel = Labels.newBlankLabel();
    transactionsExportFileValue = Labels.newBlankLabel();
    mbhdPaymentRequestsExportFileLabel = Labels.newBlankLabel();
    mbhdPaymentRequestsExportFileValue = Labels.newBlankLabel();
    paymentRequestsExportFileLabel = Labels.newBlankLabel();
    paymentRequestsExportFileValue = Labels.newBlankLabel();

    contentPanel.add(exportCompletedLabel, "wrap, span 2");

    contentPanel.add(Labels.newBlankLabel());
    contentPanel.add(transactionsExportFileLabel, "wrap");

    contentPanel.add(Labels.newBlankLabel());
    contentPanel.add(transactionsExportFileValue, "wrap");

    contentPanel.add(Labels.newBlankLabel());
    contentPanel.add(mbhdPaymentRequestsExportFileLabel, "wrap");

    contentPanel.add(Labels.newBlankLabel());
    contentPanel.add(mbhdPaymentRequestsExportFileValue, "wrap");

    contentPanel.add(Labels.newBlankLabel());
     contentPanel.add(paymentRequestsExportFileLabel, "wrap");

     contentPanel.add(Labels.newBlankLabel());
     contentPanel.add(paymentRequestsExportFileValue, "wrap");
   }

  @Override
  protected void initialiseButtons(AbstractWizard<ExportPaymentsWizardModel> wizard) {
    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {
    // Disable the finish button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);
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
      // Create the stems for the output files. These are localised and (in English) : exportedTransactions-2014-03-14
      String[] stems = createStems();

      // Perform the export
      TransactionHeaderConverter transactionHeaderConverter = new TransactionHeaderConverter();
      TransactionConverter transactionConverter = new TransactionConverter();
      MBHDPaymentRequestHeaderConverter mbhdPaymentRequestHeaderConverter = new MBHDPaymentRequestHeaderConverter();
      MBHDPaymentRequestConverter mbhdPaymentRequestConverter = new MBHDPaymentRequestConverter();
      PaymentRequestHeaderConverter paymentRequestHeaderConverter = new PaymentRequestHeaderConverter();
      PaymentRequestConverter paymentRequestConverter = new PaymentRequestConverter();

      CoreServices.getCurrentWalletService().get().exportPayments(
              exportPaymentsLocationFile,
              stems[0],
              stems[1],
              stems[2],
              transactionHeaderConverter,
              transactionConverter,
              mbhdPaymentRequestHeaderConverter,
              mbhdPaymentRequestConverter,
              paymentRequestHeaderConverter,
              paymentRequestConverter
      );
      // Results of export are sent by an event
    } else {
      CoreEvents.fireExportPerformedEvent(new ExportPerformedEvent(null, null, null, false, CoreMessageKey.THE_ERROR_WAS,
              new String[]{Languages.safeText(MessageKey.COULD_NOT_WRITE_TO_THE_DIRECTORY, exportPaymentsLocation)}));
    }

    // Enable the finish button on the report page
    ViewEvents.fireWizardButtonEnabledEvent(ExportPaymentsWizardState.EXPORT_PAYMENTS_REPORT.name(), WizardButton.FINISH, true);

    return true;
  }

  /**
   * Create localised filename stems to be used by the Wallet Service export.
   * A stem is something like 'payment-request-2014-04-13', localised.
   *
   * @return String[1] String[0] is the transactionExport stem, String[1] is the paymentRequestExport stem
   */
  private String[] createStems() {
    DateTime now = Dates.nowUtc();

    // Use the system timezone
    String nowAsString = Dates.formatCompactDateWithHyphensLocal(now);

    String stem0 = Languages.safeText(MessageKey.EXPORT_TRANSACTIONS_STEM) + SEPARATOR + nowAsString;
    String stem1 = Languages.safeText(MessageKey.EXPORT_PAYMENT_REQUESTS_STEM) + "1" + SEPARATOR + nowAsString;
    String stem2 = Languages.safeText(MessageKey.EXPORT_PAYMENT_REQUESTS_STEM) + "2" + SEPARATOR + nowAsString;

    return new String[]{stem0, stem1, stem2};
  }

  /**
   * Call back after export
   */
  @Subscribe
  public void onExportPerformedEvent(final ExportPerformedEvent exportPerformedEvent) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (exportPerformedEvent.isExportWasSuccessful()) {
          AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, exportCompletedLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
          exportCompletedLabel.setText(Languages.safeText(MessageKey.EXPORT_WAS_SUCCESSFUL));
          if (!Strings.isNullOrEmpty(exportPerformedEvent.getTransactionsExportFilename())) {
            transactionsExportFileLabel.setText(Languages.safeText(MessageKey.TRANSACTIONS_WERE_EXPORTED_TO_THE_FILE));
            transactionsExportFileValue.setText(exportPerformedEvent.getTransactionsExportFilename());
          }
          if (!Strings.isNullOrEmpty(exportPerformedEvent.getMBHDPaymentRequestsExportFilename())) {
            mbhdPaymentRequestsExportFileLabel.setText(Languages.safeText(MessageKey.YOUR_PAYMENT_REQUESTS_WERE_EXPORTED_TO_THE_FILE));
            mbhdPaymentRequestsExportFileValue.setText(exportPerformedEvent.getMBHDPaymentRequestsExportFilename());
          }
          if (!Strings.isNullOrEmpty(exportPerformedEvent.getPaymentRequestsExportFilename())) {
                     paymentRequestsExportFileLabel.setText(Languages.safeText(MessageKey.THEIR_PAYMENT_REQUESTS_WERE_EXPORTED_TO_THE_FILE));
                     paymentRequestsExportFileValue.setText(exportPerformedEvent.getPaymentRequestsExportFilename());
                   }
        } else {
          AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, exportCompletedLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
          // TODO (JB) Verify that the wrapping of Object[] for the failure reason data is valid
          // Original was giving compiler warning for varargs
          exportCompletedLabel.setText(
                  Languages.safeText(
                          exportPerformedEvent.getExportFailureReasonKey(),
                          new Object[]{exportPerformedEvent.getExportFailureReasonData()}
                  )
          );
        }
      }
    });
  }
}
