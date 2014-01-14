package org.multibit.hd.ui.views.components.enter_amount;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.ui.views.AbstractView;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;

import javax.swing.*;
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
public class EnterAmountView extends AbstractView<EnterAmountModel> {

  // View components
  private JTextField bitcoinAmountText;
  private JTextField localAmountText;

  private JLabel exchangeNameLabel;

  /**
   * The exchange rate in the local currency (e.g. 1000 means 1000 USD = 1 bitcoin)
   */
  private BigDecimal exchangeRate;

  /**
   * @param model The model backing this view
   */
  public EnterAmountView(EnterAmountModel model) {
    super(model);

  }

  @Override
  public JPanel newPanel() {

    panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[][][][][]", // Columns
      "[]10[]10[]" // Rows
    ));

    // Keep track of the password fields
    bitcoinAmountText = TextBoxes.newCurrencyAmount();
    localAmountText = TextBoxes.newCurrencyAmount();
    exchangeNameLabel = Labels.newCurrentExchangeName();

    // Bind a key listener to allow instant update of UI to matched passwords
    bitcoinAmountText.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        BigDecimal bitcoinAmount = new BigDecimal(bitcoinAmountText.getText());
        BigDecimal localAmount = bitcoinAmount.multiply(exchangeRate);
        localAmountText.setText(localAmount.toPlainString());
      }

    });
    localAmountText.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        BigDecimal localAmount = new BigDecimal(localAmountText.getText());
        BigDecimal bitcoinAmount = localAmount.divide(exchangeRate);
        bitcoinAmountText.setText(bitcoinAmount.toPlainString());
      }

    });

    // Add to the panel
    panel.add(Labels.newEnterAmount(),"span 4,grow,push,wrap");
    panel.add(Labels.newBitcoinCurrencySymbol());
    panel.add(bitcoinAmountText);
    panel.add(Labels.newApproximately());
    panel.add(localAmountText,"wrap");
    panel.add(exchangeNameLabel,"span 4,push,wrap");

    return panel;


  }

  @Override
  public void updateModel() {
  }

  @Subscribe
  public void onExchangeRateChanged(ExchangeRateChangedEvent event) {

    // Transfer the text to the view
    this.exchangeNameLabel.setText(Labels.newCurrentExchangeName().getText());
    this.exchangeRate = event.getRate();

  }

}
