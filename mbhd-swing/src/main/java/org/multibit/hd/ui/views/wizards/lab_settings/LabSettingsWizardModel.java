package org.multibit.hd.ui.views.wizards.lab_settings;

import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "lab settings" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class LabSettingsWizardModel extends AbstractWizardModel<LabSettingsState> {

  private Configuration configuration;

  /**
   * @param state         The state object
   * @param configuration The configuration to use
   */
  public LabSettingsWizardModel(LabSettingsState state, Configuration configuration) {
    super(state);
    this.configuration = configuration;
  }

  /**
   * @return The configuration
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * @param configuration The new Configuration
   */
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }
}
