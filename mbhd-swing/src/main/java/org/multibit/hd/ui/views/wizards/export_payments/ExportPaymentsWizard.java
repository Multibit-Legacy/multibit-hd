package org.multibit.hd.ui.views.wizards.export_payments;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

import static org.multibit.hd.ui.views.wizards.export_payments.ExportPaymentsWizardState.EXPORT_PAYMENTS_REPORT;
import static org.multibit.hd.ui.views.wizards.export_payments.ExportPaymentsWizardState.SELECT_EXPORT_LOCATION;

/**
 * <p>Wizard to provide the following to UI for "Welcome":</p>
 * <ol>
 * <li>Welcome and choose language</li>
 * <li>Create or restore a wallet</li>
 * <li>Create a wallet with seed phrase and backup location</li>
 * </ol>
 *
 * @since 0.0.1
 *         
 */
public class ExportPaymentsWizard extends AbstractWizard<ExportPaymentsWizardModel> {

  public ExportPaymentsWizard(ExportPaymentsWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
            SELECT_EXPORT_LOCATION.name(),
      new ExportPaymentsLocationPanelView(this, SELECT_EXPORT_LOCATION.name()));

    wizardViewMap.put(
            EXPORT_PAYMENTS_REPORT.name(),
      new ExportPaymentsReportPanelView(this, EXPORT_PAYMENTS_REPORT.name()));
  }
}
