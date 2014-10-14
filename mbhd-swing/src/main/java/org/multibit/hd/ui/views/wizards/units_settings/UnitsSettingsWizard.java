package org.multibit.hd.ui.views.wizards.units_settings;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "units settings" wizard:</p>
 * <ol>
 * <li>Enter settings</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class UnitsSettingsWizard extends AbstractWizard<UnitsWizardModel> {

  public UnitsSettingsWizard(UnitsWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    // Use the wizard parameter to retrieve the appropriate mode
    wizardViewMap.put(
      UnitsSettingsState.UNITS_ENTER_DETAILS.name(),
      new UnitsSettingsPanelView(this, UnitsSettingsState.UNITS_ENTER_DETAILS.name())
    );

  }

}
