package org.multibit.hd.ui.views.components.display_amount;

import org.multibit.hd.ui.i18n.BitcoinSymbol;
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

  private final DisplayAmountStyle style;

  // Values depend on earlier wizard panel
  private BigDecimal satoshis = BigDecimal.ZERO;
  private BigDecimal localAmount = BigDecimal.ZERO;

  private boolean localAmountVisible = true;

  /**
   * @param style The display amount style
   */
  public DisplayAmountModel(DisplayAmountStyle style) {
    this.style = style;
  }

  @Override
  public DisplayAmountModel getValue() {
    return this;
  }

  @Override
  public void setValue(DisplayAmountModel value) {
    // Do nothing
  }

  /**
   * @return The display amount style
   */
  public DisplayAmountStyle getStyle() {
    return style;
  }

  /**
   * @return The Bitcoin amount with symbol multiplier
   */
  public BigDecimal getSymbolicBitcoinAmount() {
    return getSatoshis().multiply(BitcoinSymbol.current().multiplier());
  }

  /**
   * @return The Bitcoin amount without symbol multiplier
   */
  public BigDecimal getSatoshis() {
    return satoshis;
  }

  public void setSatoshis(BigDecimal satoshis) {
    this.satoshis = satoshis;
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

  /**
   * @return True if the local amount should be visible
   */
  public boolean isLocalAmountVisible() {
    return localAmountVisible;
  }

  public void setLocalAmountVisible(boolean localAmountVisible) {
    this.localAmountVisible = localAmountVisible;
  }
}
