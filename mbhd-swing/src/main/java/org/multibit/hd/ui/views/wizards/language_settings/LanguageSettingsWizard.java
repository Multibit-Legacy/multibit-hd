package org.multibit.hd.ui.views.wizards.language_settings;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "edit contact" wizard:</p>
 * <ol>
 * <li>Enter details</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class LanguageSettingsWizard extends AbstractWizard<LanguageSettingsWizardModel> {

  public LanguageSettingsWizard(LanguageSettingsWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    // Use the wizard parameter to retrieve the appropriate mode
    wizardViewMap.put(
      LanguageSettingsState.LANGUAGE_ENTER_DETAILS.name(),
      new LanguageSettingsPanelView(this, LanguageSettingsState.LANGUAGE_ENTER_DETAILS.name())
    );

  }

}
