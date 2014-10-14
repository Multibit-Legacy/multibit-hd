package org.multibit.hd.ui.views.components.enter_amount;

import com.google.bitcoin.core.Coin;
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
public class EnterAmountModel implements Model<EnterAmountModel> {

  private Optional<Coin> coinAmount = Optional.absent();
  private Optional<BigDecimal> localAmount = Optional.absent();

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
  public EnterAmountModel getValue() {
    throw new IllegalStateException("This method should not be called directly.");
  }

  @Override
  public void setValue(EnterAmountModel value) {
    throw new IllegalStateException("This method should not be called directly.");
  }

  /**
   * @return The Bitcoin amount (zero if not present) in coins
   */
  public Coin getCoinAmount() {
    return coinAmount.or(Coin.ZERO);
  }

  /**
   * @param value The Bitcoin amount (fires a "component model changed" event)
   */
  public void setCoinAmount(Coin value) {

    coinAmount = Optional.of(value);

    // Fire a component model updated event
    ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));

  }

  /**
   * @return The local amount
   */
  public Optional<BigDecimal> getLocalAmount() {
    return localAmount;
  }

  /**
   * @param value The local amount - no component event since the Bitcoin value drives this component
   */
  public void setLocalAmount(Optional<BigDecimal> value) {

    localAmount = value;

  }
}
