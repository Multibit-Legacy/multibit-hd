package org.multibit.hd.ui.views.components.enter_amount;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.xeiam.xchange.currency.MoneyUtils;
import net.miginfocom.swing.MigLayout;
import org.joda.money.BigMoney;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.core.utils.CurrencyUtils;
import org.multibit.hd.core.utils.Numbers;
import org.multibit.hd.core.utils.Satoshis;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.text_fields.FormattedDecimalField;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a Bitcoin and local currency amount</li>
 * <li>Support for instant bi-directional conversion through exchange rate</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterAmountView extends AbstractComponentView<EnterAmountModel> {

  // View components
  private FormattedDecimalField bitcoinAmountText;
  private FormattedDecimalField localAmountText;

  private JLabel exchangeRateStatusLabel = Labels.newBlankLabel();
  private JLabel approximatelyLabel = Labels.newBlankLabel();

  private JLabel localCurrencySymbolLabel = Labels.newBlankLabel();
  private JLabel bitcoinSymbolLabel = Labels.newBlankLabel();

  private Optional<ExchangeRateChangedEvent> latestExchangeRateChangedEvent = Optional.absent();

  /**
   * @param model The model backing this view
   */
  public EnterAmountView(EnterAmountModel model) {
    super(model);

    latestExchangeRateChangedEvent = CoreServices
      .getApplicationEventService()
      .getLatestExchangeRateChangedEvent();

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(),
      "[][][][][][]", // Columns
      "[][][]" // Rows
    ));

    // Keep track of the amount fields
    bitcoinAmountText = TextBoxes.newBitcoinAmount(BitcoinSymbol.maxSymbolicAmount().doubleValue());
    localAmountText = TextBoxes.newCurrencyAmount(999_999_999_999_999.9999);

    approximatelyLabel = Labels.newApproximately();

    LabelDecorator.applyLocalCurrencySymbol(localCurrencySymbolLabel);

    // Ensure the Bitcoin symbol label matches the local currency
    Font font = bitcoinSymbolLabel.getFont().deriveFont(Font.PLAIN, (float) MultiBitUI.NORMAL_ICON_SIZE);
    bitcoinSymbolLabel.setFont(font);

    // Use the current Bitcoin configuration
    LabelDecorator.applyBitcoinSymbolLabel(bitcoinSymbolLabel);

    // Bind a key listener to allow instant update of UI to amount changes
    bitcoinAmountText.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {

        updateLocalAmount();

      }
    });

    localAmountText.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        updateBitcoinAmount();
      }

    });

    // Arrange label placement according to configuration
    boolean isCurrencySymbolLeading = Configurations
      .currentConfiguration
      .getBitcoinConfiguration()
      .isCurrencySymbolLeading();

    // Add to the panel
    panel.add(Labels.newAmount(), "span 4,grow,push,wrap");

    if (isCurrencySymbolLeading) {
      panel.add(bitcoinSymbolLabel);
      panel.add(bitcoinAmountText);
      panel.add(approximatelyLabel, "pushy,baseline");
      panel.add(localCurrencySymbolLabel, "pushy,baseline");
      panel.add(localAmountText, "wrap");
    } else {
      panel.add(bitcoinAmountText);
      panel.add(bitcoinSymbolLabel);
      panel.add(approximatelyLabel, "pushy,baseline");
      panel.add(localAmountText);
      panel.add(localCurrencySymbolLabel, "pushy,baseline,wrap");
    }

    panel.add(exchangeRateStatusLabel, "span 4,push,wrap");

    setLocalAmountVisibility();

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    bitcoinAmountText.requestFocusInWindow();
  }

  @Override
  public void updateModelFromView() {
    // Do nothing - the model is updated during key press
  }

  @Subscribe
  public void onExchangeRateChanged(ExchangeRateChangedEvent event) {

    if (panel == null) {
      // Still initialising
      return;
    }

    this.latestExchangeRateChangedEvent = Optional.fromNullable(event);

    setLocalAmountVisibility();

    // Rate has changed so trigger an update if focus is on either amount boxes
    if (bitcoinAmountText.hasFocus()) {
      // User is entering Bitcoin amount so will expect the local to update
      updateLocalAmount();
    }
    if (localAmountText.hasFocus()) {
      // User is entered local amount so will expect the Bitcoin amount to update
      updateBitcoinAmount();
    }


  }

  /**
   * <p>Handles the process of updating the visibility of the local amount</p>
   * <p>This is required when an exchange has failed to provide an exchange rate in the current session</p>
   */
  private void setLocalAmountVisibility() {

    if (latestExchangeRateChangedEvent.isPresent() && latestExchangeRateChangedEvent.get().getRateProvider().isPresent()) {

      setLocalCurrencyComponentVisibility(true);

      // Rate may not be valid
      setExchangeRateStatus(latestExchangeRateChangedEvent.get().isValid());

    } else {

      // Never had a rate so hide the local currency components
      setLocalCurrencyComponentVisibility(false);

      // Rate is not valid by definition
      setExchangeRateStatus(false);

    }

  }

  /**
   * @param visible True if the local currency components should be visible
   */
  private void setLocalCurrencyComponentVisibility(boolean visible) {

    // We can show local currency components
    this.approximatelyLabel.setVisible(visible);
    this.localCurrencySymbolLabel.setVisible(visible);
    this.localAmountText.setVisible(visible);
    this.exchangeRateStatusLabel.setVisible(visible);

  }

  /**
   * @param valid True if the exchange rate is present and valid
   */
  private void setExchangeRateStatus(boolean valid) {

    if (valid) {
      // Update the label to show a check mark
      AwesomeDecorator.bindIcon(
        AwesomeIcon.CHECK,
        exchangeRateStatusLabel,
        true,
        MultiBitUI.NORMAL_ICON_SIZE
      );
      exchangeRateStatusLabel.setText(Languages.safeText(MessageKey.EXCHANGE_RATE_STATUS_OK));
    } else {
      // Update the label to show a cross
      AwesomeDecorator.bindIcon(
        AwesomeIcon.TIMES,
        exchangeRateStatusLabel,
        true,
        MultiBitUI.NORMAL_ICON_SIZE
      );
      exchangeRateStatusLabel.setText(Languages.safeText(MessageKey.EXCHANGE_RATE_STATUS_WARN));
    }

  }

  /**
   * Update the Bitcoin amount based on a change in the local amount
   */
  private void updateBitcoinAmount() {

    String text = localAmountText.getText();
    Optional<Double> value = Numbers.parseDouble(text);

    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.current();

    if (latestExchangeRateChangedEvent.isPresent()) {

      if (value.isPresent()) {
        BigMoney localAmount = MoneyUtils.parseMoney(CurrencyUtils.currentCode(), value.get());

        BigMoney exchangeRate = latestExchangeRateChangedEvent.get().getRate();

        try {
          // Apply the exchange rate
          BigInteger satoshis = Satoshis.fromLocalAmount(localAmount, exchangeRate);

          // Update the model with the plain value
          getModel().get().setSatoshis(satoshis);
          getModel().get().setLocalAmount(localAmount);

          // Use the symbolic amount for display formatting
          BigDecimal symbolicAmount = Satoshis.toSymbolicAmount(satoshis, bitcoinSymbol);
          bitcoinAmountText.setValue(symbolicAmount.doubleValue());

          // Give feedback to the user
          localAmountText.setBackground(Themes.currentTheme.dataEntryBackground());

        } catch (ArithmeticException e) {

          // Give feedback to the user
          localAmountText.setBackground(Themes.currentTheme.invalidDataEntryBackground());

        }

      } else {
        bitcoinAmountText.setText("");

        // Update the model
        getModel().get().setSatoshis(BigInteger.ZERO);
        getModel().get().setLocalAmount(CurrencyUtils.ZERO);
      }

    } else {

      // No exchange rate so no local amount
      getModel().get().setLocalAmount(CurrencyUtils.ZERO);

    }

    setLocalAmountVisibility();
  }

  /**
   * Update the local amount based on a change in the Bitcoin amount
   */
  private void updateLocalAmount() {

    String text = bitcoinAmountText.getText();
    Optional<Double> value = Numbers.parseDouble(text);

    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.current();

    if (latestExchangeRateChangedEvent.isPresent()) {

      if (value.isPresent()) {

        try {
          // Convert to satoshis
          BigInteger satoshis = Satoshis.fromSymbolicAmount(new BigDecimal(value.get()), bitcoinSymbol);

          // Apply the exchange rate
          BigMoney localAmount = Satoshis.toLocalAmount(satoshis, latestExchangeRateChangedEvent.get().getRate());

          // Update the model
          getModel().get().setSatoshis(satoshis);
          getModel().get().setLocalAmount(localAmount);

          // Use double for display formatting
          localAmountText.setValue(localAmount.getAmount().doubleValue());

          // Give feedback to the user
          bitcoinAmountText.setBackground(Themes.currentTheme.dataEntryBackground());

        } catch (ArithmeticException e) {

          // Give feedback to the user
          bitcoinAmountText.setBackground(Themes.currentTheme.invalidDataEntryBackground());
        }

      } else {
        localAmountText.setText("");

        // Update the model
        getModel().get().setSatoshis(BigInteger.ZERO);
        getModel().get().setLocalAmount(CurrencyUtils.ZERO);
      }
    } else {

      // No exchange rate so no local amount
      if (value.isPresent()) {

        try {
          // Convert to satoshis
          BigInteger satoshis = Satoshis.fromSymbolicAmount(new BigDecimal(value.get()), bitcoinSymbol);

          // Update the model
          getModel().get().setSatoshis(satoshis);
          getModel().get().setLocalAmount(CurrencyUtils.ZERO);

          // Give feedback to the user
          localAmountText.setBackground(Themes.currentTheme.dataEntryBackground());

        } catch (ArithmeticException e) {

          // Give feedback to the user
          localAmountText.setBackground(Themes.currentTheme.invalidDataEntryBackground());

        }

      } else {

        // Update the model
        getModel().get().setSatoshis(BigInteger.ZERO);
        getModel().get().setLocalAmount(CurrencyUtils.ZERO);
      }
    }

    setLocalAmountVisibility();

  }

}
