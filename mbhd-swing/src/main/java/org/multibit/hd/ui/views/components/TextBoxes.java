package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.BitcoinNetwork;
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
 */
public class TextBoxes {

  /**
   * The maximum display width of a V1 Trezor device (allowing for icon)
   */
  private static final int TREZOR_MAX_COLUMNS = 22;

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
   * @param nameKey    The name key for accessibility
   * @param tooltipKey The tooltip key for accessibility
   *
   * @return A new text field with default theme
   */
  public static JTextField newReadOnlyTextField(int columns, MessageKey nameKey, MessageKey tooltipKey) {

    JTextField textField = new JTextField(columns);

    // Users should not be able to change the data
    textField.setEditable(false);

    // Set the theme
    textField.setBorder(new TextBubbleBorder(Themes.currentTheme.readOnlyBorder()));
    textField.setBackground(Themes.currentTheme.readOnlyBackground());

    textField.setOpaque(false);

    // Ensure FEST can find it
    AccessibilityDecorator.apply(textField, nameKey, tooltipKey);

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
    AccessibilityDecorator.apply(textField, MessageKey.TRANSACTION_LABEL, MessageKey.TRANSACTION_LABEL_TOOLTIP);

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(MultiBitUI.RECEIVE_ADDRESS_LABEL_LENGTH));
    textField.setDocument(doc);

    return textField;
  }

  /**
   * @return A new "enter QR code label" text field
   */
  public static JTextField newEnterQRCodeLabel() {

    JTextField textField = newTextField(MultiBitUI.RECEIVE_ADDRESS_LABEL_LENGTH);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.QR_CODE_LABEL, MessageKey.QR_CODE_LABEL_TOOLTIP);

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
    AccessibilityDecorator.apply(textField, MessageKey.TAGS, MessageKey.TAGS_TOOLTIP);

    return textField;
  }

  /**
   * @return A new "enter search" text field
   */
  public static JTextField newEnterSearch() {

    JTextField textField = newTextField(60);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.SEARCH, MessageKey.SEARCH_TOOLTIP);

    return textField;
  }

  /**
   * @return A new "Select file" text field
   */
  public static JTextField newSelectFile() {

    JTextField textField = newTextField(60);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.SELECT_FILE, MessageKey.SELECT_FILE_TOOLTIP);

    return textField;
  }

  /**
   * @param seedTimestamp The seed timestamp to display (e.g. "1850/2")
   *
   * @return A new "display seed timestamp" text field
   */
  public static JTextField newDisplaySeedTimestamp(String seedTimestamp) {

    JTextField textField = newReadOnlyTextField(20, MessageKey.TIMESTAMP, MessageKey.TIMESTAMP_TOOLTIP);

    textField.setText(seedTimestamp);

    return textField;
  }

  /**
   * @return A new "enter seed timestamp" text field
   */
  public static JTextField newEnterSeedTimestamp() {

    JTextField textField = newTextField(20);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.TIMESTAMP, MessageKey.TIMESTAMP_TOOLTIP);

    return textField;

  }

  /**
   * @param listener The document listener for detecting changes to the content
   * @param readOnly True if the field should be read only (i.e. in multi-edit mode)
   *
   * @return A new "enter name" text field
   */
  public static JTextField newEnterName(DocumentListener listener, boolean readOnly) {

    JTextField textField;
    if (readOnly) {
      textField = newReadOnlyTextField(40, MessageKey.NAME, MessageKey.NAME_TOOLTIP);
    } else {
      textField = newTextField(40);
      AccessibilityDecorator.apply(textField, MessageKey.NAME, MessageKey.NAME_TOOLTIP);
    }

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

    JTextField textField;
    if (readOnly) {
      textField = newReadOnlyTextField(40, MessageKey.EMAIL_ADDRESS, MessageKey.EMAIL_ADDRESS_TOOLTIP);
    } else {
      textField = newTextField(40);
      AccessibilityDecorator.apply(textField, MessageKey.EMAIL_ADDRESS, MessageKey.EMAIL_ADDRESS_TOOLTIP);
    }

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
    AccessibilityDecorator.apply(textField, MessageKey.BITCOIN_ADDRESS, MessageKey.BITCOIN_ADDRESS_TOOLTIP);

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

    JTextField textField = newReadOnlyTextField(34, MessageKey.BITCOIN_ADDRESS, MessageKey.BITCOIN_ADDRESS_TOOLTIP);

    textField.setText(bitcoinAddress);

    return textField;
  }

  /**
   * @return A new "display recipient Bitcoin addresses" multi-line text field
   */
  public static JTextArea newDisplayRecipientBitcoinAddresses() {

    // 3 rows should be sufficient to cover all transactions from us
    JTextArea textArea = newReadOnlyTextArea(3, 38);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textArea, MessageKey.RECIPIENT, MessageKey.RECIPIENT_TOOLTIP);

    return textArea;
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
    AccessibilityDecorator.apply(textField, MessageKey.BITCOIN_AMOUNT, MessageKey.BITCOIN_AMOUNT_TOOLTIP);

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
    AccessibilityDecorator.apply(textField, MessageKey.LOCAL_AMOUNT, MessageKey.LOCAL_AMOUNT_TOOLTIP);

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

    // Set the theme
    passwordField.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
    passwordField.setBackground(Themes.currentTheme.dataEntryBackground());

    passwordField.setOpaque(false);

    // Allow copy/paste into password field
    //
    // This is allowed over the default of disabled for these reasons:
    // 1. It encourages much stronger passwords through a password manager (LastPass, KeyPass etc)
    // 2. Using the clipboard is as secure as the keyboard (i.e. both are visible to all)
    // 3. Improved user experience overall
    //
    // One attack vector is that a user may place a password into the clipboard and then
    // leave their computer unattended and therefore vulnerable to Mallory spending on their
    // behalf. However a user requiring the clipboard to transfer the password would likely
    // have higher than average security awareness and not put themselves in this position.
    passwordField.putClientProperty("JPasswordField.cutCopyAllowed", true);

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
    AccessibilityDecorator.apply(textArea, MessageKey.SEED_PHRASE, MessageKey.SEED_PHRASE_TOOLTIP);

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

    // Wider than password to prevent push down on 24 word hidden text
    JTextArea textArea = new JTextArea(doc, "", 6, MultiBitUI.SEED_PHRASE_WIDTH);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textArea, MessageKey.SEED_PHRASE, MessageKey.SEED_PHRASE_TOOLTIP);

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
   * @param panelName The panel name used as the basis for the FEST name
   *
   * @return A text area with similar dimensions to a V1 Trezor after MiG resizing
   */
  public static JTextArea newTrezorV1Display(String panelName) {

    JTextArea trezorDisplay = newReadOnlyTextArea(5, 50);

    // Ensure FEST can find it
    trezorDisplay.setName(panelName + ".trezor_display");

    return trezorDisplay;

  }

  /**
   * @param listener A document listener to detect changes
   *
   * @return A new "enter API key" text field
   */
  public static JTextField newEnterApiKey(DocumentListener listener) {

    JTextField textField = newTextField(40);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.ENTER_ACCESS_CODE, MessageKey.ENTER_ACCESS_CODE_TOOLTIP);

    textField.getDocument().addDocumentListener(listener);

    return textField;

  }

  /**
   * @return The themed echo character for credentials fields
   */
  public static char getPasswordEchoChar() {
    return '\u2022';
  }

  /**
   * @return A new "enter Trezor label" limited length text field
   */
  public static JTextField newEnterTrezorLabel() {

    JTextField textField = newTextField(TREZOR_MAX_COLUMNS);

    // Limit the length of the underlying document
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.setDocumentFilter(new DocumentMaxLengthFilter(TREZOR_MAX_COLUMNS));
    textField.setDocument(doc);

    // Ensure it is accessible
    AccessibilityDecorator.apply(textField, MessageKey.ENTER_TREZOR_LABEL, MessageKey.ENTER_TREZOR_LABEL_TOOLTIP);

    return textField;

  }
}
