package org.multibit.hd.ui.views.wizards.payment_settings;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "appearance" wizard:</p>
 * <ol>
 * <li>Enter details</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class PaymentSettingsWizard extends AbstractWizard<PaymentSettingsWizardModel> {

  public PaymentSettingsWizard(PaymentSettingsWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    // Use the wizard parameter to retrieve the appropriate mode
    wizardViewMap.put(
      PaymentSettingsState.PAYMENT_SETTINGS.name(),
      new PaymentSettingsPanelView(this, PaymentSettingsState.PAYMENT_SETTINGS.name())
    );

  }

}
