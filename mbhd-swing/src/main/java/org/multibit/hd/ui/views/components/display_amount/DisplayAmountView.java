package org.multibit.hd.ui.views.components.display_amount;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.i18n.Formats;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.AbstractComponentView;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a seed phrase display</li>
 * <li>Support for refresh and reveal operations</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplayAmountView extends AbstractComponentView<DisplayAmountModel> {

  // View components
  private JLabel primaryBalanceLabel;
  private JLabel secondaryBalanceLabel;
  private JLabel trailingSymbolLabel;
  private JLabel exchangeLabel;

  /**
   * @param model The model backing this view
   */
  public DisplayAmountView(DisplayAmountModel model) {
    super(model);
  }

  @Override
  public void updateModelFromView() {

  }

  @Override
  public JPanel newComponentPanel() {

    // Create the balance panel - forcing a LTR layout to ensure correct placement of labels
    panel = Panels.newPanel(new MigLayout(
      "fill,ltr,insets 0", // Layout
      "[][][][][]", // Columns
      "[]10[shrink]" // Rows
    ));

    // Create the balance labels (normal size)
    JLabel[] balanceLabels = Labels.newBalanceLabels(getModel().get().getStyle());
    primaryBalanceLabel = balanceLabels[0];
    secondaryBalanceLabel = balanceLabels[1];
    trailingSymbolLabel = balanceLabels[2];
    exchangeLabel = balanceLabels[3];

    // Determine how to add them back into the panel
    if (Languages.isLeftToRight()) {
      panel.add(primaryBalanceLabel, "left,shrink,baseline");
      panel.add(secondaryBalanceLabel, "left,shrink,gap 0");
      panel.add(trailingSymbolLabel, "left,shrink,gap 0");
      panel.add(exchangeLabel, "left,shrink,gap 0");
      panel.add(new JLabel(), "push,wrap"); // Provides a flexible column
    } else {

      panel.add(exchangeLabel, "right,shrink,gap 0");
      panel.add(primaryBalanceLabel, "right,shrink,baseline");
      panel.add(secondaryBalanceLabel, "right,shrink,gap 0");
      panel.add(trailingSymbolLabel, "right,shrink,gap 0");
      panel.add(new JLabel(), "push,wrap"); // Provides a flexible column
    }

    return panel;

  }

  /**
   * Updates the view to reflect the current Bitcoin and local amounts
   */
  public void updateView() {

    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();
    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration();

    // Display using the symbolic amount
    String[] bitcoinDisplay = Formats.formatSatoshisAsSymbolic(getModel().get().getSatoshis());

    BitcoinSymbol symbol = BitcoinSymbol.of(bitcoinConfiguration.getBitcoinSymbol());

    if (i18nConfiguration.isCurrencySymbolLeading()) {
      handleLeadingSymbol(bitcoinDisplay, symbol);
    } else {
      handleTrailingSymbol(symbol);
    }

    primaryBalanceLabel.setText(bitcoinDisplay[0]);
    secondaryBalanceLabel.setText(bitcoinDisplay[1]);

    if (getModel().get().isLocalAmountVisible()) {
      String localDisplay = Formats.formatLocalAmount(getModel().get().getLocalAmount());
      exchangeLabel.setText(
        Languages.safeText(
          MessageKey.EXCHANGE_FIAT_RATE,
          "~ $",
          localDisplay
        ));
      exchangeLabel.setVisible(true);
    } else {
      exchangeLabel.setVisible(false);
    }
  }


  /**
   * <p>Place currency symbol before the number</p>
   *
   * @param symbol The symbol to use
   */
  private void handleLeadingSymbol(String[] balance, BitcoinSymbol symbol) {

    // Placement is primary, secondary, trailing, exchange (reading LTR)

    if (BitcoinSymbol.ICON.equals(symbol)) {

      // Icon leads primary balance but decorator will automatically swap which is undesired
      if (Languages.isLeftToRight()) {
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, primaryBalanceLabel, true, getLargeFontSize());
      } else {
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, primaryBalanceLabel, false, getLargeFontSize());
      }
      AwesomeDecorator.removeIcon(trailingSymbolLabel);
      trailingSymbolLabel.setText("");

    } else {

      // Symbol leads primary balance
      balance[0] = symbol.getSymbol() + " " + balance[0];
      AwesomeDecorator.removeIcon(primaryBalanceLabel);
      AwesomeDecorator.removeIcon(trailingSymbolLabel);

    }

  }

  /**
   * <p>Place currency symbol after the number</p>
   *
   * @param symbol The symbol to use
   */
  private void handleTrailingSymbol(BitcoinSymbol symbol) {

    if (BitcoinSymbol.ICON.equals(symbol)) {

      // Icon trails secondary balance
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, trailingSymbolLabel, true, getNormalFontSize());
      AwesomeDecorator.removeIcon(primaryBalanceLabel);
      trailingSymbolLabel.setText("");

    } else {

      // Symbol trails secondary balance
      trailingSymbolLabel.setText(symbol.getSymbol());
      AwesomeDecorator.removeIcon(primaryBalanceLabel);
      AwesomeDecorator.removeIcon(trailingSymbolLabel);

    }

  }

  /**
   * @return The size of the normal font for the given style
   */
  private int getNormalFontSize() {

    final int size;
    switch (getModel().get().getStyle()) {
      case HEADER:
        size = (int) Labels.BALANCE_HEADER_NORMAL_FONT_SIZE;
        break;
      case TRANSACTION_DETAIL_AMOUNT:
        size = (int) Labels.BALANCE_TRANSACTION_NORMAL_FONT_SIZE;
        break;
      case FEE_AMOUNT:
        size = (int) Labels.BALANCE_FEE_NORMAL_FONT_SIZE;
        break;
      default:
        throw new IllegalStateException("Style: " + getModel().get().getStyle().name() + " is unknown");
    }

    return size;

  }

  /**
   * @return The size of the large font for the given style
   */
  private int getLargeFontSize() {

    final int size;
    switch (getModel().get().getStyle()) {
      case HEADER:
        size = (int) Labels.BALANCE_HEADER_LARGE_FONT_SIZE;
        break;
      case TRANSACTION_DETAIL_AMOUNT:
        size = (int) Labels.BALANCE_TRANSACTION_LARGE_FONT_SIZE;
        break;
      case FEE_AMOUNT:
        size = (int) Labels.BALANCE_FEE_LARGE_FONT_SIZE;
        break;
      default:
        throw new IllegalStateException("Style: " + getModel().get().getStyle().name() + " is unknown");
    }

    return size;

  }
}