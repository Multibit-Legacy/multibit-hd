package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.i18n.Languages;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised buttons</li>
 * </ul>
 * new JComboBox<>(Languages.getLanguageNames(resourceBundle, true))
 *
 * @since 0.0.1
 *         
 */
public class Labels {

  private static final String USE_LANGUAGE_LABEL = "showPreferencesPanel.useSpecific";
  private static final String HELP_LABEL = "multiBitFrame.helpMenuText";
  private static final String SETTINGS_LABEL = "showPreferencesPanel.title";
  private static final String SIGN_OUT_LABEL = "showPreferencesPanel.useSpecific";

  /**
   * Utilities have no public constructor
   */
  private Labels() {
  }

  /**
   * @return A new "Select language" label
   */
  public static JLabel newLanguageLabel() {

    return new JLabel(Languages.safeText(USE_LANGUAGE_LABEL));
  }

  /**
   * @return A new "Help" label with icon
   */
  public static JLabel newHelpLabel() {

    ResourceBundle rb = Languages.currentResourceBundle();
    return AwesomeDecorator.createIconLabel(AwesomeIcon.QUESTION, rb.getString(HELP_LABEL));
  }

  /**
   * @return A new "Settings" label with icon
   */
  public static JLabel newSettingsLabel() {

    ResourceBundle rb = Languages.currentResourceBundle();
    return AwesomeDecorator.createIconLabel(AwesomeIcon.GEAR, rb.getString(SETTINGS_LABEL));
  }

  /**
   * @return A new "Sign Out" label with icon
   */
  public static JLabel newSignOutLabel() {

    ResourceBundle rb = Languages.currentResourceBundle();
    // TODO Require key
    return AwesomeDecorator.createIconLabel(AwesomeIcon.SIGN_OUT, "Sign Out");
  }

}
