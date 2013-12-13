package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised buttons</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Labels {

  private static final float BALANCE_LARGE_FONT_SIZE = 42.0f;
  private static final float BALANCE_NORMAL_FONT_SIZE = 28.0f;
  private static final float PANEL_CLOSE_FONT_SIZE = 28.0f;

  /**
   * Utilities have no public constructor
   */
  private Labels() {
  }

  /**
   * @param key    The resource key for the i18n message text
   * @param values The data values for token replacement in the message text
   *
   * @return A new label with default styling
   */
  public static JLabel newLabel(MessageKey key, Object... values) {
    return new JLabel(Languages.safeText(key, values));
  }

  /**
   * @return A new "Select language" label
   */
  public static JLabel newSelectLanguageLabel() {

    return new JLabel(Languages.safeText(MessageKey.USE_LANGUAGE_LABEL));
  }

  /**
   * @param mouseAdapter The mouse adapter that provides the event handling
   *
   * @return A new panel close "X" label with icon
   */
  public static JLabel newPanelCloseLabel(MouseAdapter mouseAdapter) {

    JLabel panelCloseLabel = new JLabel();

    // Font
    Font panelCloseFont = panelCloseLabel.getFont().deriveFont(PANEL_CLOSE_FONT_SIZE);
    panelCloseLabel.setFont(panelCloseFont);

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, panelCloseLabel, true);
    panelCloseLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    panelCloseLabel.addMouseListener(mouseAdapter);

    return panelCloseLabel;
  }

  /**
   * <p>The balance labels</p>
   * <ul>
   * <li>[0]: Primary value, possibly decorated with leading symbol/code, to 2dp</li>
   * <li>[1]: Secondary value covering remaining decimal places</li>
   * <li>[2]: Placeholder for trailing symbol/code</li>
   * <li>[3]: Localised exchange rate display</li>
   * </ul>
   *
   * @return A new collection of labels that together form a balance display
   */
  public static JLabel[] newBalanceLabels() {

    JLabel primaryBalanceLabel = new JLabel();
    JLabel secondaryBalanceLabel = new JLabel();
    JLabel trailingSymbolLabel = new JLabel();
    JLabel exchangeLabel = new JLabel();

    // Font
    Font balanceFont = primaryBalanceLabel.getFont().deriveFont(BALANCE_LARGE_FONT_SIZE);
    Font decimalFont = primaryBalanceLabel.getFont().deriveFont(BALANCE_NORMAL_FONT_SIZE);

    primaryBalanceLabel.setFont(balanceFont);
    secondaryBalanceLabel.setFont(decimalFont);
    trailingSymbolLabel.setFont(balanceFont);
    exchangeLabel.setFont(decimalFont);

    // Theme
    primaryBalanceLabel.setForeground(Themes.currentTheme.text());
    secondaryBalanceLabel.setForeground(Themes.currentTheme.fadedText());
    trailingSymbolLabel.setForeground(Themes.currentTheme.text());
    exchangeLabel.setForeground(Themes.currentTheme.text());

    return new JLabel[]{

      primaryBalanceLabel,
      secondaryBalanceLabel,
      trailingSymbolLabel,
      exchangeLabel
    };

  }

  /**
   * @return A new "Send Bitcoin" title
   */
  public static JLabel newSendTitle() {

    return newTitleLabel(MessageKey.SEND_TITLE_LABEL);

  }

  /**
   * @return A new "Confirm Send" title
   */
  public static JLabel newConfirmSendTitle() {
    return newTitleLabel(MessageKey.CONFIRM_SEND_TITLE_LABEL);
  }

  /**
   * @return A new "You are about to send" message
   */
  public static JLabel newConfirmSendAmount() {
    return new JLabel(Languages.safeText(MessageKey.CONFIRM_SEND_MESSAGE_LABEL));
  }

  /**
   * @return A new "Send Progress" title
   */
  public static JLabel newSendProgressTitle() {
    return newTitleLabel(MessageKey.SEND_PROGRESS_TITLE_LABEL);
  }

  /**
   * @param key The message key
   *
   * @return A new label with appropriate font and theme
   */
  private static JLabel newTitleLabel(MessageKey key) {

    JLabel label = new JLabel(Languages.safeText(key));

    // Font
    Font font = label.getFont().deriveFont(BALANCE_LARGE_FONT_SIZE);
    label.setFont(font);

    // Theme
    label.setForeground(Themes.currentTheme.text());

    return label;

  }
}
