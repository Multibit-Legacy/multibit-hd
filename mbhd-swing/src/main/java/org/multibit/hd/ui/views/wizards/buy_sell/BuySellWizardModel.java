package org.multibit.hd.ui.views.wizards.buy_sell;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "buy/sell" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.2.0
 *
 */
public class BuySellWizardModel extends AbstractWizardModel<BuySellState> {

  /**
   * @param state The state object
   */
  public BuySellWizardModel(BuySellState state) {
    super(state);
  }

  public void setState(BuySellState state) {
    this.state = state;
  }

}
