package org.multibit.hd.ui.views.wizards.i18n_settings;

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
public class I18NSettingsWizard extends AbstractWizard<I18NSettingsWizardModel> {

  public I18NSettingsWizard(I18NSettingsWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    // Use the wizard parameter to retrieve the appropriate mode
    wizardViewMap.put(
      I18NSettingsState.I18N_ENTER_DETAILS.name(),
      new I18NSettingsPanelView(this,I18NSettingsState.I18N_ENTER_DETAILS.name())
    );

  }

}
