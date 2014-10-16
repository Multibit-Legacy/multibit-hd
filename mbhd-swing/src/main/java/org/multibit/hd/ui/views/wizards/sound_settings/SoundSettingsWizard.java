package org.multibit.hd.ui.views.wizards.sound_settings;

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
public class SoundSettingsWizard extends AbstractWizard<SoundSettingsWizardModel> {

  public SoundSettingsWizard(SoundSettingsWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    // Use the wizard parameter to retrieve the appropriate mode
    wizardViewMap.put(
      SoundSettingsState.SOUND_ENTER_DETAILS.name(),
      new SoundSettingsPanelView(this, SoundSettingsState.SOUND_ENTER_DETAILS.name())
    );

  }

}
