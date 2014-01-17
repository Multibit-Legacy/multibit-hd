package org.multibit.hd.ui.views.wizards.send_bitcoin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountView;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

import static org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState.ENTER_AMOUNT;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class SendBitcoinEnterAmountView extends AbstractWizardView<SendBitcoinWizardModel, SendBitcoinEnterAmountPanelModel> {

  // Panel specific components
  private ModelAndView<EnterRecipientModel, EnterRecipientView> enterRecipientMaV;
  private ModelAndView<EnterAmountModel, EnterAmountView> enterAmountMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public SendBitcoinEnterAmountView(AbstractWizard<SendBitcoinWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.SEND_BITCOIN_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    enterRecipientMaV = Components.newEnterRecipient(ENTER_AMOUNT.name());
    enterAmountMaV = Components.newEnterAmount(ENTER_AMOUNT.name());

    // Configure the panel model
    setPanelModel(new SendBitcoinEnterAmountPanelModel(
      enterRecipientMaV.getModel(),
      enterAmountMaV.getModel()
    ));

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout constrains
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(enterRecipientMaV.getView().newPanel(),"wrap");
    panel.add(enterAmountMaV.getView().newPanel(),"wrap");

    return panel;
  }

  @Override
  public void fireViewEvents() {

    // Disable the next button
    ViewEvents.fireWizardButtonEnabledEvent(ENTER_AMOUNT.name(), WizardButton.NEXT, false);
  }

  @Override
  public boolean updatePanelModel() {

    enterAmountMaV.getView().updateModel();
    enterRecipientMaV.getView().updateModel();

    return false;
  }

}
