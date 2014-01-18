package org.multibit.hd.ui.views.components.display_amount;

import org.multibit.hd.ui.models.Model;

import java.math.BigDecimal;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Storage of the Bitcoin and local amounts to display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplayAmountModel implements Model<DisplayAmountModel> {

  private BigDecimal bitcoinAmount;
  private BigDecimal localAmount;

  @Override
  public DisplayAmountModel getValue() {
    return this;
  }

  @Override
  public void setValue(DisplayAmountModel value) {
    // Do nothing
  }

  /**
   * @return The Bitcoin amount
   */
  public BigDecimal getBitcoinAmount() {
    return bitcoinAmount;
  }

  public void setBitcoinAmount(BigDecimal bitcoinAmount) {
    this.bitcoinAmount = bitcoinAmount;
  }

  /**
   * @return The local amount
   */
  public BigDecimal getLocalAmount() {
    return localAmount;
  }

  public void setLocalAmount(BigDecimal localAmount) {
    this.localAmount = localAmount;
  }
}
