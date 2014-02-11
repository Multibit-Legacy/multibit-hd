package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.components.text_fields.FormattedDecimalField;
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
   * Utilities have no public constructor
   */
  private TextBoxes() {
  }

  /**
   * @return A new text field with default theme
   */
  public static JTextField newTextField(int columns) {

    JTextField textField = new JTextField(columns);

    // Set the theme
    textField.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
    textField.setBackground(Themes.currentTheme.dataEntryBackground());

    return textField;
  }

  /**
   * @return A new text field with default theme
   */
  public static JTextField newReadOnlyTextField(int columns) {

    JTextField textField = new JTextField(columns);

    // Users should not be able to change the data
    textField.setEditable(false);

    // Set the theme
    textField.setBorder(new TextBubbleBorder(Themes.currentTheme.readOnlyBorder()));
    textField.setBackground(Themes.currentTheme.readOnlyBackground());

    return textField;
  }

  /**
   * @return A new "enter label" text field
   */
  public static JTextField newEnterLabel() {

    JTextField textField = newTextField(MultiBitUI.RECEIVE_ADDRESS_LABEL_LENGTH);

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(MultiBitUI.RECEIVE_ADDRESS_LABEL_LENGTH));
    textField.setDocument(doc);

    return textField;
  }

  /**
   * @return A new "enter search" text field
   */
  public static JTextField newEnterSearch() {

    return newTextField(60);
  }

  /**
   * @return A new "Select file" text field
   */
  public static JTextField newSelectFile() {

    return newTextField(60);
  }

  /**
   * @param seedTimestamp The seed timestamp to display (e.g. "1850/2")
   *
   * @return A new "display seed timestamp" text field
   */
  public static JTextField newDisplaySeedTimestamp(String seedTimestamp) {

    JTextField textField = newReadOnlyTextField(20);
    textField.setText(seedTimestamp);

    return textField;
  }

  /**
   * @return A new "enter seed timestamp" text field
   */
  public static JTextField newEnterSeedTimestamp() {

    return newTextField(20);

  }

  /**
   * @param bitcoinAddress The Bitcoin address to display
   *
   * @return A new "display Bitcoin address" text field
   */
  public static JTextField newDisplayBitcoinAddress(String bitcoinAddress) {

    JTextField textField = newReadOnlyTextField(40);
    textField.setText(bitcoinAddress);

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

    Font font = textField.getFont().deriveFont((float) MultiBitUI.NORMAL_ICON_SIZE);

    textField.setFont(font);
    textField.setColumns(15);

    // Set the theme
    textField.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
    textField.setBackground(Themes.currentTheme.dataEntryBackground());

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

    Font font = textField.getFont().deriveFont((float) MultiBitUI.NORMAL_ICON_SIZE);

    textField.setFont(font);
    textField.setColumns(15);

    // Set the theme
    textField.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
    textField.setBackground(Themes.currentTheme.dataEntryBackground());

    return textField;
  }

  /**
   * @return A new "Password" text field
   */
  public static JPasswordField newPassword() {

    JPasswordField passwordField = new JPasswordField(MultiBitUI.PASSWORD_LENGTH);

    // Provide a consistent echo character across all components
    passwordField.setEchoChar(getPasswordEchoChar());

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(MultiBitUI.PASSWORD_LENGTH));
    passwordField.setDocument(doc);

    // Set the theme
    passwordField.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
    passwordField.setBackground(Themes.currentTheme.dataEntryBackground());

    return passwordField;
  }

  /**
   * @return A new "Notes" text area
   */
  public static JTextArea newEnterNotes() {

    JTextArea textArea = new JTextArea(6, MultiBitUI.PASSWORD_LENGTH);

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(MultiBitUI.SEED_PHRASE_LENGTH));
    textArea.setDocument(doc);

    // Ensure TAB transfers focus
    AbstractAction transferFocus = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        ((Component) e.getSource()).transferFocus();
      }
    };
    textArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "transferFocus");
    textArea.getActionMap().put("transferFocus", transferFocus);

    // Set the theme
    textArea.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
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

    // Set the theme
    textArea.setBorder(new TextBubbleBorder(Themes.currentTheme.readOnlyBorder()));
    textArea.setBackground(Themes.currentTheme.readOnlyBackground());

    return textArea;

  }

  /**
   * @return A new "seed phrase" text area for entry
   */
  public static JTextArea newEnterSeedPhrase() {

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(MultiBitUI.SEED_PHRASE_LENGTH));

    // Keep this in line with the PASSWORD_AREA constant
    JTextArea textArea = new JTextArea(doc, "", 6, MultiBitUI.PASSWORD_LENGTH);

    // Ensure TAB transfers focus
    AbstractAction transferFocus = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        ((Component) e.getSource()).transferFocus();
      }
    };
    textArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "transferFocus");
    textArea.getActionMap().put("transferFocus", transferFocus);

//    // Ensure we provide a suitable inner margin to allow letters to be clear
//    textArea.setMargin(new Insets(6, 4, 6, 4));
//
    // Ensure line and word wrapping occur as required
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    // Set the theme
    textArea.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
    textArea.setBackground(Themes.currentTheme.dataEntryBackground());
    textArea.setFont(new Font("Courier New", Font.PLAIN, 14));

    return textArea;

  }

  /**
   * @return The themed echo character for password fields
   */
  public static char getPasswordEchoChar() {
    return '\u2022';
  }
}
