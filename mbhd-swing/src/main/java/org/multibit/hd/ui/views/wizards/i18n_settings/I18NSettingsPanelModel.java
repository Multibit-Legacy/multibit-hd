package org.multibit.hd.ui.views.wizards.i18n_settings;

import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "internationalisation settings" wizard:</p>
 * <ul>
 * <li>Storage of state for the "formatting" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class I18NSettingsPanelModel extends AbstractWizardPanelModel {

  private final I18NConfiguration i18NConfiguration;

  /**
   * @param panelName The panel name
   */
  public I18NSettingsPanelModel(String panelName, I18NConfiguration i18nConfiguration) {
    super(panelName);
    this.i18NConfiguration = i18nConfiguration;
  }

  public I18NConfiguration getI18NConfiguration() {
    return i18NConfiguration;
  }

}
