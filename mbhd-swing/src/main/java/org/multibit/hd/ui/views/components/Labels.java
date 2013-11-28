package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;

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

  // TODO Require keys
  private static final String SIGN_OUT_LABEL = "Sign Out";

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

    JLabel label = AwesomeDecorator.createIconLabel(AwesomeIcon.QUESTION, Languages.safeText(HELP_LABEL));
    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    return label;
  }

  /**
   * @return A new "Settings" label with icon
   */
  public static JLabel newSettingsLabel() {

    JLabel label = AwesomeDecorator.createIconLabel(AwesomeIcon.GEAR, Languages.safeText(SETTINGS_LABEL));
    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    return label;
  }

  /**
   * @return A new "Sign Out" label with icon
   */
  public static JLabel newSignOutLabel() {

    JLabel label = AwesomeDecorator.createIconLabel(AwesomeIcon.SIGN_OUT, Languages.safeText(SIGN_OUT_LABEL));
    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    return label;
  }

  /**
   * @return A new collection of labels that together form a balance display
   */
  public static JLabel[] newBalanceLabels() {

    JLabel balanceLHSLabel = new JLabel();
    JLabel balanceRHSLabel = new JLabel();
    JLabel balanceRHSSymbolLabel = new JLabel();
    JLabel exchangeLabel = new JLabel();

    // Font
    Font balanceFont = balanceLHSLabel.getFont().deriveFont(42.0f);
    Font decimalFont = balanceLHSLabel.getFont().deriveFont(28.0f);

    balanceLHSLabel.setFont(balanceFont);
    balanceRHSLabel.setFont(decimalFont);
    balanceRHSSymbolLabel.setFont(balanceFont);
    exchangeLabel.setFont(decimalFont);

    // Cursor
    balanceLHSLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    balanceRHSLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    balanceRHSSymbolLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    exchangeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Theme
    balanceRHSLabel.setForeground(Themes.H1.foreground);

    return new JLabel[]{

      balanceLHSLabel,
      balanceRHSLabel,
      balanceRHSSymbolLabel,
      exchangeLabel
    };

  }
}
