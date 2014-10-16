package org.multibit.hd.ui.views.wizards.export_payments;

import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Model object to provide the following to export payments wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *
 */
public class ExportPaymentsWizardModel extends AbstractWizardModel<ExportPaymentsWizardState> {

  private static final Logger log = LoggerFactory.getLogger(ExportPaymentsWizardModel.class);

  private SelectFileModel exportPaymentsLocationSelectFileModel;


  /**
   * @param state The state object
   */
  public ExportPaymentsWizardModel(ExportPaymentsWizardState state) {
    super(state);
  }

  @Override
  public void showNext() {

    switch (state) {
      case SELECT_EXPORT_LOCATION:
        state = ExportPaymentsWizardState.EXPORT_PAYMENTS_REPORT;
        break;
      case EXPORT_PAYMENTS_REPORT:
        break;

      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }

  }

  @Override
  public void showPrevious() {
    // no previous buttons
  }

  @Override
  public String getPanelName() {
    return state.name();
  }


  /**
   * @return The user entered export payments location
   */
  public String getExportPaymentsLocation() {
    return exportPaymentsLocationSelectFileModel.getValue();
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param exportPaymentsLocationSelectFileModel The "export payments location" select file model
   */
  void setExportPaymentsLocationSelectFileModel(SelectFileModel exportPaymentsLocationSelectFileModel) {
    this.exportPaymentsLocationSelectFileModel = exportPaymentsLocationSelectFileModel;
  }
}
