package org.multibit.hd.ui.views.components.display_amount;

import org.bitcoinj.core.Coin;
import com.google.common.base.Optional;
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
  private Coin coinAmount = Coin.ZERO;
  private BigDecimal localAmount = BigDecimal.ZERO;

  // Default to Bitcoin only (exchanges are an optional feature)
  private boolean localAmountVisible = false;

  private Optional<String> rateProvider = Optional.absent();

  private boolean showNegative = true;
  private final String festName;

  /**
   * @param style        The display amount style
   * @param showNegative True if a "-" is required for negative numbers
   * @param festName     The FEST name to identify this component during testing
   */
  public DisplayAmountModel(DisplayAmountStyle style, boolean showNegative, String festName) {
    this.style = style;
    this.showNegative = showNegative;
    this.festName = festName;
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
   * @return The name to use to identify the JPanel for FEST testing
   */
  public String getFestName() {
    return festName;
  }

  /**
   * @return The display amount style
   */
  public DisplayAmountStyle getStyle() {
    return style;
  }

  /**
   * @return The Bitcoin amount in coins
   */
  public Coin getCoinAmount() {
    return coinAmount;
  }

  public void setCoinAmount(Coin coinAmount) {
    this.coinAmount = coinAmount;
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

  /**
   * @return The rate provider (e.g. "Bitstamp" or absent if no provider is available)
   */
  public Optional<String> getRateProvider() {
    return rateProvider;
  }

  public void setRateProvider(Optional<String> rateProvider) {
    this.rateProvider = rateProvider;
  }

  public boolean isShowNegative() {
    return showNegative;
  }

}
