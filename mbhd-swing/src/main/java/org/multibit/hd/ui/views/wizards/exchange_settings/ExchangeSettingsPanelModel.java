package org.multibit.hd.ui.views.wizards.exchange_settings;

import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "Exchange settings" wizard:</p>
 * <ul>
 * <li>Storage of state for the "exchange rate provider selection" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ExchangeSettingsPanelModel extends AbstractWizardPanelModel {

  private final Configuration configuration;

  /**
   * @param panelName     The panel name
   * @param configuration The configuration (require both Bitcoin and I18N settings)
   */
  public ExchangeSettingsPanelModel(String panelName, Configuration configuration) {
    super(panelName);
    this.configuration = configuration;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

}
