package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.core.utils.CurrencyUtils;
import org.multibit.hd.ui.MultiBitUI;
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
   * <p>Apply the current local currency symbol to the label</p>
   */
  public static void applyLocalCurrencySymbol(JLabel label) {

    label.setText(CurrencyUtils.currentSymbol());

    Font font = label.getFont().deriveFont(Font.BOLD, (float) MultiBitUI.NORMAL_ICON_SIZE);
    label.setFont(font);

  }

  /**
   * <p>Apply the current Bitcoin symbol text(+icon) to the label</p>
   */
  public static void applyBitcoinSymbolLabel(JLabel label) {

    BitcoinSymbol symbol = BitcoinSymbol.of(Configurations.currentConfiguration.getBitcoinConfiguration().getBitcoinSymbol());

    int fontSize = label.getFont().getSize();

    switch (symbol) {
      case ICON:
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, label, false, fontSize);
        label.setIconTextGap(0);
        break;
      case MICON:
        label.setText("m");
        label.setIconTextGap(0);
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, label, false, fontSize);
        break;
      case UICON:
        label.setText("\u00B5");
        label.setIconTextGap(-2);
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, label, false, fontSize);
        break;
      default:
        label.setText(symbol.getSymbol());
    }

  }

}
