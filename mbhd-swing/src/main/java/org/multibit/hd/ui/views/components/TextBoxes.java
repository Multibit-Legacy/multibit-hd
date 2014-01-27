package org.multibit.hd.ui.views.components;

import org.joda.time.DateTime;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.core.utils.Dates;
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
   * The maximum length of a receive address label
   */
  public static final int RECEIVE_ADDRESS_LABEL_LENGTH = 60;

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
   * @return A new "enter label" text field
   */
  public static JTextField newEnterLabel() {

    JTextField textField = new JTextField(RECEIVE_ADDRESS_LABEL_LENGTH);

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(RECEIVE_ADDRESS_LABEL_LENGTH));
    textField.setDocument(doc);

    textField.setBackground(Themes.currentTheme.dataEntryBackground());

    return textField;
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
   * @param timestamp The timestamp to display in localised form
   *
   * @return A new "display timestamp" text field
   */
  public static JTextField newDisplayTimestamp(DateTime timestamp) {

    // Use SMTP format since it is unambiguous and user readable
    String localisedTimestamp = Dates.formatSmtpDate(timestamp);

    JTextField textField = new JTextField(40);
    textField.setText(localisedTimestamp);

    // Users should not be able to change the timestamp
    textField.setEditable(false);

    return textField;
  }

  /**
   * @param bitcoinAddress The Bitcoin address to display
   *
   * @return A new "display Bitcoin address" text field
   */
  public static JTextField newDisplayBitcoinAddress(String bitcoinAddress) {

    JTextField textField = new JTextField(40);
    textField.setText(bitcoinAddress);

    // Users should not be able to change the address
    textField.setEditable(false);

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

    // The max edit length varies depending on the Bitcoin symbol (e.g. Satoshis have no decimal)
    int maxEditLength = BitcoinSymbol.current().maxRepresentationLength();

    FormattedDecimalField textField = new FormattedDecimalField(0, maximum, decimalPlaces, maxEditLength);

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

    // Allow an extra 6 digits for local currency
    int maxEditLength = BitcoinSymbol.current().maxRepresentationLength() + 6;

    FormattedDecimalField textField = new FormattedDecimalField(0, maximum, decimalPlaces, maxEditLength);

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
