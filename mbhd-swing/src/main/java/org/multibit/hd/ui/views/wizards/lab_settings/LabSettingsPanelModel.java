package org.multibit.hd.ui.views.wizards.lab_settings;

import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "lab settings" wizard:</p>
 * <ul>
 * <li>Storage of state for the "labs" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class LabSettingsPanelModel extends AbstractWizardPanelModel {

  private final Configuration configuration;

  /**
   * @param panelName     The panel name
   * @param configuration The configuration to use
   */
  public LabSettingsPanelModel(String panelName, Configuration configuration) {
    super(panelName);
    this.configuration = configuration;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

}
