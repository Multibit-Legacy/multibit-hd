package org.multibit.hd.ui.views.components.display_amount;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.core.utils.CurrencyUtils;
import org.multibit.hd.ui.i18n.Formats;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;

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
  private JLabel leadingSymbolLabel;
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
      "[][][][][][]", // Columns
      "[]" // Rows
    ));

    // Create the balance labels (normal size)
    JLabel[] balanceLabels = Labels.newBalanceLabels(getModel().get().getStyle());
    leadingSymbolLabel = balanceLabels[0];
    primaryBalanceLabel = balanceLabels[1];
    secondaryBalanceLabel = balanceLabels[2];
    trailingSymbolLabel = balanceLabels[3];
    exchangeLabel = balanceLabels[4];

    // Determine how to add them back into the panel
    if (Languages.isLeftToRight()) {
      panel.add(leadingSymbolLabel, "left,shrink,gap 0,baseline");
      panel.add(primaryBalanceLabel, "left,shrink,baseline");
      panel.add(secondaryBalanceLabel, "left,shrink,gap 0");
      panel.add(trailingSymbolLabel, "left,shrink,gap 0");
      panel.add(exchangeLabel, "left,shrink,gap 0");
      panel.add(new JLabel(), "push,wrap"); // Provides a flexible column
    } else {

      panel.add(exchangeLabel, "right,shrink,gap 0");
      panel.add(leadingSymbolLabel, "right,shrink,gap 0");
      panel.add(primaryBalanceLabel, "right,shrink,baseline");
      panel.add(secondaryBalanceLabel, "right,shrink,gap 0");
      panel.add(trailingSymbolLabel, "right,shrink,gap 0");
      panel.add(new JLabel(), "push,wrap"); // Provides a flexible column
    }

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    // No focus required
  }

  /**
   * Updates the view to reflect the current Bitcoin and local amounts
   */
  public void updateView() {

    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration();

    // Display using the symbolic amount
    String[] bitcoinDisplay = Formats.formatSatoshisAsSymbolic(getModel().get().getSatoshis());

    if (i18nConfiguration.isCurrencySymbolLeading()) {
      handleLeadingSymbol();
    } else {
      handleTrailingSymbol();
    }

    primaryBalanceLabel.setText(bitcoinDisplay[0]);
    secondaryBalanceLabel.setText(bitcoinDisplay[1]);

    String localSymbol = CurrencyUtils.currentSymbol();

    if (getModel().get().isLocalAmountVisible()) {
      String localDisplay = Formats.formatLocalAmount(getModel().get().getLocalAmount());
      if (getModel().get().getRateProvider().isPresent()) {
        // Have a provider
        exchangeLabel.setText(
          Languages.safeText(
            MessageKey.EXCHANGE_FIAT_RATE_WITH_PROVIDER,
            " ~ " + localSymbol,
            localDisplay,
            getModel().get().getRateProvider().get()
          ));
      } else {
        // No provider
        exchangeLabel.setText(
          Languages.safeText(
            MessageKey.EXCHANGE_FIAT_RATE,
            " ~ " + localSymbol,
            localDisplay
          ));

      }
      exchangeLabel.setVisible(true);
    } else {
      exchangeLabel.setVisible(false);
    }
  }


  /**
   * <p>Place currency symbol before the number</p>
   */
  private void handleLeadingSymbol() {

    // Add the symbol to the leading position
    LabelDecorator.applyBitcoinSymbolLabel(leadingSymbolLabel);

    // Remove it from the trailing position
    AwesomeDecorator.removeIcon(trailingSymbolLabel);
    trailingSymbolLabel.setText("");

  }

  /**
   * <p>Place currency symbol after the number</p>
   */
  private void handleTrailingSymbol() {

    // Add the symbol to the trailing position
    LabelDecorator.applyBitcoinSymbolLabel(trailingSymbolLabel);

    // Remove it from the leading position
    AwesomeDecorator.removeIcon(leadingSymbolLabel);
    leadingSymbolLabel.setText("");

  }

}