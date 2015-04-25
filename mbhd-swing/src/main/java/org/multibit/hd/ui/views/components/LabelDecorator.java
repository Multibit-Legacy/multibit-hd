package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentSessionStatus;
import org.multibit.hd.core.dto.PaymentStatus;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.HtmlUtils;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Decorator to provide the following to UI:</p>
 * <ul>
 * <li>Apply symbols and iconography to labels without affecting their references</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class LabelDecorator {

  private static final Logger log = LoggerFactory.getLogger(LabelDecorator.class);

  /**
   * Utilities have no public constructor
   */
  private LabelDecorator() {
  }

  /**
   * <p>Apply the configured local currency symbol to the label</p>
   */
  public static void applyLocalCurrencySymbol(JLabel label) {

    label.setText(Configurations.currentConfiguration.getBitcoin().getLocalCurrencySymbol());

    Font font = label.getFont().deriveFont(Font.BOLD, (float) MultiBitUI.NORMAL_ICON_SIZE);
    label.setFont(font);

  }

  /**
   * <p>Apply the given Bitcoin symbol text(+icon) to the label using the current Bitcoin configuration</p>
   *
   * @param label The label to apply the symbol to
   */
  public static void applyBitcoinSymbolLabel(JLabel label) {
    applyBitcoinSymbolLabel(
      label,
      Configurations.currentConfiguration.getBitcoin(),
      "");
  }

  /**
   * <p>Apply the given Bitcoin symbol text(+icon) to the label. Removes any existing icon, including Bitcoin if symbol requires it.</p>
   *
   * @param label                The label to apply the symbol to
   * @param bitcoinConfiguration The Bitcoin configuration to use
   * @param leadingText          The text leading the Bitcoin symbol (e.g. "Amount" or "" )</p>
   */
  public static void applyBitcoinSymbolLabel(JLabel label, BitcoinConfiguration bitcoinConfiguration, String leadingText) {

    BitcoinSymbol symbol = BitcoinSymbol.of(bitcoinConfiguration.getBitcoinSymbol());

    int fontSize = label.getFont().getSize();

    if (!Strings.isNullOrEmpty(leadingText)) {
      leadingText = leadingText + " ";
    }

    int horizontalTextPosition = Languages.isLeftToRight() ? SwingConstants.LEADING : SwingConstants.TRAILING;

    // Due to NIST standards for SI units the text always leads the icon (RTL is ignored)
    switch (symbol) {
      case ICON:
        label.setText(leadingText);
        label.setIconTextGap(0);
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, label, false, fontSize);
        label.setHorizontalTextPosition(horizontalTextPosition);
        break;
      case MICON:
        label.setText(leadingText + symbol.getSymbol());
        label.setIconTextGap(-2);
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, label, false, fontSize);
        label.setHorizontalTextPosition(horizontalTextPosition);
        break;
      case UICON:
        label.setText(leadingText + symbol.getSymbol());
        label.setIconTextGap(-2);
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, label, false, fontSize);
        label.setHorizontalTextPosition(horizontalTextPosition);
        break;
      default:
        label.setText(leadingText + symbol.getSymbol());
        AwesomeDecorator.removeIcon(label);
    }
  }

  /**
   * Apply the payment status icon and color to a label
   *
   * @param paymentStatus The payment status to derive the status icon and color from
   * @param label         The label to apply the icon and color to
   * @param isCoinbase    True if the transaction is a coinbase
   * @param iconSize      THe size of the icon to use, typically MultiBitUI.SMALL_ICON_SIZE
   */
  public static void applyPaymentStatusIconAndColor(PaymentStatus paymentStatus, JLabel label, boolean isCoinbase, int iconSize) {

    applyPaymentStatusIcon(paymentStatus, label, isCoinbase, iconSize);

    switch (paymentStatus.getStatus()) {
      case RED:
        label.setForeground(Themes.currentTheme.statusRed());
        break;
      case AMBER:
        label.setForeground(Themes.currentTheme.text());
        break;
      case GREEN:
        label.setForeground(Themes.currentTheme.statusGreen());
        break;
      case PINK:
        label.setForeground(Themes.currentTheme.pendingAlertBackground().darker());
        break;
      default:
        // Unknown status
        throw new IllegalStateException("Unknown status " + paymentStatus.getStatus());
    }

  }

  /**
   * Apply the payment status icon to a label
   *
   * @param paymentStatus The payment status to derive the status icon and color from
   * @param label         The label to apply the icon and color to
   * @param isCoinbase    True if the transaction is a coinbase
   * @param iconSize      THe size of the icon to use, typically MultiBitUI.SMALL_ICON_SIZE
   */
  public static void applyPaymentStatusIcon(PaymentStatus paymentStatus, JLabel label, boolean isCoinbase, int iconSize) {

    switch (paymentStatus.getStatus()) {
      case RED:
        AwesomeDecorator.bindIcon(AwesomeIcon.TIMES, label, true, iconSize);
        break;
      case AMBER:
        AwesomeDecorator.bindIcon(AwesomeIcon.EXCHANGE, label, true, iconSize);
        break;
      case GREEN:
        int depth = paymentStatus.getDepth();
        label.setIcon(Images.newConfirmationIcon(depth, isCoinbase, iconSize));
        break;
      case PINK:
        AwesomeDecorator.bindIcon(AwesomeIcon.FILE_TEXT, label, true, iconSize);
        break;
      default:
        // Unknown status
        throw new IllegalStateException("Unknown status " + paymentStatus.getStatus());
    }

  }

  /**
   * Apply the payment status icon to a label
   *
   * @param paymentSessionStatus The payment session status to derive the status icon and color from
   * @param label                The label to apply the icon and color to
   * @param messageKey           The message key for the text
   * @param iconSize             THe size of the icon to use, typically MultiBitUI.SMALL_ICON_SIZE
   */
  public static void applyPaymentSessionStatusIcon(PaymentSessionStatus paymentSessionStatus, JLabel label, MessageKey messageKey, int iconSize) {

    label.setText(Languages.safeText(messageKey));

    switch (paymentSessionStatus) {
      case TRUSTED:
        AwesomeDecorator.bindIcon(AwesomeIcon.CHECK, label, true, iconSize);
        break;
      case UNTRUSTED:
        AwesomeDecorator.bindIcon(AwesomeIcon.WARNING, label, true, iconSize);
        break;
      case DOWN:
      case ERROR:
        AwesomeDecorator.bindIcon(AwesomeIcon.TIMES, label, true, iconSize);
        break;
      default:
        // Unknown status
        throw new IllegalStateException("Unknown status " + paymentSessionStatus);
    }

  }

  /**
   * Apply the hardware wallet report message key and status to a label resulting in a check mark or cross
   * and the appropriate message
   *
   * @param label               The label to decorate
   * @param reportMessageKey    The report message key (if not present then label is not visible)
   * @param reportMessageStatus The status (true then label has a check mark otherwise a cross)
   */
  public static void applyReportMessage(JLabel label, Optional<MessageKey> reportMessageKey, boolean reportMessageStatus) {
    if (label == null) {
      // Do nothing
      log.debug("No label to attach report label to");
      return;
    }
    if (reportMessageKey.isPresent()) {
      label.setText(Languages.safeText(reportMessageKey.get()));
      AccessibilityDecorator.apply(
        label,
        reportMessageKey.get()
      );
      applyStatusLabel(
        label,
        Optional.of(reportMessageStatus)
      );

      label.setVisible(true);
    } else {
      label.setVisible(false);
    }


  }

  /**
   * <p>Decorate a label with HTML-wrapped text respecting LTR/RTL to ensure line breaks occur predictably</p>
   *
   * @param label The label to decorate
   * @param value The text to show (will be wrapped in HTML)
   */
  public static void applyWrappingLabel(JLabel label, String value) {

    Preconditions.checkNotNull(value, "'value' must be present");

    String htmlText = HtmlUtils.localiseWithLineBreaks(value.split("\n"));

    label.setText(htmlText);

  }

  /**
   * @param statusLabel The status label to decorate
   * @param status      True for check, false for cross, absent for nothing (useful for initial message)
   */
  public static void applyStatusLabel(JLabel statusLabel, Optional<Boolean> status) {

    if (status.isPresent()) {
      if (status.get()) {
        AwesomeDecorator.bindIcon(AwesomeIcon.CHECK, statusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
      } else {
        AwesomeDecorator.bindIcon(AwesomeIcon.TIMES, statusLabel, true, MultiBitUI.NORMAL_ICON_SIZE);
      }
    }
  }
}
