package org.multibit.hd.ui.views.wizards.exchange_settings;

import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "Exchange settings" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ExchangeSettingsWizardModel extends AbstractWizardModel<ExchangeSettingsState> {

  private Configuration configuration;

  /**
   * @param state         The state object
   * @param configuration The new configuration (require both Bitcoin and I18N details)
   */
  public ExchangeSettingsWizardModel(ExchangeSettingsState state, Configuration configuration) {
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
   * @param configuration The new configuration
   */
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }
}
