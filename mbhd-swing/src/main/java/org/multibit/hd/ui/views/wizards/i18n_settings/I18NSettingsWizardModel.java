package org.multibit.hd.ui.views.wizards.i18n_settings;

import org.multibit.hd.core.config.I18NConfiguration;
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
public class I18NSettingsWizardModel extends AbstractWizardModel<I18NSettingsState> {

  private I18NConfiguration i18nConfiguration;

  /**
   * @param state The state object
   */
  public I18NSettingsWizardModel(I18NSettingsState state, I18NConfiguration i18nConfiguration) {
    super(state);
    this.i18nConfiguration = i18nConfiguration;
  }

  /**
   * @return The I18NConfiguration
   */
  public I18NConfiguration getI18nConfiguration() {
    return i18nConfiguration;
  }

  /**
   * @param i18nConfiguration The new I18NConfiguration
   */
  public void setI18nConfiguration(I18NConfiguration i18nConfiguration) {
    this.i18nConfiguration = i18nConfiguration;
  }
}
