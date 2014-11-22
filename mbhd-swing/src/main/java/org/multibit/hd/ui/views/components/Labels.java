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
import org.multibit.hd.ui.views.fonts.TitleFontDecorator;
import org.multibit.hd.ui.views.themes.Themes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised buttons</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class Labels {

  /**
   * Utilities have no public constructor
   */
  private Labels() {
  }


  /**
   * @param key    The resource key for the language message text
   * @param values The data values for token replacement in the message text
   *
   * @return A new label with default styling
   */
  public static JLabel newLabel(MessageKey key, Object... values) {

    JLabel label = new JLabel(Languages.safeText(key, values));

    // Ensure it is accessible
    AccessibilityDecorator.apply(label, key);

    // Apply theme
    label.setForeground(Themes.currentTheme.text());

    return label;

  }

  /**
   * @param key    The resource key for the language message text
   * @param values The data values for token replacement in the message text
   *
   * @return A new label with default styling
   */
  public static JLabel newLabel(CoreMessageKey key, Object... values) {

    JLabel label = new JLabel(Languages.safeText(key, values));

    // Ensure it is accessible
    AccessibilityDecorator.apply(label, key);

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
   * @return A new label with appropriate font, theme and alignment for a wizard panel view title
   */
  public static JLabel newTitleLabel(MessageKey key) {

    String[] titleText = new String[]{Languages.safeText(key)};

    String htmlText = HtmlUtils.localiseWithCenteredLinedBreaks(titleText);

    JLabel label = new JLabel(htmlText);

    // Ensure it is accessible
    AccessibilityDecorator.apply(label, key);

    // Font
    TitleFontDecorator.apply(label, MultiBitUI.BALANCE_HEADER_LARGE_FONT_SIZE);

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

    // Ensure it is accessible
    AccessibilityDecorator.apply(label, keys[0]);

    // Theme
    label.setForeground(Themes.currentTheme.text());

    return label;

  }

  /**
   * <p>Create a new label with appropriate font/theme for a note. Interpret the contents of the text as Markdown for HTML translation.</p>
   *
   * @param key    The message key referencing simple HTML (standard wrapping/breaking elements like {@literal <html></html>} and {@literal <br/>} will be provided)
   * @param values The substitution values if applicable
   *
   * @return A new label with HTML formatting to correctly render the line break and contents
   */
  public static JLabel newNoteLabel(MessageKey key, Object[] values) {

    String line;
    if (values != null && values.length > 0) {
      // Substitution is required
      line = Languages.safeText(key, values);
    } else {
      // Key only
      line = Languages.safeText(key);
    }

    // Wrap in HTML to ensure LTR/RTL and line breaks are respected
    JLabel label = new JLabel(HtmlUtils.localiseWithLineBreaks(new String[] {line}));

    // Ensure it is accessible
    AccessibilityDecorator.apply(label, key);

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

    // Ensure it is accessible
    AccessibilityDecorator.apply(label, keys[0]);

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
      label = newLabel(key.get(), values);
    } else {
      label = newBlankLabel();
    }

    decorateStatusLabel(label, status);

    return label;
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
  public static JLabel newCoreStatusLabel(Optional<CoreMessageKey> key, Object[] values, Optional<Boolean> status) {

    JLabel label;

    if (key.isPresent()) {
      label = newLabel(key.get(), values);
    } else {
      label = newBlankLabel();
    }

    decorateStatusLabel(label, status);

    return label;
  }

  /**
   * <p>Decorate a label with HTML-wrapped text respecting LTR/RTL to ensure line breaks occur predictably</p>
   *
   * @param label The label to decorate
   * @param value The text to show (will be wrapped in HTML)
   */
  public static void decorateWrappingLabel(JLabel label, String value) {

    Preconditions.checkNotNull(value, "'value' must be present");

    String htmlText = HtmlUtils.localiseWithLineBreaks(value.split("\n"));

    label.setText(htmlText);

  }

  /**
   * @param statusLabel The status label to decorate
   * @param status      True for check, false for cross, absent for nothing (useful for initial message)
   */
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
   * <p>An "icon" label sets a label with an icon in the leading position. Useful for lists of notes.</p>
   *
   * @param icon   The icon to place in the leading position
   * @param key    The message key - if not present then empty text is put on the label
   * @param values The substitution values
   *
   * @return A new label with icon binding to allow the AwesomeDecorator to update it
   */
  public static JLabel newIconLabel(AwesomeIcon icon, Optional<MessageKey> key, Object[] values) {

    JLabel label;

    if (key.isPresent()) {
      label = newLabel(key.get(), values);
    } else {
      label = newBlankLabel();
    }

    AwesomeDecorator.bindIcon(icon, label, false, MultiBitUI.NORMAL_ICON_SIZE);

    return label;
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
   * @param panelName The panel name (used as the basis of the unique FEST name)
   * @param status    True if the status is "good"
   *
   * @return A new "verification" status label (confirms user has done something right)
   */
  public static JLabel newVerificationStatus(String panelName, boolean status) {

    JLabel label = newStatusLabel(MessageKey.VERIFICATION_STATUS, null, status);

    // There could be many verification labels on a single panel so provide a unique name
    // See AbstractFestUseCase for more details
    label.setName(panelName + "." + MessageKey.VERIFICATION_STATUS.getKey());

    return label;

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
   * @param status True if the address is acceptable (i.e. not mine)
   *
   * @return A new "address is mine" status label
   */
  public static JLabel newAddressIsMineStatusLabel(boolean status) {
    return newStatusLabel(MessageKey.ADDRESS_IS_MINE_STATUS, null, status);
  }

  /**
   * @param status True if the status is "good"
   *
   * @return A new "wallet credentials created" status label
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
   * @return A new "passwordChanged" status label
   */
  public static JLabel newPasswordChangedStatus() {
    return newStatusLabel(Optional.<MessageKey>absent(), new Object[]{}, Optional.<Boolean>absent());
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
   * @return A new "CA certs installed" status label
   */
  public static JLabel newCACertsInstalledStatus(boolean status) {
    return newStatusLabel(MessageKey.CACERTS_INSTALLED_STATUS, null, status);
  }

  /**
   * @return A new "wipe Trezor message" status label
   */
  public static JLabel newWipeTrezorLabel() {
    return newLabel(MessageKey.WIPE_TREZOR_MESSAGE);
  }

  /**
   * @param status True if the status is "good"
   *
   * @return A new "synchronizing" status label
   */
  public static JLabel newSynchronizingStatus(boolean status) {
    return newStatusLabel(MessageKey.SYNCHRONIZING_STATUS, null, status);
  }

  /**
   * @param color The spinner color
   * @param size  The size in pixels of the target component
   *
   * @return A new "spinner" label (indicates that something is happening asynchronously)
   */
  public static JLabel newSpinner(Color color, int size) {

    // The container label
    final JLabel label = newBlankLabel();

    final RotatingIcon rotatingIcon = new RotatingIcon(AwesomeDecorator.createIcon(
      AwesomeIcon.REFRESH,
      color,
      size
    ), label);

    label.setIcon(rotatingIcon);

    // Require a small border when placing in a central position
    label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

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
   * @return A new "show balance" label
   */
  public static JLabel newShowBalance() {

    return Labels.newLabel(MessageKey.SHOW_BALANCE);

  }

  /**
   * @return A new "select theme" label
   */
  public static JLabel newSelectTheme() {

    return Labels.newLabel(MessageKey.SELECT_THEME);

  }

  /**
   * @return A new "select decimal separator" label
   */
  public static JLabel newSelectDecimal() {

    return Labels.newLabel(MessageKey.SELECT_DECIMAL_SEPARATOR);
  }

  /**
   * @return A new "select grouping separator" label
   */
  public static JLabel newSelectGrouping() {

    return Labels.newLabel(MessageKey.SELECT_GROUPING_SEPARATOR);
  }

  /**
   * @return A new "select local currency symbol" label
   */
  public static JLabel newLocalSymbol() {

    return Labels.newLabel(MessageKey.SELECT_LOCAL_SYMBOL);
  }

  /**
   * @return A new "select local currency code" label
   */
  public static JLabel newLocalCurrency() {

    return Labels.newLabel(MessageKey.SELECT_LOCAL_CURRENCY);
  }

  /**
   * @return A new "enter access code" label (for API keys)
   */
  public static JLabel newApiKey() {

    JLabel label = Labels.newLabel(MessageKey.ENTER_ACCESS_CODE);
    label.setName("exchange_" + MessageKey.ENTER_ACCESS_CODE.getKey());
    return label;
  }

  /**
   * @return A new "select local Bitcoin symbol" label
   */
  public static JLabel newBitcoinSymbol() {

    return Labels.newLabel(MessageKey.SELECT_BITCOIN_SYMBOL);
  }

  /**
   * @return A new "select placement" label
   */
  public static JLabel newPlacement() {

    return Labels.newLabel(MessageKey.SELECT_PLACEMENT);
  }

  /**
   * @return A new "example" label
   */
  public static JLabel newExample() {

    return Labels.newLabel(MessageKey.EXAMPLE);
  }

  /**
   * @return A new "version" label
   */
  public static JLabel newVersion() {

    return Labels.newLabel(MessageKey.VERSION);
  }

  /**
   * @return A new "select exchange rate provider" label
   */
  public static JLabel newSelectExchangeRateProvider() {

    return Labels.newLabel(MessageKey.SELECT_EXCHANGE_RATE_PROVIDER);
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
   * @param style    The display style to use depending on the context
   * @param festName The FEST name to use when adding accessibility
   *
   * @return A new collection of labels that together form a balance display
   */
  public static JLabel[] newBalanceLabels(DisplayAmountStyle style, String festName) {

    Preconditions.checkNotNull(style, "'style' must be present");

    JLabel leadingBalanceLabel = newBlankLabel();
    JLabel primaryBalanceLabel = newBlankLabel();
    JLabel secondaryBalanceLabel = newBlankLabel();
    JLabel trailingSymbolLabel = newBlankLabel();
    JLabel exchangeLabel = newBlankLabel();

    // Add FEST information (accessibility is covered at the overall panel level)
    leadingBalanceLabel.setName(festName + ".leading_balance");
    primaryBalanceLabel.setName(festName + ".primary_balance");
    secondaryBalanceLabel.setName(festName + ".secondary_balance");
    trailingSymbolLabel.setName(festName + ".trailing_symbol");
    exchangeLabel.setName(festName + ".exchange");

    // Font
    final Font largeFont;
    final Font normalFont;

    final Color textColor;

    switch (style) {
      case HEADER:
        largeFont = primaryBalanceLabel.getFont().deriveFont(MultiBitUI.BALANCE_HEADER_LARGE_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(MultiBitUI.BALANCE_HEADER_NORMAL_FONT_SIZE);
        textColor = Themes.currentTheme.headerPanelText();
        break;
      case TRANSACTION_DETAIL_AMOUNT:
        largeFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, MultiBitUI.BALANCE_TRANSACTION_LARGE_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, MultiBitUI.BALANCE_TRANSACTION_NORMAL_FONT_SIZE);
        textColor = Themes.currentTheme.text();
        break;
      case FEE_AMOUNT:
        largeFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, MultiBitUI.BALANCE_FEE_NORMAL_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(Font.BOLD, MultiBitUI.BALANCE_FEE_NORMAL_FONT_SIZE);
        textColor = Themes.currentTheme.text();
        break;
      case PLAIN:
        largeFont = primaryBalanceLabel.getFont().deriveFont(Font.PLAIN, MultiBitUI.BALANCE_FEE_NORMAL_FONT_SIZE);
        normalFont = primaryBalanceLabel.getFont().deriveFont(Font.PLAIN, MultiBitUI.BALANCE_FEE_NORMAL_FONT_SIZE);
        textColor = Themes.currentTheme.text();
        break;
      default:
        throw new IllegalStateException("Unknown style:" + style.name());
    }

    leadingBalanceLabel.setFont(largeFont);
    leadingBalanceLabel.setForeground(textColor);

    primaryBalanceLabel.setFont(largeFont);
    primaryBalanceLabel.setForeground(textColor);

    secondaryBalanceLabel.setFont(normalFont);
    secondaryBalanceLabel.setForeground(textColor);

    trailingSymbolLabel.setFont(largeFont);
    trailingSymbolLabel.setForeground(textColor);

    exchangeLabel.setFont(normalFont);
    exchangeLabel.setForeground(textColor);

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
    return newLabel(MessageKey.LOCAL_AMOUNT);
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
   * @return A new "select wallet" label
   */
  public static JLabel newSelectWallet() {

    return newLabel(MessageKey.SELECT_WALLET);
  }

  /**
   * @return A new "Press Confirm on device" label
   */
  public static JLabel newPressConfirmOnDevice() {

    return newLabel(MessageKey.TREZOR_PRESS_CONFIRM_OPERATION);

  }

  /**
   * @return A new "enter current PIN"
   */
  public static JLabel newEnterCurrentPin() {

    return newLabel(MessageKey.ENTER_CURRENT_PIN);

  }

  /**
   * @return A new "enter new PIN"
   */
  public static JLabel newEnterNewPin() {

    return newLabel(MessageKey.ENTER_NEW_PIN);

  }

  /**
   * @return A new "confirm new PIN"
   */
  public static JLabel newConfirmNewPin() {

    return newLabel(MessageKey.CONFIRM_NEW_PIN);

  }

  /**
   * @return A new "enter PIN look at device"
   */
  public static JLabel newEnterPinLookAtDevice() {

    return newLabel(MessageKey.ENTER_PIN_LOOK_AT_DEVICE);

  }

  /**
   * @return A new "Enter credentials" label
   */
  public static JLabel newEnterPassword() {

    return newLabel(MessageKey.ENTER_PASSWORD);
  }

  /**
   * @return A new "Enter new credentials" label
   */
  public static JLabel newEnterNewPassword() {

    return newLabel(MessageKey.ENTER_NEW_PASSWORD);
  }

  /**
   * @return A new "Retype new credentials" label
   */
  public static JLabel newRetypeNewPassword() {

    return newLabel(MessageKey.RETYPE_NEW_PASSWORD);
  }

  /**
   * @return A new "Confirm credentials" label
   */
  public static JLabel newPasswordVerified() {

    return newLabel(MessageKey.PASSWORD_VERIFIED);
  }

  /**
   * @return A new "Password failed" label
   */
  public static JLabel newPasswordFailed() {

    return newLabel(MessageKey.PASSWORD_FAILED);
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
   * @return A new "transaction fee" message
   */
  public static JLabel newTransactionFee() {
    return newLabel(MessageKey.TRANSACTION_FEE);
  }

  /**
   * @return A new "transaction hash" label
   */
  public static JLabel newTransactionHash() {
    return newValueLabel(Languages.safeText(MessageKey.TRANSACTION_HASH));
  }

  /**
   * @return A new "size" label
   */
  public static JLabel newSize() {
    return newValueLabel(Languages.safeText(MessageKey.SIZE));
  }

  /**
   * @return A new "raw transaction" label
   */
  public static JLabel newRawTransaction() {
    return Labels.newValueLabel(Languages.safeText(MessageKey.RAW_TRANSACTION));
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
   * @return A new "developer fee" message
   */
  public static JLabel newDeveloperFee() {
    return newLabel(MessageKey.CLIENT_FEE);
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
   * @return A new "seed phrase" message
   */
  public static JLabel newSeedPhrase() {
    return newLabel(MessageKey.SEED_PHRASE);
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
  public static JLabel newQRCodeLabel() {
    return newLabel(MessageKey.QR_CODE_LABEL);
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
   * @return a new "select TOR" for lab settings
   */
  public static JLabel newSelectTor() {
    return newLabel(MessageKey.SELECT_TOR);
  }

  /**
   * @return a new "select Trezor" for lab settings
   */
  public static JLabel newSelectTrezor() {
    return newLabel(MessageKey.SELECT_TREZOR);
  }

  /**
   * @return a new "peer count" for verifying network
   */
  public static JLabel newPeerCount() {
    return newLabel(MessageKey.PEER_COUNT);
  }

  /**
   * @return a new "blocks left" for verifying network
   */
  public static JLabel newBlocksLeft() {
    return newLabel(MessageKey.BLOCKS_LEFT);
  }

  /**
   * @return A new "notes" label
   */
  public static JLabel newNotes() {
    return newLabel(MessageKey.PRIVATE_NOTES);
  }

  /**
   * @return A new "message" label
   */
  public static JLabel newMessage() {
    return newLabel(MessageKey.MESSAGE);
  }

  /**
   * @return A new "signature" label
   */
  public static JLabel newSignature() {
    return newLabel(MessageKey.SIGNATURE);
  }

  /**
   * @return A new "communicating with Trezor" label
   */
  public static JLabel newCommunicatingWithTrezor() {
    return newLabel(MessageKey.COMMUNICATING_WITH_TREZOR);
  }

  /**
   * @return A new "multi edit note" label
   */
  public static JLabel newMultiEditNote() {
    return newLabel(MessageKey.MULTI_EDIT_NOTE);
  }

  /**
   * @return a new Cloud backup location label
   */
  public static JLabel newCloudBackupLocation() {
    return newLabel(MessageKey.CLOUD_BACKUP_LOCATION);
  }

  /**
   * @return A new "welcome" note
   */
  public static JLabel newWelcomeNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.WELCOME_NOTE_1,
      MessageKey.WELCOME_NOTE_2,
      MessageKey.WELCOME_NOTE_3
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
   * @return A new "wallet credentials" note
   */
  public static JLabel newWalletPasswordNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.WALLET_PASSWORD_NOTE_1,
      MessageKey.WALLET_PASSWORD_NOTE_2,
      MessageKey.WALLET_PASSWORD_NOTE_3
    }, new Object[][]{});

  }

  /**
   * @return A new "press Confirm on Trezor" note
   */
  public static JLabel newPressConfirmOnTrezorNoteShort() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.TREZOR_PRESS_CONFIRM_OPERATION
    }, new Object[][]{});

  }

  /**
   * @return A new "language change" note
   */
  public static JLabel newBuyTrezorCommentNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.BUY_TREZOR_COMMENT
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
   * @return A new "lab change" note
   */
  public static JLabel newLabChangeNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.LAB_CHANGE_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "create wallet preparation" note
   */
  public static JLabel newCreateWalletPreparationNote() {

    JLabel label = newNoteLabel(new MessageKey[]{
      MessageKey.PREPARATION_NOTE_1,
      MessageKey.PREPARATION_NOTE_2,
      MessageKey.PREPARATION_NOTE_3,
      MessageKey.PREPARATION_NOTE_4,
      MessageKey.PREPARATION_NOTE_5,
    }, new Object[][]{});

    // Allow FEST to find this
    label.setName(MessageKey.PREPARATION_NOTE_1.getKey());

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
      MessageKey.CONFIRM_SEED_PHRASE_NOTE_3
    }, new Object[][]{});
  }

  /**
   * @return A new "restore from seed phrase" note
   */
  public static JLabel newRestoreFromSeedPhraseNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.RESTORE_FROM_SEED_PHRASE_NOTE_1,
      MessageKey.RESTORE_FROM_SEED_PHRASE_NOTE_2,
      MessageKey.RESTORE_FROM_SEED_PHRASE_NOTE_3
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
   * @return A new "password" note (credentials wizard)
   */
  public static JLabel newPasswordNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.PASSWORD_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "PIN introduction" note (credentials wizard)
   */
  public static JLabel newPinIntroductionNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.PIN_INTRODUCTION
    }, new Object[][]{});

  }

  /**
   * @return A new "select wallet" note (credentials wizard)
   */
  public static JLabel newSelectWalletNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.SELECT_WALLET_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "restore wallet" note (credentials wizard)
   */
  public static JLabel newRestoreWalletNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.RESTORE_WALLET_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "change credentials note 1" (change credentials wizard)
   */
  public static JLabel newChangePasswordNote1() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.CHANGE_PASSWORD_NOTE_1
    }, new Object[][]{});

  }

  /**
   * @return A new "change credentials note 2" (change credentials wizard)
   */
  public static JLabel newChangePasswordNote2() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.CHANGE_PASSWORD_NOTE_2
    }, new Object[][]{});

  }

  /**
   * @return A new "verify network" note
   */
  public static JLabel newVerifyNetworkNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.VERIFY_NETWORK_NOTE_1,
      MessageKey.VERIFY_NETWORK_NOTE_2
    }, new Object[][]{});

  }

  /**
   * @return A new "Units settings" note
   */
  public static JLabel newUnitsSettingsNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.UNITS_SETTINGS_NOTE_1
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

  /**
   * @return A new "verify message" note
   */
  public static JLabel newVerifyMessageNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.VERIFY_MESSAGE_NOTE_2
    }, new Object[][]{});

  }

  /**
   * @return A new "repair wallet" note
   */
  public static JLabel newRepairWalletNote() {

    return newNoteLabel(new MessageKey[]{
      MessageKey.REPAIR_WALLET_NOTE_1,
      MessageKey.REPAIR_WALLET_NOTE_2,
      MessageKey.REPAIR_WALLET_NOTE_3,
      MessageKey.REPAIR_WALLET_NOTE_4
    }, new Object[][]{});

  }

  /**
   * @param isCopyAvailable True if the additional "copy QR image" note should be included
   *
   * @return A new "QR popover" note
   */
  public static JLabel newQRCodePopoverNote(boolean isCopyAvailable) {

    if (isCopyAvailable) {
      return newNoteLabel(new MessageKey[]{
        MessageKey.QR_CODE_NOTE_1,
        MessageKey.QR_CODE_NOTE_2,
      }, new Object[][]{});
    }

    return newNoteLabel(new MessageKey[]{
      MessageKey.QR_CODE_NOTE_1,
    }, new Object[][]{});

  }
}
