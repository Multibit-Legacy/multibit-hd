package org.multibit.hd.ui.views.wizards.buy_sell;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "Buy/Sell":</p>
 * <ol>
 * <li>Confirm choice</li>
 * </ol>
 *
 * @since 0.2.0
 *
 */
public class BuySellWizard extends AbstractWizard<BuySellWizardModel> {

  public BuySellWizard(BuySellWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(BuySellState.SHOW_PARTNER_NOTES.name(), new BuySellSelectPanelView(this, BuySellState.SHOW_PARTNER_NOTES.name()));

  }

}
