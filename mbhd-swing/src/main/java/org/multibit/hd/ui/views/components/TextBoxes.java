package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.i18n.BitcoinSymbol;
import org.multibit.hd.ui.views.components.text_fields.FormattedDecimalField;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised text boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class TextBoxes {

  /**
   * The maximum length of the password
   */
  public static final int PASSWORD_LENGTH = 40;

  /**
   * The maximum length of the seed phrase
   */
  public static final int SEED_PHRASE_LENGTH = 240;

  /**
   * Utilities have no public constructor
   */
  private TextBoxes() {
  }

  /**
   * @return A new "Select file" text field
   */
  public static JTextField newSelectFile() {

    JTextField textField = new JTextField(60);
    textField.setBackground(Themes.currentTheme.dataEntryBackground());

    return textField;
  }

  /**
   * @param maximum The largest value than can be accepted (typically the wallet Bitcoin balance) - no financial calculations are performed on this value
   *
   * @return A new text field for Bitcoin amount entry
   */
  public static FormattedDecimalField newBitcoinAmount(double maximum) {

    // Use the Bitcoin symbol multiplier to determine the decimal places
    int decimalPlaces = BitcoinSymbol.current().decimalPlaces();

    FormattedDecimalField textField = new FormattedDecimalField(0, maximum, decimalPlaces);

    Font font = textField.getFont().deriveFont((float) AwesomeDecorator.NORMAL_ICON_SIZE);

    textField.setFont(font);
    textField.setColumns(15);

    return textField;
  }

  /**
   * @param maximum The largest value than can be accepted (typically the wallet local balance) - no financial calculations are performed on this value
   *
   * @return A new text field for currency amount entry
   */
  public static FormattedDecimalField newCurrencyAmount(double maximum) {

    // Use the current configuration to provide the decimal places
    int decimalPlaces = Configurations
      .currentConfiguration
      .getI18NConfiguration()
      .getLocalDecimalPlaces();

    FormattedDecimalField textField = new FormattedDecimalField(0, maximum, decimalPlaces);

    Font font = textField.getFont().deriveFont((float) AwesomeDecorator.NORMAL_ICON_SIZE);

    textField.setFont(font);
    textField.setColumns(15);

    return textField;
  }

  /**
   * @return A new "Password" text field
   */
  public static JPasswordField newPassword() {

    JPasswordField passwordField = new JPasswordField(PASSWORD_LENGTH);

    // Provide a consistent echo character across all components
    passwordField.setEchoChar(getPasswordEchoChar());

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(PASSWORD_LENGTH));
    passwordField.setDocument(doc);

    // Apply the theme
    passwordField.setBackground(Themes.currentTheme.dataEntryBackground());

    return passwordField;
  }

  /**
   * @return A new "Notes" text area
   */
  public static JTextArea newEnterNotes() {

    JTextArea textArea = new JTextArea(6, PASSWORD_LENGTH);

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(SEED_PHRASE_LENGTH));
    textArea.setDocument(doc);

    // Ensure TAB transfers focus
    AbstractAction transferFocus = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        ((Component) e.getSource()).transferFocus();
      }
    };
    textArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "transferFocus");
    textArea.getActionMap().put("transferFocus", transferFocus);

    // Apply the theme
    textArea.setBackground(Themes.currentTheme.dataEntryBackground());

    return textArea;
  }

  /**
   * @return A new "seed phrase" text area for display only (no copy/paste etc)
   */
  public static JTextArea newDisplaySeedPhrase() {

    // Build off the enter seed phrase
    JTextArea textArea = newEnterSeedPhrase();

    // Prevent copy/paste operations
    textArea.setTransferHandler(null);
    textArea.setEditable(false);

    return textArea;

  }

  /**
   * @return A new "seed phrase" text area for entry
   */
  public static JTextArea newEnterSeedPhrase() {

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(SEED_PHRASE_LENGTH));

    // Keep this in line with the PASSWORD_AREA constant
    JTextArea textArea = new JTextArea(doc, "", 6, PASSWORD_LENGTH);

    // Ensure TAB transfers focus
    AbstractAction transferFocus = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        ((Component) e.getSource()).transferFocus();
      }
    };
    textArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "transferFocus");
    textArea.getActionMap().put("transferFocus", transferFocus);

    // Apply the theme
    textArea.setBackground(Themes.currentTheme.readOnlyBackground());
    textArea.setFont(new Font("Courier New", Font.PLAIN, 14));

    // Ensure we provide a suitable inner margin to allow letters to be clear
    textArea.setMargin(new Insets(2, 4, 2, 4));

    // Ensure line and word wrapping occur as required
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    return textArea;

  }

  /**
   * @return The themed echo character for password fields
   */
  public static char getPasswordEchoChar() {
    return '\u2022';
  }
}
