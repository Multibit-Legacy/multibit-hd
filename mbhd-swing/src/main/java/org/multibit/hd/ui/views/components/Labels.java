package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.HtmlUtils;
import org.multibit.hd.ui.views.animations.RotatingIcon;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

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

  /**
   * Utilities have no public constructor
   */
  private Labels() {
  }


  /**
   * TODO make this package private and create specialised labels
   *
   * @param key    The resource key for the language message text
   * @param values The data values for token replacement in the message text
   *
   * @return A new label with default styling
   */
  public static JLabel newLabel(MessageKey key, Object... values) {

    JLabel label = new JLabel(Languages.safeText(key, values));

    // Apply theme
    label.setForeground(Themes.currentTheme.text());

    return label;

  }

  /**
   * @return A new blank label with default styling
   */
  public static JLabel newBlankLabel() {

    JLabel label = new JLabel("");

    // Apply theme
    label.setForeground(Themes.currentTheme.text());

    return label;

  }

  /**
   * TODO Make this package private and create specialised methods to reduce key leaks
   * <p>A convenience method for creating a themed label with direct text. This is not internationalised.</p>
   *
   * @return A new value label with default styling for placing direct text
   */
  public static JLabel newValueLabel(String value) {

    JLabel label = new JLabel(value);

    // Apply theme
    label.setForeground(Themes.currentTheme.text());

    return label;

  }

  /**
   * @param key The message key
   *
   * @return A new label with appropriate font and theme
   */
  public static JLabel newTitleLabel(MessageKey key) {

    JLabel label = newLabel(key);

    // Font
    Font font = label.getFont().deriveFont(MultiBitUI.BALANCE_HEADER_LARGE_FONT_SIZE);
    label.setFont(font);

    // Theme
    label.setForeground(Themes.currentTheme.text());

    return label;

  }

  /**
   * <p>Create a new label with appropriate font/theme for a note. Interpret the contents of the text as Markdown for HTML translation.</p>
   *
   * @param keys   The message keys for each line referencing simple HTML (standard wrapping/breaking elements like {@literal <html></html>} and {@literal <br/>} will be provided)
   * @param values The substitution values for each line if applicable
   *
   * @return A new label with HTML formatting to correctly render the line break and contents
   */
  static JLabel newNoteLabel(CoreMessageKey[] keys, Object[][] values) {

    String[] lines = new String[keys.length];
    for (int i = 0; i < keys.length; i++) {
      if (values.length > 0) {
        // Substitution is required
        lines[i] = Languages.safeText(keys[i], values[i]);
      } else {
        // Key only
        lines[i] = Languages.safeText(keys[i]);
      }
    }

    // Wrap in HTML to ensure LTR/RTL and line breaks are respected
    JLabel label = new JLabel(HtmlUtils.localiseWithLineBreaks(lines));

    // Theme
    label.setForeground(Themes.currentTheme.text());

    return label;

  }

  /**
   * <p>Create a new label with appropriate font/theme for a note. Interpret the contents of the text as Markdown for HTML translation.</p>
   *
   * @param keys   The message keys for each line referencing simple HTML (standard wrapping/breaking elements like {@literal <html></html>} and {@literal <br/>} will be provided)
   * @param values The substitution values for each line if applicable
   *
   * @return A new label with HTML formatting to correctly render the line break and contents
   */
  static JLabel newNoteLabel(MessageKey[] keys, Object[][] values) {

    String[] lines = new String[keys.length];
    for (int i = 0; i < keys.length; i++) {
      if (values.length > 0) {
        // Substitution is required
        lines[i] = Languages.safeText(keys[i], values[i]);
      } else {
        // Key only
        lines[i] = Languages.safeText(keys[i]);
      }
    }

    // Wrap in HTML to ensure LTR/RTL and line breaks are respected
    JLabel label = new JLabel(HtmlUtils.localiseWithLineBreaks(lines));

    // Theme
    label.setForeground(Themes.currentTheme.text());

    return label;

  }

  /**
   * <p>A "status" label sets a label with a check or cross icon</p>
   *
   * @param key    The message key
   * @param values The substitution values
   * @param status True if a check icon is required, false for a cross
   *
   * @return A new label with icon binding to allow the AwesomeDecorator to update it
   */
  static JLabel newStatusLabel(MessageKey key, Object[] values, boolean status) {
    return newStatusLabel(Optional.of(key), values, Optional.of(status));
  }

  /**
   * <p>A "status" label sets a label with no icon, a check or cross icon</p>
   *
   * @param key    The message key - if not present then empty text is put on the label
   * @param values The substitution values
   * @param status True if a check icon is required, false for a cross
   *
   * @return A new label with icon binding to allow the AwesomeDecorator to update it
   */
  public static JLabel newStatusLabel(Optional<MessageKey> key, Object[] values, Optional<Boolean> status) {

    JLabel label;

    if (key.isPresent()) {
      label = newLabel(key.get(), values);
    } else {
      label = newBlankLabel();
    }

    decorateStatusLabel(label, status);

    return label;
  }

  public static void decorateStatusLabel(JLabel statusLabel, Optional<Boolean> status) {

    if (status.isPresent()) {
      if (status.get()) {
        AwesomeDecorator.bindIcon(AwesomeIcon.CHECK, statusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
      } else {
        AwesomeDecorator.bindIcon(AwesomeIcon.TIMES, statusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
      }
    }

  }

  /**
   * @param image The optional image
   *
   * @return A new label with the image or a placeholder if not present
   */
  public static JLabel newImageLabel(Optional<BufferedImage> image) {

    if (image.isPresent()) {
      JLabel label = new JLabel(new ImageIcon(image.get()));

      // Apply theme
      label.setForeground(Themes.currentTheme.text());

      return label;
    }

    // Fall back to a default image
    JLabel label = newBlankLabel();

    AwesomeDecorator.applyIcon(AwesomeIcon.USER, label, true, MultiBitUI.LARGE_ICON_SIZE);

    return label;

  }

  /**
   * @param walletPath The path to the image resource within the current wallet
   *
   * @return A new label with the image or a placeholder if not present
   */
  public static JLabel newWalletImageLabel(String walletPath) {
    final BufferedImage image;
    try {
      image = ImageIO.read(new File(walletPath));
      return new JLabel(new ImageIcon(image));
    } catch (IOException e) {
      // Fall back to a default image
    }

    JLabel label = new JLabel();
    AwesomeDecorator.applyIcon(AwesomeIcon.USER, label, true, MultiBitUI.LARGE_ICON_SIZE);
    return label;

  }

  /**
   * @param status True if the status is "good"
   *
   * @return A new "verification" status label (confirms user has done something right)
   */
  public static JLabel newVerificationStatus(boolean status) {

    return newStatusLabel(MessageKey.VERIFICATION_STATUS, null, status);
  }

  /**
   * @param status True if the status is "good"
   *
   * @return A new "validity" status label (confirms user has made a valid combination)
   */
  public static JLabel newErrorStatus(boolean status) {

    return newStatusLabel(MessageKey.ERROR, null, status);
  }

  /**
   * @param status True if the status is "good"
   *
   * @return A new "seed phrase created" status label
   */
  public static JLabel newSeedPhraseCreatedStatus(boolean status) {
    return newStatusLabel(MessageKey.SEED_PHRASE_CREATED_STATUS, null, status);
  }

  /**
   * @param status True if the status is "good"
   *
   * @return A new "wallet password created" status label
   */
  public static JLabel newWalletPasswordCreatedStatus(boolean status) {
    return newStatusLabel(MessageKey.WALLET_PASSWORD_CREATED_STATUS, null, status);
  }

  /**
   * @param status True if the status is "good"
   *
   * @return A new "wallet created" status label
   */
  public static JLabel newWalletCreatedStatus(boolean status) {
    return newStatusLabel(MessageKey.WALLET_CREATED_STATUS, null, status);
  }

  /**
   * @param status True if the status is "good"
   *
   * @return A new "backup location" status label
   */
  public static JLabel newBackupLocationStatus(boolean status) {
    return newStatusLabel(MessageKey.BACKUP_LOCATION_STATUS, null, status);
  }

  /**
   * @return A new "spinner" label (indicates that something is happening asynchronously)
   */
  public static JLabel newSpinner() {

    JLabel label = newBlankLabel();

    Icon icon = new RotatingIcon(AwesomeDecorator.createIcon(
      AwesomeIcon.SPINNER,
      Themes.currentTheme.text(),
      MultiBitUI.NORMAL_PLUS_ICON_SIZE
    ), label);

    label.setIcon(icon);

    label.setSize(icon.getIconWidth() + 20, icon.getIconHeight() + 20);

    return label;
  }

  /**
   * @return A new "select language" label
   */
  public static JLabel newSelectLanguageLabel() {

    JLabel label = Labels.newLabel(MessageKey.DISPLAY_LANGUAGE);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.GLOBE,
      label,
      true,
      MultiBitUI.NORMAL_PLUS_ICON_SIZE
    );

    return label;
  }

  /**
   * @return A new "select theme" label
   */
  public static JLabel newSelectThemeLabel() {

    return Labels.newLabel(MessageKey.DISPLAY_THEME);

  }

  /**
   * @return A new "select decimal separator" label
   */
  public static JLabel newSelectDecimalLabel() {

    return Labels.newLabel(MessageKey.SELECT_DECIMAL_SEPARATOR);
  }

  /**
   * @return A new "select grouping separator" label
   */
  public static JLabel newSelectGroupingLabel() {

    return Labels.newLabel(MessageKey.SELECT_GROUPING_SEPARATOR);
  }

  /**
   * @return A new "select local currency symbol" label
   */
  public static JLabel newLocalSymbolLabel() {

    return Labels.newLabel(MessageKey.SELECT_LOCAL_SYMBOL);
  }

  /**
   * @return A new "select local currency code" label
   */
  public static JLabel newLocalCurrencyLabel() {

    return Labels.newLabel(MessageKey.SELECT_LOCAL_CURRENCY);
  }

  /**
   * @return A new "enter access code" label (for API keys)
   */
  public static JLabel newApiKeyLabel() {

    return Labels.newLabel(MessageKey.ENTER_ACCESS_CODE);
  }

  /**
   * @return A new "select local Bitcoin symbol" label
   */
  public static JLabel newBitcoinSymbolLabel() {

    return Labels.newLabel(MessageKey.SELECT_BITCOIN_SYMBOL);
  }

  /**
   * @return A new "select placement" label
   */
  public static JLabel newPlacementLabel() {

    return Labels.newLabel(MessageKey.SELECT_PLACEMENT);
  }

  /**
   * @return A new "example" label
   */
  public static JLabel newExampleLabel() {

    return Labels.newLabel(MessageKey.EXAMPLE);
  }

  /**
   * @return A new "version" label
   */
  public static JLabel newVersionLabel() {

    return Labels.newLabel(MessageKey.VERSION);
  }

  /**
   * @return A new "select exchange rate provider" label
   */
  public static JLabel newSelectExchangeRateProviderLabel() {

    return Labels.newLabel(MessageKey.SELECT_EXCHANGE_RATE_PROVIDER);
  }

  /**
   * @param mouseAdapter The mouse adapter that provides the event handling
   *
   * @return A new panel close "X" label with icon
   */
  public static JLabel newPanelCloseLabel(MouseAdapter mouseAdapter) {

    JLabel panelCloseLabel = newBlankLabel();

    // Font
    Font panelCloseFont = panelCloseLabel.getFont().deriveFont(MultiBitUI.PANEL_CLOSE_FONT_SIZE);
    panelCloseLabel.setFont(panelCloseFont);

    AwesomeDecorator.bindIcon(AwesomeIcon.TIMES, panelCloseLabel, true, 16);
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
   * @param style The display style to use depending on the context
   *
   * @return A new collection of labels that together form a balance display
   */
  public static JLabel[] newBalanceLabels(DisplayAmountStyle style) {

    Preconditions.checkNotNull(style, "'style' must be present");

    JLabel leadingBalanceLabel = newBlankLabel();
    JLabel primaryBalanceLabel = newBlankLabel();
    JLabel secondaryBalanceLabel = newBlankLabel();
    JLabel trailingSymbolLabel = newBlankLabel();
    JLabel exchangeLabel = newBlankLabel();

    // Font
    final Font largeFont;
    final Font normalFont;

    switch (style) {
      case HEADER:
        largeFont = primaryBalanceLabel.getFont().deriveFont(MultiBitUI.BALANCE_HEADER_LARGE_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(MultiBitUI.BALANCE_HEADER_NORMAL_FONT_SIZE);
        break;
      case TRANSACTION_DETAIL_AMOUNT:
        largeFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, MultiBitUI.BALANCE_TRANSACTION_LARGE_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, MultiBitUI.BALANCE_TRANSACTION_NORMAL_FONT_SIZE);
        break;
      case FEE_AMOUNT:
        largeFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, MultiBitUI.BALANCE_FEE_NORMAL_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, MultiBitUI.BALANCE_FEE_NORMAL_FONT_SIZE);
        break;
      case PLAIN:
         largeFont = primaryBalanceLabel.getFont().deriveFont(Font.PLAIN, MultiBitUI.BALANCE_FEE_NORMAL_FONT_SIZE);
         normalFont = primaryBalanceLabel.getFont().deriveFont(Font.PLAIN, MultiBitUI.BALANCE_FEE_NORMAL_FONT_SIZE);
         break;
      default:
        throw new IllegalStateException("Unknown style:" + style.name());
    }

    leadingBalanceLabel.setFont(largeFont);

    primaryBalanceLabel.setFont(largeFont);

    secondaryBalanceLabel.setFont(normalFont);

    trailingSymbolLabel.setFont(largeFont);

    exchangeLabel.setFont(normalFont);

    // Theme
    if (style != DisplayAmountStyle.PLAIN) {
      secondaryBalanceLabel.setForeground(Themes.currentTheme.fadedText());
    }

    return new JLabel[]{

      leadingBalanceLabel,
      primaryBalanceLabel,
      secondaryBalanceLabel,
      trailingSymbolLabel,
      exchangeLabel
    };

  }

  /**
   * @return A new "Amount" label
   */
  public static JLabel newAmount() {
    return newLabel(MessageKey.AMOUNT);
  }

  /**
   * @return A new "approximately" symbol
   */
  public static JLabel newApproximately() {

    JLabel label = newLabel(MessageKey.APPROXIMATELY);

    Font font = label.getFont().deriveFont(Font.BOLD, (float) MultiBitUI.NORMAL_ICON_SIZE);
    label.setFont(font);

    return label;
  }

  /**
   * @return A new "select file" label
   */
  public static JLabel newSelectFile() {

    return newLabel(MessageKey.SELECT_FILE);
  }

  /**
   * @return A new "select folder" label
   */
  public static JLabel newSelectFolder() {

    return newLabel(MessageKey.SELECT_FOLDER);
  }

  /**
   * @return A new "Enter password" label
   */
  public static JLabel newEnterPassword() {

    return newLabel(MessageKey.ENTER_PASSWORD);
  }

  /**
   * @return A new "Confirm password" label
   */
  public static JLabel newConfirmPassword() {

    return newLabel(MessageKey.PASSWORD_VERIFIED);
  }

  /**
   * @return The current exchange name from the configuration
   */
  public static JLabel newCurrentExchangeName() {

    String exchangeName = ExchangeKey.current().getExchangeName();

    return newLabel(MessageKey.EXCHANGE_RATE_PROVIDER, exchangeName);
  }

  /**
   * @return A new "You are about to send" message
   */
  public static JLabel newConfirmSendAmount() {

    return newLabel(MessageKey.CONFIRM_SEND_MESSAGE);
  }

  /**
   * @return A new "recipient" message
   */
  public static JLabel newRecipient() {
    return newLabel(MessageKey.RECIPIENT);
  }

  /**
   * @return A new "recipient summary" label
   */
  public static JLabel newRecipientSummary(Recipient recipient) {

    return newLabel(MessageKey.RECIPIENT_SUMMARY, recipient.getSummary());

  }

  /**
   * @param transactionFee The transaction fee in satoshis
   *
   * @return A new "transaction fee" message
   */
  public static JLabel newTransactionFee(BigInteger transactionFee) {
    return newLabel(MessageKey.TRANSACTION_FEE);
  }


  /**
   * @return A new "circle" label
   */
  public static JLabel newCircle() {

    JLabel label = newBlankLabel();

    AwesomeDecorator.bindIcon(AwesomeIcon.CIRCLE, label, false, MultiBitUI.SMALL_ICON_SIZE);

    return label;
  }

  /**
   * @param developerFee The developer fee in satoshis
   *
   * @return A new "developer fee" message
   */
  public static JLabel newDeveloperFee(BigInteger developerFee) {
    return newLabel(MessageKey.DEVELOPER_FEE);
  }

  /**
   * @return A new "seed size" message
   */
  public static JLabel newSeedSize() {
    return newLabel(MessageKey.SEED_SIZE);
  }

  /**
   * @return A new "timestamp" message
   */
  public static JLabel newTimestamp() {
    return newLabel(MessageKey.TIMESTAMP);
  }

  /**
   * @return A new "transaction label" message for use with receiving addresses
   */
  public static JLabel newTransactionLabel() {
    return newLabel(MessageKey.TRANSACTION_LABEL);
  }

  /**
   * @return A new "description" label
   */
  public static JLabel newDescription() {
    return newLabel(MessageKey.DESCRIPTION);
  }

  /**
   * @return A new "contact name" label
   */
  public static JLabel newName() {
    return newLabel(MessageKey.NAME);
  }

  /**
   * @return A new "contact email" label
   */
  public static JLabel newEmailAddress() {
    return newLabel(MessageKey.EMAIL_ADDRESS);
  }

  /**
   * @return A new "contact Bitcoin address" label
   */
  public static JLabel newBitcoinAddress() {
    return newLabel(MessageKey.BITCOIN_ADDRESS);
  }

  /**
   * @return A new "contact extended public key" label
   */
  public static JLabel newExtendedPublicKey() {
    return newLabel(MessageKey.EXTENDED_PUBLIC_KEY);
  }

  /**
   * @return A new "names" label
   */
  public static JLabel newNames() {
    return newLabel(MessageKey.NAMES);
  }

  /**
   * @return A new "tags" label
   */
  public static JLabel newTags() {
    return newLabel(MessageKey.TAGS);
  }

  /**
   * @return A new "QR code label" message for use with receiving addresses
   */
  public static JLabel newQRCodeLabelLabel() {
    return newLabel(MessageKey.QR_CODE_LABEL_LABEL);
  }

  /**
   * @return a new "select alert sound" for sound settings
   */
  public static JLabel newSelectAlertSound() {
    return newLabel(MessageKey.ALERT_SOUND);
  }

  /**
   * @return a new "select receive sound" for sound settings
   */
  public static JLabel newSelectReceiveSound() {
    return newLabel(MessageKey.RECEIVE_SOUND);
  }

  /**
   * @return A new "notes" message
   */
  public static JLabel newNotes() {
    return newLabel(MessageKey.NOTES);
  }

  /**
   * @return A new "multi edit note" label
   */
  public static JLabel newMultiEditNote() {
    return newLabel(MessageKey.MULTI_EDIT_NOTE);
  }

  /**
   * @return A new "welcome" note
   */
  public static JLabel newWelcomeNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.WELCOME_NOTE_1,
      MessageKey.WELCOME_NOTE_2,
      MessageKey.WELCOME_NOTE_3,
      MessageKey.WELCOME_NOTE_4
    }, new Object[][]{});
  }

  /**
   * @return A new "about" note
   */
  public static JLabel newAboutNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.ABOUT_NOTE_1,
      MessageKey.ABOUT_NOTE_2,
      MessageKey.ABOUT_NOTE_3
    }, new Object[][]{});
  }

  /**
   * @return A new "wallet password" note
   */
  public static JLabel newWalletPasswordNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.WALLET_PASSWORD_NOTE_1,
      MessageKey.WALLET_PASSWORD_NOTE_2,
      MessageKey.WALLET_PASSWORD_NOTE_3
    }, new Object[][]{});

  }

  /**
   * @return A new "debugger warning" note
   */
  public static JLabel newDebuggerWarningNote() {

    JLabel label = newNoteLabel(new CoreMessageKey[]{
      CoreMessageKey.DEBUGGER_ATTACHED,
      CoreMessageKey.SECURITY_ADVICE
    }, new Object[][]{});

    // Allow for danger theme
    label.setForeground(Themes.currentTheme.dangerAlertText());

    return label;

  }

  /**
   * @return A new "language change" note
   */
  public static JLabel newLanguageChangeNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.LANGUAGE_CHANGE_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "theme change" note
   */
  public static JLabel newThemeChangeNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.THEME_CHANGE_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "sound change" note
   */
  public static JLabel newSoundChangeNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.SOUND_CHANGE_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "seed warning" note
   */
  public static JLabel newSeedWarningNote() {

    JLabel label = newNoteLabel(new MessageKey[]{
      MessageKey.SEED_WARNING_NOTE_1,
      MessageKey.SEED_WARNING_NOTE_2,
      MessageKey.SEED_WARNING_NOTE_3,
      MessageKey.SEED_WARNING_NOTE_4,
      MessageKey.SEED_WARNING_NOTE_5,
    }, new Object[][]{});

    // Allow for danger theme
    label.setForeground(Themes.currentTheme.dangerAlertText());

    return label;

  }

  /**
   * @return A new "confirm seed phrase" note
   */
  public static JLabel newConfirmSeedPhraseNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.CONFIRM_SEED_PHRASE_NOTE_1,
      MessageKey.CONFIRM_SEED_PHRASE_NOTE_2,
      MessageKey.CONFIRM_SEED_PHRASE_NOTE_3,
      MessageKey.CONFIRM_SEED_PHRASE_NOTE_4
    }, new Object[][]{});
  }

  /**
   * @return A new "restore from seed phrase" note
   */
  public static JLabel newRestoreFromSeedPhraseNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.RESTORE_SEED_PHRASE_NOTE_1,
      MessageKey.RESTORE_SEED_PHRASE_NOTE_2,
      MessageKey.RESTORE_SEED_PHRASE_NOTE_3
    }, new Object[][]{});
  }

  /**
   * @return A new "restore from timestamp" note
   */
  public static JLabel newRestoreFromTimestampNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.RESTORE_TIMESTAMP_NOTE_1,
      MessageKey.RESTORE_TIMESTAMP_NOTE_2,
      MessageKey.RESTORE_TIMESTAMP_NOTE_3,
      MessageKey.RESTORE_TIMESTAMP_NOTE_4,
    }, new Object[][]{});
  }

  /**
   * @return A new "restore method" note
   */
  public static JLabel newRestoreSelectMethodNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.RESTORE_METHOD_NOTE_1,
      MessageKey.RESTORE_METHOD_NOTE_2,
      MessageKey.RESTORE_METHOD_NOTE_3,
      MessageKey.RESTORE_METHOD_NOTE_4
    }, new Object[][]{});
  }

  /**
   * @return A new "select backup location" note (create wizard)
   */
  public static JLabel newSelectBackupLocationNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.SELECT_BACKUP_LOCATION_NOTE_1,
      MessageKey.SELECT_BACKUP_LOCATION_NOTE_2,
      MessageKey.SELECT_BACKUP_LOCATION_NOTE_3,
      MessageKey.SELECT_BACKUP_LOCATION_NOTE_4,
    }, new Object[][]{});

  }

  /**
    * @return A new "export payments location" status label
    */
   public static JLabel newSelectExportPaymentsLocationNote() {

     return newNoteLabel(new MessageKey[]{
             MessageKey.SELECT_EXPORT_PAYMENTS_LOCATION_NOTE_1,
             MessageKey.SELECT_EXPORT_PAYMENTS_LOCATION_NOTE_2,
             MessageKey.SELECT_EXPORT_PAYMENTS_LOCATION_NOTE_3,
             MessageKey.SELECT_EXPORT_PAYMENTS_LOCATION_NOTE_4,
     }, new Object[][]{});
   }
  /**
   * @return A new "restore from backup" note
   */
  public static JLabel newRestoreFromBackupNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.RESTORE_BACKUP_NOTE_1,
      MessageKey.RESTORE_BACKUP_NOTE_2,
      MessageKey.RESTORE_BACKUP_NOTE_3
    }, new Object[][]{});
  }

  /**
   * @return A new "select backup" note (restore wizard)
   */
  public static JLabel newSelectBackupNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.SELECT_BACKUP_NOTE_1,
      MessageKey.SELECT_BACKUP_NOTE_2
    }, new Object[][]{});

  }

  /**
   * @return A new "password" note (password wizard)
   */
  public static JLabel newPasswordNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.PASSWORD_NOTE_1,
      MessageKey.PASSWORD_NOTE_2
    }, new Object[][]{});

  }

  /**
   * @return A new "Bitcoin settings" note
   */
  public static JLabel newBitcoinSettingsNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.BITCOIN_SETTINGS_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "exchange settings" note
   */
  public static JLabel newExchangeSettingsNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.EXCHANGE_SETTINGS_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "data entered" note
   */
  public static JLabel newDataEnteredNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.DATA_ENTERED_NOTE_1,
      MessageKey.DATA_ENTERED_NOTE_2
    }, new Object[][]{});

  }

}
