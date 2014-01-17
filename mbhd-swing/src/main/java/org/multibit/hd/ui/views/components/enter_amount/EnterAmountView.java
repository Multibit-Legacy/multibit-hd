package org.multibit.hd.ui.views.components.enter_amount;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Numbers;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.AbstractView;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.text_fields.FormattedDecimalField;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
public class EnterAmountView extends AbstractView<EnterAmountModel> {

  // View components
  private FormattedDecimalField bitcoinAmountText;
  private FormattedDecimalField localAmountText;

  private JLabel exchangeRateStatusLabel = new JLabel("");
  private JLabel exchangeNameLabel = new JLabel("");
  private JLabel approximatelyLabel = new JLabel("");
  private JLabel localCurrencySymbolLabel = new JLabel("");

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
  public JPanel newPanel() {

    panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[][][][][][]", // Columns
      "[][][][]" // Rows
    ));

    // Keep track of the amount fields
    bitcoinAmountText = TextBoxes.newBitcoinAmount(20_999_999.12345678);
    localAmountText = TextBoxes.newCurrencyAmount(20_999_999_123.45678);

    approximatelyLabel = Labels.newApproximately();
    localCurrencySymbolLabel = Labels.newLocalCurrencySymbol();
    exchangeNameLabel = Labels.newCurrentExchangeName();

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
      .getI18NConfiguration()
      .isCurrencySymbolLeading();

    isCurrencySymbolLeading = false;

    // Add to the panel
    panel.add(Labels.newEnterAmount(), "span 4,grow,push,wrap");

    if (isCurrencySymbolLeading) {
      panel.add(Labels.newBitcoinCurrencySymbol());
      panel.add(bitcoinAmountText);
      panel.add(approximatelyLabel, "pushy,baseline");
      panel.add(localCurrencySymbolLabel, "pushy,baseline");
      panel.add(localAmountText, "wrap");
    } else {
      panel.add(bitcoinAmountText);
      panel.add(Labels.newBitcoinCurrencySymbol());
      panel.add(approximatelyLabel, "pushy,baseline");
      panel.add(localAmountText);
      panel.add(localCurrencySymbolLabel, "pushy,baseline,wrap");
    }

    panel.add(exchangeRateStatusLabel, "span 4,push,wrap");
    panel.add(exchangeNameLabel, "span 4,push,wrap");

    setLocalAmountVisibility();

    return panel;

  }

  @Override
  public void updateModel() {
    // Do nothing - the model is updated during key press
  }

  @Subscribe
  public void onExchangeRateChanged(ExchangeRateChangedEvent event) {

    this.latestExchangeRateChangedEvent = Optional.fromNullable(event);

    setLocalAmountVisibility();

    // Rate has changed so trigger an update based on focus
    if (bitcoinAmountText.hasFocus()) {
      updateLocalAmount();
    } else {
      updateBitcoinAmount();
    }

  }

  /**
   * <p>Handles the process of updating the visibility of the local amount</p>
   * <p>This is required when an exchange has failed to provide an exchange rate in the current session</p>
   */
  private void setLocalAmountVisibility() {

    if (latestExchangeRateChangedEvent.isPresent()) {

      setLocalCurrencyComponentVisibility(true);

      // Set the exchange rate provider
      exchangeNameLabel.setText(Labels.newCurrentExchangeName().getText());

      // Rate may be valid
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
    this.exchangeNameLabel.setVisible(visible);

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
        AwesomeDecorator.NORMAL_ICON_SIZE
      );
      exchangeRateStatusLabel.setText(Languages.safeText(MessageKey.EXCHANGE_RATE_STATUS_OK));
    } else {
      // Update the label to show a cross
      AwesomeDecorator.bindIcon(
        AwesomeIcon.TIMES,
        exchangeRateStatusLabel,
        true,
        AwesomeDecorator.NORMAL_ICON_SIZE
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

    if (latestExchangeRateChangedEvent.isPresent()) {

      if (value.isPresent()) {
        BigDecimal localAmount = new BigDecimal(value.get()).setScale(8, RoundingMode.HALF_EVEN);
        BigDecimal bitcoinAmount = localAmount
          .divide(latestExchangeRateChangedEvent.get().getRate(), 12, RoundingMode.HALF_EVEN);
        // Use double for display
        bitcoinAmountText.setValue(bitcoinAmount.doubleValue());

        // Update the model
        getModel().get().setValue(bitcoinAmount);

      } else {
        bitcoinAmountText.setText("");

        // Update the model
        getModel().get().setValue(BigDecimal.ZERO);
      }

    }

    setLocalAmountVisibility();
  }

  /**
   * Update the local amount based on a change in the Bitcoin amount
   */
  private void updateLocalAmount() {

    String text = bitcoinAmountText.getText();
    Optional<Double> value = Numbers.parseDouble(text);

    if (latestExchangeRateChangedEvent.isPresent()) {

      if (value.isPresent()) {
        BigDecimal bitcoinAmount = new BigDecimal(value.get()).setScale(12, RoundingMode.HALF_EVEN);
        BigDecimal localAmount = bitcoinAmount
          .multiply(latestExchangeRateChangedEvent.get().getRate())
          .setScale(8, RoundingMode.HALF_EVEN);
        // Use double for display
        localAmountText.setValue(localAmount.doubleValue());

        // Update the model
        getModel().get().setValue(bitcoinAmount);
      } else {
        localAmountText.setText("");

        // Update the model
        getModel().get().setValue(BigDecimal.ZERO);
      }
    } else {

      if (value.isPresent()) {

        BigDecimal bitcoinAmount = new BigDecimal(value.get()).setScale(12, RoundingMode.HALF_EVEN);

        // Update the model
        getModel().get().setValue(bitcoinAmount);

      } else {

        // Update the model
        getModel().get().setValue(BigDecimal.ZERO);
      }
    }

    setLocalAmountVisibility();

  }

}
