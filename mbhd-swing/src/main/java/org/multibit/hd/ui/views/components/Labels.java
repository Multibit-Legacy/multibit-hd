package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.api.Recipient;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.core.utils.CurrencyUtils;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.utils.HtmlUtils;
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

  public static final float BALANCE_HEADER_LARGE_FONT_SIZE = 42.0f;
  public static final float BALANCE_HEADER_NORMAL_FONT_SIZE = 28.0f;

  public static final float BALANCE_TRANSACTION_LARGE_FONT_SIZE = 18.0f;
  public static final float BALANCE_TRANSACTION_NORMAL_FONT_SIZE = 14.0f;

  public static final float BALANCE_FEE_LARGE_FONT_SIZE = 14.0f;
  public static final float BALANCE_FEE_NORMAL_FONT_SIZE = 12.0f;

  public static final float PANEL_CLOSE_FONT_SIZE = 28.0f;

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
   * @return A new blank label with default styling
   */
  public static JLabel newBlankLabel() {
    return new JLabel("");
  }

  /**
   * @param key The message key
   *
   * @return A new label with appropriate font and theme
   */
  public static JLabel newTitleLabel(MessageKey key) {

    JLabel label = newLabel(key);

    // Font
    Font font = label.getFont().deriveFont(BALANCE_HEADER_LARGE_FONT_SIZE);
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
  public static JLabel newNoteLabel(MessageKey[] keys, Object[][] values) {

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
  public static JLabel newStatusLabel(MessageKey key, Object[] values, boolean status) {
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
      label = Labels.newLabel(key.get(), values);
    } else {
      label = new JLabel();
    }

    decorateStatusLabel(label, status);

    return label;
  }

  public static void decorateStatusLabel(JLabel statusLabel, Optional<Boolean> status) {
    if (status.isPresent()) {
      if (status.get()) {
        AwesomeDecorator.bindIcon(AwesomeIcon.CHECK, statusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
      } else {
        AwesomeDecorator.bindIcon(AwesomeIcon.TIMES, statusLabel, true, AwesomeDecorator.NORMAL_ICON_SIZE);
      }
    } else {
      // No icon on the label
    }
  }

  /**
   * @param image The optional image
   *
   * @return A new label with the image or a placeholder if not present
   */
  public static JLabel newImageLabel(Optional<BufferedImage> image) {

    if (image.isPresent()) {
      return new JLabel(new ImageIcon(image.get()));
    }

    // Fall back to a default image
    JLabel label = new JLabel();
    AwesomeDecorator.applyIcon(AwesomeIcon.USER, label, true, AwesomeDecorator.LARGE_ICON_SIZE);
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
    AwesomeDecorator.applyIcon(AwesomeIcon.USER, label, true, AwesomeDecorator.LARGE_ICON_SIZE);
    return label;

  }

  /**
   * @param status True if the status is "good"
   *
   * @return A new "verification" status label
   */
  public static JLabel newVerificationStatus(boolean status) {

    return newStatusLabel(MessageKey.VERIFICATION_STATUS, null, status);
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
   * @param status True if the status is "good"
   *
   * @return A new "exchange rate status" message
   */
  public static JLabel newExchangeRateStatus(boolean status) {
    if (status) {
      return newStatusLabel(MessageKey.EXCHANGE_RATE_STATUS_OK, null, true);
    } else {
      return newStatusLabel(MessageKey.EXCHANGE_RATE_STATUS_WARN, null, false);
    }
  }

  /**
   * @return A new "Select language" label
   */
  public static JLabel newSelectLanguageLabel() {

    return new JLabel(Languages.safeText(MessageKey.DISPLAY_LANGUAGE));
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

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, panelCloseLabel, true, 16);
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

    JLabel primaryBalanceLabel = new JLabel("0.00");
    JLabel secondaryBalanceLabel = new JLabel("");
    JLabel trailingSymbolLabel = new JLabel("");
    JLabel exchangeLabel = new JLabel("");

    // Font
    final Font largeFont;
    final Font normalFont;

    switch (style) {
      case HEADER:
        largeFont = primaryBalanceLabel.getFont().deriveFont(BALANCE_HEADER_LARGE_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(BALANCE_HEADER_NORMAL_FONT_SIZE);
        break;
      case TRANSACTION_DETAIL_AMOUNT:
        largeFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, BALANCE_TRANSACTION_LARGE_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, BALANCE_TRANSACTION_NORMAL_FONT_SIZE);
        break;
      case FEE_AMOUNT:
        largeFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, BALANCE_FEE_NORMAL_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, BALANCE_FEE_NORMAL_FONT_SIZE);
        break;
      default:
        throw new IllegalStateException("Unknown style:" + style.name());
    }

    primaryBalanceLabel.setFont(largeFont);
    primaryBalanceLabel.setForeground(Color.RED);
    secondaryBalanceLabel.setFont(normalFont);
    trailingSymbolLabel.setFont(largeFont);
    exchangeLabel.setFont(normalFont);

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
   * @return A new "Amount" label
   */
  public static JLabel newAmount() {
    return newLabel(MessageKey.AMOUNT);
  }

  /**
   * @return A new "Bitcoin currency symbol" based on the current configuration
   */
  public static JLabel newBitcoinCurrencySymbol() {

    BitcoinSymbol symbol = BitcoinSymbol.of(
      Configurations
        .currentConfiguration
        .getBitcoinConfiguration()
        .getBitcoinSymbol()
    );

    JLabel label = new JLabel();
    if (BitcoinSymbol.ICON.equals(symbol)) {
      AwesomeDecorator.applyIcon(
        AwesomeIcon.BITCOIN,
        label,
        true,
        AwesomeDecorator.NORMAL_ICON_SIZE
      );
    } else {
      label.setText(symbol.getSymbol());
    }

    return label;

  }

  /**
   * @return A new "local currency symbol" based on the current configuration
   */
  public static JLabel newLocalCurrencySymbol() {

    JLabel label = new JLabel(CurrencyUtils.currentSymbol());

    Font font = label.getFont().deriveFont(Font.BOLD, (float) AwesomeDecorator.NORMAL_ICON_SIZE);
    label.setFont(font);

    return label;
  }

  /**
   * @return A new "approximately" symbol
   */
  public static JLabel newApproximately() {

    JLabel label = newLabel(MessageKey.APPROXIMATELY);

    Font font = label.getFont().deriveFont(Font.BOLD, (float) AwesomeDecorator.NORMAL_ICON_SIZE);
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

    return newLabel(MessageKey.CONFIRM_PASSWORD);
  }

  /**
   * @return The current exchange name from the configuration
   */
  public static JLabel newCurrentExchangeName() {

    String exchangeName = Configurations
      .currentConfiguration
      .getBitcoinConfiguration()
      .getExchangeName();

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
   * @return A new "notes" message
   */
  public static JLabel newNotes() {
    return newLabel(MessageKey.NOTES);
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
   * @return A new "seed warning" note
   */
  public static JLabel newSeedWarningNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.SEED_WARNING_NOTE_1,
      MessageKey.SEED_WARNING_NOTE_2,
      MessageKey.SEED_WARNING_NOTE_3,
      MessageKey.SEED_WARNING_NOTE_4,
    }, new Object[][]{});
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
      MessageKey.RESTORE_TIMESTAMP_NOTE_3
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
}
