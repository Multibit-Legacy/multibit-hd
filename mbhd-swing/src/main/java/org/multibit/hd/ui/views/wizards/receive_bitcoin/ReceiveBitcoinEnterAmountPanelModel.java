package org.multibit.hd.ui.views.wizards.receive_bitcoin;

import com.google.common.base.Optional;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.wizards.AbstractPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "send bitcoin" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ReceiveBitcoinEnterAmountPanelModel extends AbstractPanelModel {

  private final EnterAmountModel enterAmountModel;

  /**
   * @param panelName           The panel name
   * @param enterAmountModel    The "enter amount" component model
   */
  public ReceiveBitcoinEnterAmountPanelModel(
    String panelName,
    EnterAmountModel enterAmountModel
  ) {
    super(panelName);
    this.enterAmountModel = enterAmountModel;
  }

  /**
   * @return The amount model
   */
  public EnterAmountModel getEnterAmountModel() {
    return enterAmountModel;
  }

  @Override
  protected void update(Optional componentModel) {

    // No need to update since we have the references

    // Inform the wizard model that a change has occurred
    ViewEvents.fireWizardPanelModelChangedEvent(panelName, Optional.of(this));

  }
}
