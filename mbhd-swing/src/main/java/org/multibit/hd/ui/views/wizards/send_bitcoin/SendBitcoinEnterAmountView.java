package org.multibit.hd.ui.views.wizards.send_bitcoin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class SendBitcoinEnterAmountView extends AbstractWizardView<SendBitcoinWizardModel, String> {

  // Model
  private String model;

  /**
   * @param wizard The wizard managing the states
   */
  public SendBitcoinEnterAmountView(AbstractWizard<SendBitcoinWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.SEND_BITCOIN_TITLE);

    PanelDecorator.addCancelPreviousNext(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    model = "TODO replace with a proper model";
    setPanelModel(model);

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constrains
      "[][][]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Components.newContactSearch(),"wrap");
    panel.add(Components.newBitcoinAmount(),"wrap");

    return panel;
  }

  @Override
  public boolean updatePanelModel() {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
    return true;
  }

}
