package org.multibit.hd.ui.views.wizards.fee_settings;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.appearance_settings.AppearanceSettingsPanelView;
import org.multibit.hd.ui.views.wizards.appearance_settings.AppearanceSettingsState;
import org.multibit.hd.ui.views.wizards.appearance_settings.AppearanceSettingsWizardModel;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "fee settings" wizard:</p>
 * <ol>
 * <li>Select feePerKB</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class FeeSettingsWizard extends AbstractWizard<FeeSettingsWizardModel> {

  public FeeSettingsWizard(FeeSettingsWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    // Use the wizard parameter to retrieve the appropriate mode
    wizardViewMap.put(
      FeeSettingsState.FEE_ENTER_DETAILS.name(),
      new FeeSettingsPanelView(this, FeeSettingsState.FEE_ENTER_DETAILS.name())
    );
  }
}
