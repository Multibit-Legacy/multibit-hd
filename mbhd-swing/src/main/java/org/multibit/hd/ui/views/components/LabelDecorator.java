package org.multibit.hd.ui.views.components;

import com.google.common.base.Strings;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Decorator to provide the following to UI:</p>
 * <ul>
 * <li>Apply symbols and iconography to labels without affecting their references</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class LabelDecorator {

  /**
   * Utilities have no public constructor
   */
  private LabelDecorator() {
  }

  /**
   * <p>Apply the configured local currency symbol to the label</p>
   */
  public static void applyLocalCurrencySymbol(JLabel label) {

    label.setText(Configurations.currentConfiguration.getBitcoinConfiguration().getLocalCurrencySymbol());

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
      Configurations.currentConfiguration.getBitcoinConfiguration(),
      "");
  }

  /**
   * <p>Apply the given Bitcoin symbol text(+icon) to the label. Removes any existing icon, including Bitcoin if symbol requires it.</p>
   *
   * @param label                The label to apply the symbol to
   * @param bitcoinConfiguration The Bitcoin configuration to use
   * @param leadingText           The text leading the Bitcoin symbol (e.g. "Amount" or "" )</p>
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
}
