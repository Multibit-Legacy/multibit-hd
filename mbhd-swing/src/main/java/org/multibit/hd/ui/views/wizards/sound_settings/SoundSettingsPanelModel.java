package org.multibit.hd.ui.views.wizards.sound_settings;

import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "sound settings" wizard:</p>
 * <ul>
 * <li>Storage of state for the "formatting" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SoundSettingsPanelModel extends AbstractWizardPanelModel {

  private final Configuration configuration;

  /**
   * @param panelName     The panel name
   * @param configuration The configuration to use
   */
  public SoundSettingsPanelModel(String panelName, Configuration configuration) {
    super(panelName);
    this.configuration = configuration;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

}
