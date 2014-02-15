package org.multibit.hd.ui.views.components.display_amount;

import com.google.common.base.Optional;
import org.joda.money.BigMoney;
import org.multibit.hd.core.utils.CurrencyUtils;
import org.multibit.hd.ui.models.Model;

import java.math.BigInteger;

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
  private BigInteger satoshis = BigInteger.ZERO;
  private BigMoney localAmount = CurrencyUtils.ZERO;

  private boolean localAmountVisible = true;
  private Optional<String> rateProvider = Optional.absent();

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
   * @return The Bitcoin amount in satoshis
   */
  public BigInteger getSatoshis() {
    return satoshis;
  }

  public void setSatoshis(BigInteger satoshis) {
    this.satoshis = satoshis;
  }

  /**
   * @return The local amount
   */
  public BigMoney getLocalAmount() {
    return localAmount;
  }

  public void setLocalAmount(BigMoney localAmount) {
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

  /**
   * @return The rate provider (e.g. "Bitstamp" or absent if no provider is available)
   */
  public Optional<String> getRateProvider() {
    return rateProvider;
  }

  public void setRateProvider(Optional<String> rateProvider) {
    this.rateProvider = rateProvider;
  }
}
