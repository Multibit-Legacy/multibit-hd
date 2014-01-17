package org.multibit.hd.ui.views.components.enter_amount;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
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

  private Optional<BigDecimal> bitcoinAmount=Optional.absent();
  private Optional<BigDecimal> localAmount=Optional.absent();

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
    return getBitcoinAmount();
  }

  @Override
  public void setValue(BigDecimal value) {
    setBitcoinAmount(value);
  }

  /**
   * @return The Bitcoin amount (zero if not present)
   */
  public BigDecimal getBitcoinAmount() {
    return bitcoinAmount.or(BigDecimal.ZERO);
  }

  public void setBitcoinAmount(BigDecimal value) {

    Preconditions.checkNotNull(value, "'value' should be present");

    bitcoinAmount = Optional.of(value);

    // The panel model has changed so alert the wizard
    ViewEvents.fireWizardPanelModelChangedEvent(panelName, bitcoinAmount);

  }

  /**
   * @return The local amount (zero if not present)
   */
  public BigDecimal getLocalAmount() {
    return localAmount.or(BigDecimal.ZERO);
  }

  public void setLocalAmount(BigDecimal value) {

    Preconditions.checkNotNull(value, "'value' should be present");

    localAmount = Optional.of(value);

  }
}
