package org.multibit.hd.ui.views.wizards.application_settings;

import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "edit contact" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ApplicationSettingsWizardModel extends AbstractWizardModel<ApplicationSettingsState> {

  private Configuration configuration;

  /**
   * @param state         The state object
   * @param configuration The configuration to use
   */
  public ApplicationSettingsWizardModel(ApplicationSettingsState state, Configuration configuration) {
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
