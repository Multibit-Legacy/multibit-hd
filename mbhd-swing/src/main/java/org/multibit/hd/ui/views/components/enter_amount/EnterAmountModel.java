package org.multibit.hd.ui.views.components.enter_amount;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;

import java.math.BigDecimal;
import java.math.BigInteger;

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

  private Optional<BigInteger> satoshis = Optional.absent();
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
    return this;
  }

  @Override
  public void setValue(EnterAmountModel value) {
    // Do nothing
  }

  /**
   * @return The Bitcoin amount (zero if not present) in satoshis
   */
  public BigInteger getSatoshis() {
    return satoshis.or(BigInteger.ZERO);
  }

  /**
   * @param value The Bitcoin amount (fires a "component model changed" event)
   */
  public void setSatoshis(BigInteger value) {

    Preconditions.checkNotNull(value, "'value' should be present");

    satoshis = Optional.of(value);

    // Fire a component model updated event
    ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));

  }

  /**
   * @return The local amount (zero if not present)
   */
  public BigDecimal getLocalAmount() {
    return localAmount.or(BigDecimal.ZERO);
  }

  /**
   * @param value The local amount - no component event since the Bitcoin value drives this component
   */
  public void setLocalAmount(BigDecimal value) {

    Preconditions.checkNotNull(value, "'value' should be present");

    localAmount = Optional.of(value);

  }
}
