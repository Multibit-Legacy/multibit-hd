package org.multibit.hd.ui.views.components.enter_amount;

import com.google.common.base.Optional;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;

import java.math.BigDecimal;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Show/hide the seed phrase (initially hidden)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterAmountModel implements Model<BigDecimal> {

  private BigDecimal bitcoinAmount;

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public EnterAmountModel(String panelName) {
    this.panelName = panelName;
  }


  /**
   * @return The panel name that this component is associated with
   */
  public String getPanelName() {
    return panelName;
  }

  @Override
  public BigDecimal getValue() {
    return bitcoinAmount;
  }

  @Override
  public void setValue(BigDecimal value) {
    this.bitcoinAmount = value;
    // Have a possible match so alert the wizard model
    ViewEvents.fireWizardPanelModelChangedEvent(panelName, Optional.of(bitcoinAmount));
  }

  /**
   * @param bitcoinAmount The bitcoin amount
   * @param exchangeRate  The exchange rate in the local currency (e.g. 1000 USD = 1 bitcoin)
   *
   * @return The local amount
   */
  public BigDecimal calculateLocalAmount(BigDecimal bitcoinAmount, BigDecimal exchangeRate) {

    return bitcoinAmount.multiply(exchangeRate);
  }

  /**
   * @param localAmount  The local amount
   * @param exchangeRate The exchange rate in the local currency (e.g. 1000 USD = 1 bitcoin)
   *
   * @return The bitcoin amount
   */
  public BigDecimal calculateBitcoinAmount(BigDecimal localAmount, BigDecimal exchangeRate) {

    return localAmount.divide(exchangeRate);
  }

}
