package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.core.utils.DocumentMaxLengthFilter;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.components.text_fields.FormattedBitcoinAddressField;
import org.multibit.hd.ui.views.components.text_fields.FormattedDecimalField;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

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

    textField.setOpaque(false);

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

    textField.setOpaque(false);

    return textField;
  }

  /**
   * @param rows    The number of rows (normally 6)
   * @param columns The number of columns (normally 60)
   *
   * @return A new read only text field with default theme
   */
  public static JTextArea newReadOnlyTextArea(int rows, int columns) {

    JTextArea textArea = new JTextArea(rows, columns);

    // Users should not be able to change the data
    textArea.setEditable(false);

    // Set the theme
    textArea.setBorder(new TextBubbleBorder(Themes.currentTheme.readOnlyBorder()));
    textArea.setBackground(Themes.currentTheme.readOnlyBackground());

    textArea.setOpaque(false);

    // Ensure line wrapping occurs correctly
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    // Ensure TAB transfers focus
    AbstractAction transferFocus = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        ((Component) e.getSource()).transferFocus();
      }
    };
    textArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "transferFocus");
    textArea.getActionMap().put("transferFocus", transferFocus);

    return textArea;

  }

  /**
    * @param rows    The number of rows (normally 6)
    * @param columns The number of columns (normally 60)
    *
    * @return A new read only text field with default theme
    */
   public static JTextArea newTextArea(int rows, int columns) {

     JTextArea textArea = new JTextArea(rows, columns);

     // Set the theme
     textArea.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
     textArea.setBackground(Themes.currentTheme.dataEntryBackground());

     textArea.setOpaque(false);

     // Ensure line wrapping occurs correctly
     textArea.setLineWrap(true);
     textArea.setWrapStyleWord(true);

     // Ensure TAB transfers focus
     AbstractAction transferFocus = new AbstractAction() {
       public void actionPerformed(ActionEvent e) {
         ((Component) e.getSource()).transferFocus();
       }
     };
     textArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "transferFocus");
     textArea.getActionMap().put("transferFocus", transferFocus);

     return textArea;

   }

  /**
   * @param listener The document listener for detecting changes to the content
   * @param rows     The number of rows (normally 6)
   * @param columns  The number of columns (normally 60)
   *
   * @return A new read only length limited text field with default theme
   */
  public static JTextArea newReadOnlyLengthLimitedTextArea(DocumentListener listener, int rows, int columns) {

    JTextArea textArea = newReadOnlyTextArea(rows, columns);

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(rows * columns));
    textArea.setDocument(doc);

    // Ensure we monitor changes
    doc.addDocumentListener(listener);

    return textArea;

  }

  /**
   * @return A new "enter transaction label" text field
   */
  public static JTextField newEnterTransactionLabel() {

    JTextField textField = newTextField(MultiBitUI.RECEIVE_ADDRESS_LABEL_LENGTH);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.TRANSACTION_LABEL);

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(MultiBitUI.RECEIVE_ADDRESS_LABEL_LENGTH));
    textField.setDocument(doc);

    return textField;
  }

  /**
   * @return A new "enter tag" text field
   */
  public static JTextField newEnterTag() {

    JTextField textField = newTextField(20);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.TAGS);

    return textField;
  }

  /**
   * @return A new "enter search" text field
   */
  public static JTextField newEnterSearch() {

    JTextField textField = newTextField(60);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.SEARCH);

    return textField;
  }

  /**
   * @return A new "Select file" text field
   */
  public static JTextField newSelectFile() {

    JTextField textField = newTextField(60);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.SELECT_FILE);

    return textField;
  }

  /**
   * @param seedTimestamp The seed timestamp to display (e.g. "1850/2")
   *
   * @return A new "display seed timestamp" text field
   */
  public static JTextField newDisplaySeedTimestamp(String seedTimestamp) {

    JTextField textField = newReadOnlyTextField(20);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.TIMESTAMP);

    textField.setText(seedTimestamp);

    return textField;
  }

  /**
   * @return A new "enter seed timestamp" text field
   */
  public static JTextField newEnterSeedTimestamp() {

    JTextField textField = newTextField(20);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.TIMESTAMP);

    return textField;

  }

  /**
   * @param listener The document listener for detecting changes to the content
   * @param readOnly True if the field should be read only (i.e. in multi-edit mode)
   *
   * @return A new "enter name" text field
   */
  public static JTextField newEnterName(DocumentListener listener, boolean readOnly) {

    JTextField textField = readOnly ? newReadOnlyTextField(40) : newTextField(40);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.NAME);

    textField.getDocument().addDocumentListener(listener);

    return textField;

  }

  /**
   * @param listener The document listener for detecting changes to the content
   * @param readOnly True if the field should be read only (i.e. in multi-edit mode)
   *
   * @return A new "enter email address" text field
   */
  public static JTextField newEnterEmailAddress(DocumentListener listener, boolean readOnly) {

    JTextField textField = readOnly ? newReadOnlyTextField(40) : newTextField(40);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.EMAIL_ADDRESS);

    // Detect changes
    textField.getDocument().addDocumentListener(listener);

    return textField;

  }

  /**
   * @param listener The document listener for detecting changes to the content
   * @param readOnly True if the field should be read only (i.e. in multi-edit mode)
   *
   * @return A new "enter Bitcoin address" text field
   */
  public static FormattedBitcoinAddressField newEnterBitcoinAddress(DocumentListener listener, boolean readOnly) {

    FormattedBitcoinAddressField textField = new FormattedBitcoinAddressField(BitcoinNetwork.current().get(), readOnly);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.BITCOIN_ADDRESS);

    // Detect changes
    textField.getDocument().addDocumentListener(listener);

    return textField;

  }

  /**
   * @param bitcoinAddress The Bitcoin address to display
   *
   * @return A new "display Bitcoin address" text field
   */
  public static JTextField newDisplayBitcoinAddress(String bitcoinAddress) {

    JTextField textField = newReadOnlyTextField(34);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.BITCOIN_ADDRESS);

    textField.setText(bitcoinAddress);

    return textField;
  }

  /**
   * @param listener The document listener for detecting changes to the content
   * @param readOnly True if the field should be read only (i.e. in multi-edit mode)
   *
   * @return A new "enter extended public key" text field
   */
  public static JTextField newEnterExtendedPublicKey(DocumentListener listener, boolean readOnly) {

    JTextField textField = readOnly ? newReadOnlyTextField(40) : newTextField(40);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.EXTENDED_PUBLIC_KEY);

    // Detect changes
    textField.getDocument().addDocumentListener(listener);

    // Currently the extended public field does nothing so disable
    textField.setEnabled(false);

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

    // The max edit length varies depending on the Bitcoin symbol (e.g. coins have no decimal)
    int maxEditLength = BitcoinSymbol.current().maxRepresentationLength();

    FormattedDecimalField textField = new FormattedDecimalField(0, maximum, decimalPlaces, maxEditLength);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.BITCOIN_AMOUNT);

    Font font = textField.getFont().deriveFont((float) MultiBitUI.NORMAL_ICON_SIZE);

    textField.setFont(font);
    textField.setColumns(15);

    // Set the theme
    textField.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
    textField.setBackground(Themes.currentTheme.dataEntryBackground());

    textField.setOpaque(false);

    return textField;
  }

  /**
   * @param maximum The largest value than can be accepted (typically the wallet local balance) - no financial calculations are performed on this value
   *
   * @return A new text field for local currency amount entry
   */
  public static FormattedDecimalField newLocalAmount(double maximum) {

    // Use the current configuration to provide the decimal places
    int decimalPlaces = Configurations
      .currentConfiguration
      .getBitcoin()
      .getLocalDecimalPlaces();

    // Allow an extra 6 digits for local currency
    int maxEditLength = BitcoinSymbol.current().maxRepresentationLength() + 6;

    FormattedDecimalField textField = new FormattedDecimalField(0, maximum, decimalPlaces, maxEditLength);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.LOCAL_AMOUNT);

    Font font = textField.getFont().deriveFont((float) MultiBitUI.NORMAL_ICON_SIZE);

    textField.setFont(font);
    textField.setColumns(15);

    // Set the theme
    textField.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
    textField.setBackground(Themes.currentTheme.dataEntryBackground());

    textField.setOpaque(false);

    return textField;
  }

  /**
   * @return A new "Password" text field
   */
  public static JPasswordField newPassword() {

    JPasswordField passwordField = new JPasswordField(MultiBitUI.PASSWORD_LENGTH);

    // Ensure it is accessible
    AccessibilityDecorator.apply(passwordField, MessageKey.ENTER_PASSWORD, MessageKey.ENTER_PASSWORD_TOOLTIP);

    // Provide a consistent echo character across all components
    passwordField.setEchoChar(getPasswordEchoChar());

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(MultiBitUI.PASSWORD_LENGTH));
    passwordField.setDocument(doc);

    doc.addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        System.out.println("TextBoxes#newPassword#DocumentListener# saw an insertUpdate");
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        System.out.println("TextBoxes#newPassword#DocumentListener# saw an removeUpdate");
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        System.out.println("TextBoxes#newPassword#DocumentListener# saw an changeUpdate");
      }
    });

    // Set the theme
    passwordField.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
    passwordField.setBackground(Themes.currentTheme.dataEntryBackground());

    passwordField.setOpaque(false);

    return passwordField;
  }

  /**
   * @param listener The document listener for detecting changes to the content
   *
   * @return A new default public "notes" text area
   */
  public static JTextArea newEnterNotes(DocumentListener listener) {

    JTextArea textArea = TextBoxes.newEnterPrivateNotes(listener, MultiBitUI.PASSWORD_LENGTH);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textArea, MessageKey.NOTES, MessageKey.NOTES_TOOLTIP);

    return textArea;
  }

  /**
   * @return A new "message" text area (usually for signing for verifying)
   */
  public static JTextArea newEnterMessage() {

    JTextArea textArea = new JTextArea(4, MultiBitUI.PASSWORD_LENGTH);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textArea, MessageKey.MESSAGE, MessageKey.MESSAGE_TOOLTIP);

    textArea.setOpaque(false);

    // Ensure line wrapping occurs correctly
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

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
   * @param listener The document listener for detecting changes to the content
   *
   * @return A new default "private notes" text area
   */
  public static JTextArea newEnterPrivateNotes(DocumentListener listener) {
    return TextBoxes.newEnterPrivateNotes(listener, MultiBitUI.PASSWORD_LENGTH);
  }

  /**
   * @param listener The document listener for detecting changes to the content
   *
   * @return A new "Notes" text area
   */
  public static JTextArea newEnterPrivateNotes(DocumentListener listener, int width) {

    JTextArea textArea = new JTextArea(6, width);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textArea, MessageKey.PRIVATE_NOTES, MessageKey.PRIVATE_NOTES_TOOLTIP);

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(MultiBitUI.SEED_PHRASE_LENGTH));
    textArea.setDocument(doc);

    // Ensure we monitor changes
    doc.addDocumentListener(listener);

    // Ensure line wrapping occurs correctly
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

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

    textArea.setOpaque(false);

    return textArea;
  }

  /**
   * <p>Create a new truncated localised comma separated list label (e.g. "a, b, c ..."</p>
   *
   * @param contents  The contents to join into a localised comma-separated list
   * @param maxLength The maximum length of the resulting string (including ellipsis)
   *
   * @return A new truncated list text area
   */
  public static JTextArea newTruncatedList(Collection<String> contents, int maxLength) {

    JTextArea textArea = new JTextArea(Languages.truncatedList(contents, maxLength));

    textArea.setBorder(BorderFactory.createEmptyBorder());
    textArea.setEditable(false);

    // Ensure the background is transparent
    textArea.setBackground(new Color(0, 0, 0, 0));
    textArea.setForeground(Themes.currentTheme.text());
    textArea.setOpaque(false);

    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    return textArea;
  }

  /**
   * @return A new "seed phrase" text area for display only (no copy/paste etc)
   */
  public static JTextArea newDisplaySeedPhrase() {

    // Build off the enter seed phrase
    JTextArea textArea = newEnterSeedPhrase();

    // Ensure it is accessible
    AccessibilityDecorator.apply(textArea, MessageKey.SEED_PHRASE);

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

    // Ensure it is accessible
    AccessibilityDecorator.apply(textArea, MessageKey.SEED_PHRASE);

    // Ensure TAB transfers focus
    AbstractAction transferFocus = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        ((Component) e.getSource()).transferFocus();
      }
    };
    textArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "transferFocus");
    textArea.getActionMap().put("transferFocus", transferFocus);

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
   * @param listener A document listener to detect changes
   *
   * @return A new "enter API key" text field
   */
  public static JTextField newEnterApiKey(DocumentListener listener) {

    JTextField textField = newTextField(40);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.ENTER_ACCESS_CODE);

    textField.getDocument().addDocumentListener(listener);

    return textField;

  }

  /**
   * @return The themed echo character for password fields
   */
  public static char getPasswordEchoChar() {
    return '\u2022';
  }

}
