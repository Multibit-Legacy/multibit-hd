package org.multibit.hd.ui.views.components.enter_amount;

import com.google.bitcoin.core.Coin;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.core.utils.Coins;
import org.multibit.hd.core.utils.Numbers;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.text_fields.FormattedDecimalField;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

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

  private static final Logger log = LoggerFactory.getLogger(EnterAmountView.class);

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

    // Set the maximum values for the amount fields
    bitcoinAmountText = TextBoxes.newBitcoinAmount(BitcoinSymbol.maxSymbolicAmount().doubleValue());
    localAmountText = TextBoxes.newLocalAmount(999_999_999_999_999.9999);

    // Set initial Bitcoin amount from the model (if non-zero)
    if (!Coin.ZERO.equals(getModel().get().getCoinAmount())) {
      BitcoinSymbol bitcoinSymbol = BitcoinSymbol.current();
      BigDecimal symbolicAmount = Coins.toSymbolicAmount(getModel().get().getCoinAmount(), bitcoinSymbol);
      bitcoinAmountText.setText(symbolicAmount.toPlainString());
      updateLocalAmount();
    }

    approximatelyLabel = Labels.newApproximately();

    LabelDecorator.applyLocalCurrencySymbol(localCurrencySymbolLabel);

    // Ensure the Bitcoin symbol label matches the local currency
    Font font = bitcoinSymbolLabel.getFont().deriveFont(Font.PLAIN, (float) MultiBitUI.NORMAL_ICON_SIZE);
    bitcoinSymbolLabel.setFont(font);

    // Use the current Bitcoin configuration
    LabelDecorator.applyBitcoinSymbolLabel(bitcoinSymbolLabel);

    // Bind a key listener to allow instant update of UI to amount changes
    // Do not use a focus listener because it will move the value according to
    // the inexact fiat value leading to 10mB becoming 10.00635mB
    bitcoinAmountText.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {

        if (e.isActionKey() || e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_SHIFT) {
          // Ignore
          return;
        }

        updateLocalAmount();

      }
    });

    // Bind a key listener to allow instant update of UI to amount changes
    // Do not use a focus listener because it will move the value according to
    // the inexact fiat value leading to 10mB becoming 10.00635mB
    localAmountText.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {

        if (e.isActionKey() || e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_SHIFT) {
          // Ignore
          return;
        }

     updateBitcoinAmount();
      }

    });

    // Arrange label placement according to configuration
    boolean isCurrencySymbolLeading = Configurations
      .currentConfiguration
      .getBitcoin()
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

    if (latestExchangeRateChangedEvent.isPresent()
      && latestExchangeRateChangedEvent.get().getRateProvider().isPresent()
      && !ExchangeKey.current().equals(ExchangeKey.NONE)) {

      setLocalCurrencyComponentVisibility(true);

      // Rate may not be valid
      setExchangeRateStatus(latestExchangeRateChangedEvent.get().isValid());

    } else {

      // No rate or rate provider

      // Hide the local currency components
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

    // Build the value directly from the string
    Optional<BigDecimal> value = Numbers.parseBigDecimal(localAmountText.getText());

    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.current();

    if (latestExchangeRateChangedEvent.isPresent()) {

      if (value.isPresent()) {

        BigDecimal localAmount = value.get();

        BigDecimal exchangeRate = latestExchangeRateChangedEvent.get().getRate();

        try {
          // Apply the exchange rate
          Coin coin = Coins.fromLocalAmount(localAmount, exchangeRate);

          // Update the model with the plain value
          getModel().get().setCoinAmount(coin);
          getModel().get().setLocalAmount(value);

          // Use the symbolic amount in setValue() for display formatting
          BigDecimal symbolicAmount = Coins.toSymbolicAmount(coin, bitcoinSymbol);
          bitcoinAmountText.setValue(symbolicAmount);

          // Give feedback to the user
          localAmountText.setBackground(Themes.currentTheme.dataEntryBackground());

        } catch (ArithmeticException e) {

          // Give feedback to the user
          localAmountText.setBackground(Themes.currentTheme.invalidDataEntryBackground());

        }

      } else {
        bitcoinAmountText.setText("");

        // Update the model
        getModel().get().setCoinAmount(Coin.ZERO);
        getModel().get().setLocalAmount(Optional.<BigDecimal>absent());
      }

    } else {

      // No exchange rate so no local amount
      getModel().get().setLocalAmount(Optional.<BigDecimal>absent());

    }

    setLocalAmountVisibility();
  }

  /**
   * Update the local amount based on a change in the Bitcoin amount
   */
  private void updateLocalAmount() {

    // Build the value directly from the string
    Optional<BigDecimal> value = Numbers.parseBigDecimal(bitcoinAmountText.getText());

    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.current();

    if (latestExchangeRateChangedEvent.isPresent()) {

      if (value.isPresent()) {

        try {

          Coin coin = Coins.fromSymbolicAmount(value.get(), bitcoinSymbol);

          // Apply the exchange rate
          BigDecimal localAmount = Coins.toLocalAmount(coin, latestExchangeRateChangedEvent.get().getRate());

          // Update the model
          getModel().get().setCoinAmount(coin);
          if (localAmount.compareTo(BigDecimal.ZERO) != 0) {
            getModel().get().setLocalAmount(Optional.of(localAmount));
          } else {
            getModel().get().setLocalAmount(Optional.<BigDecimal>absent());
          }
          // Use setValue for the local amount so that the display formatter
          // will match the currency requirements
          localAmountText.setValue(localAmount);

          // Give feedback to the user
          bitcoinAmountText.setBackground(Themes.currentTheme.dataEntryBackground());

        } catch (ArithmeticException e) {

          // Give feedback to the user
          bitcoinAmountText.setBackground(Themes.currentTheme.invalidDataEntryBackground());
        }

      } else {
        localAmountText.setText("");

        // Update the model
        getModel().get().setCoinAmount(Coin.ZERO);
        getModel().get().setLocalAmount(Optional.<BigDecimal>absent());
      }
    } else {

      // No exchange rate so no local amount
      if (value.isPresent()) {

        try {
          // Use the value directly
          Coin coin = Coins.fromSymbolicAmount(value.get(), bitcoinSymbol);

          // Update the model
          getModel().get().setCoinAmount(coin);
          getModel().get().setLocalAmount(Optional.<BigDecimal>absent());

          // Give feedback to the user
          localAmountText.setBackground(Themes.currentTheme.dataEntryBackground());

        } catch (ArithmeticException e) {

          // Give feedback to the user
          localAmountText.setBackground(Themes.currentTheme.invalidDataEntryBackground());

        }

      } else {

        // Update the model
        getModel().get().setCoinAmount(Coin.ZERO);
        getModel().get().setLocalAmount(Optional.<BigDecimal>absent());
      }
    }

    setLocalAmountVisibility();

  }

}
