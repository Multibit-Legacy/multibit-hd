package org.multibit.hd.ui.views.components.display_amount;

import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;

import javax.swing.*;
import java.math.BigInteger;
import java.util.Locale;

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
            "fillx,insets 0,hidemode 2,ltr", // Layout requires LTR
            "[]0[]5[]5[]5[]5[]", // Columns require careful padding for leading/trailing symbols
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
      panel.add(leadingSymbolLabel, "shrink,baseline");
      panel.add(primaryBalanceLabel, "shrink,baseline");
      panel.add(secondaryBalanceLabel, "shrink");
      panel.add(trailingSymbolLabel, "shrink");
      panel.add(exchangeLabel, "shrink");
      panel.add(Labels.newBlankLabel(), "push,wrap"); // Provides a flexible column
    } else {
      panel.add(Labels.newBlankLabel(), "push"); // Provides a flexible column
      panel.add(exchangeLabel, "shrink");
      panel.add(leadingSymbolLabel, "shrink");
      panel.add(primaryBalanceLabel, "shrink,baseline");
      panel.add(secondaryBalanceLabel, "shrink");
      panel.add(trailingSymbolLabel, "shrink,wrap");
    }

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    // No focus required
  }

  /**
   * <p>Updates the view to reflect the current Bitcoin and local amounts</p>
   *
   * @param configuration The Configuration to use
   */
  public void updateView(Configuration configuration) {

    Preconditions.checkNotNull(configuration, "'configuration' must be present");

    LanguageConfiguration languageConfiguration = configuration.getLanguageConfiguration();
    BitcoinConfiguration bitcoinConfiguration = configuration.getBitcoinConfiguration();
    BigInteger satoshis = getModel().get().getSatoshis();

    // Display using the symbolic amount
    String[] bitcoinDisplay = Formats.formatSatoshisAsSymbolic(satoshis, languageConfiguration, bitcoinConfiguration, getModel().get().isShowNegative());

    if (bitcoinConfiguration.isCurrencySymbolLeading()) {
      handleLeadingSymbol(bitcoinConfiguration);
      leadingSymbolLabel.setVisible(true);
      // Require a hard space to ensure leading/trailing symbols look right
      primaryBalanceLabel.setText("\u00a0" + bitcoinDisplay[0]);
    } else {
      handleTrailingSymbol(bitcoinConfiguration);
      leadingSymbolLabel.setVisible(false);
      primaryBalanceLabel.setText(bitcoinDisplay[0]);
    }

    secondaryBalanceLabel.setText(bitcoinDisplay[1]);

    Locale locale = languageConfiguration.getLocale();
    String localSymbol = bitcoinConfiguration.getLocalCurrencySymbol();

    if (getModel().get().isLocalAmountVisible()) {
      String localDisplay = Formats.formatLocalAmount(getModel().get().getLocalAmount(), locale, bitcoinConfiguration, getModel().get().isShowNegative());
      // Exchange label text is complex
      handleExchangeLabelText(bitcoinConfiguration, localSymbol, localDisplay);
      exchangeLabel.setVisible(true);
    } else {
      exchangeLabel.setVisible(false);
    }
  }

  /**
   * <p>Place currency symbol before the number</p>
   */
  private void handleLeadingSymbol(BitcoinConfiguration bitcoinConfiguration) {

    // Add the symbol to the leading position
    LabelDecorator.applyBitcoinSymbolLabel(leadingSymbolLabel, bitcoinConfiguration, "");

    // Remove it from the trailing position
    AwesomeDecorator.removeIcon(trailingSymbolLabel);
    trailingSymbolLabel.setText("");

  }


  /**
   * <p>Place currency symbol after the number</p>
   */
  private void handleTrailingSymbol(BitcoinConfiguration bitcoinConfiguration) {

    // Add the symbol to the trailing position (no text)
    LabelDecorator.applyBitcoinSymbolLabel(trailingSymbolLabel, bitcoinConfiguration, "");

    // Remove it from the leading position
    AwesomeDecorator.removeIcon(leadingSymbolLabel);
    leadingSymbolLabel.setText("");

  }

  /**
   * <p>Populates the exchange label text according to the configured symbols and available providers</p>
   * <p>Note the use of non-breaking spaces (\u00a0) to ensure the entire number is correctly represented</p>
   *
   * @param bitcoinConfiguration The Bitcoin configuration
   * @param localSymbol          The local symbol (e.g. "$")
   * @param localDisplay         The local display (e.g. "1,234.567")
   */
  private void handleExchangeLabelText(BitcoinConfiguration bitcoinConfiguration, String localSymbol, String localDisplay) {

    if (getModel().get().getRateProvider().isPresent() && getModel().get().getStyle() != DisplayAmountStyle.PLAIN) {

      // Have a provider
      if (bitcoinConfiguration.isCurrencySymbolLeading()) {
        // Use leading format
        exchangeLabel.setText(
                Languages.safeText(
                        MessageKey.EXCHANGE_FIAT_RATE_WITH_PROVIDER,
                        "~\u00a0" + localSymbol + "\u00a0",
                        localDisplay,
                        getModel().get().getRateProvider().get()
                ));
      } else {

        // Use trailing format
        exchangeLabel.setText(
                Languages.safeText(
                        MessageKey.EXCHANGE_FIAT_RATE_WITH_PROVIDER,
                        "~\u00a0",
                        localDisplay + "\u00a0" + localSymbol + "\u00a0",
                        getModel().get().getRateProvider().get()
                ));
      }
    } else {

      // No provider or plain style
      if (bitcoinConfiguration.isCurrencySymbolLeading()) {
        // Use leading format
        exchangeLabel.setText(
                Languages.safeText(
                        MessageKey.EXCHANGE_FIAT_RATE,
                        "~\u00a0" + localSymbol + "\u00a0",
                        localDisplay
                ));

      } else {
        // Use trailing format
        exchangeLabel.setText(
                Languages.safeText(
                        MessageKey.EXCHANGE_FIAT_RATE,
                        "~\u00a0",
                        localDisplay + "\u00a0" + localSymbol
                ));
      }

    }
  }

}