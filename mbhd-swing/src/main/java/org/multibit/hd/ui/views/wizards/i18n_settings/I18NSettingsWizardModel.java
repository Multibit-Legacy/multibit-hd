package org.multibit.hd.ui.views.wizards.i18n_settings;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

import java.util.Locale;

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

  private Locale locale;

  /**
   * @param state    The state object
   */
  public I18NSettingsWizardModel(I18NSettingsState state) {
    super(state);
  }

  /**
   * @return The locale
   */
  public Locale getLocale() {
    return locale;
  }

}
