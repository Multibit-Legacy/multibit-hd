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
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

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

public class SendBitcoinEnterAmountView extends AbstractWizardView<SendBitcoinWizardModel, BigDecimal> {

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

    enterAmountMaV = Components.newEnterAmount(ENTER_AMOUNT.name());
    setPanelModel(enterAmountMaV.getModel().getValue());

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout constrains
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    ActionListener listener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Done");
      }
    };

    panel.add(Components.newRecipient(listener),"wrap");
    panel.add(enterAmountMaV.getView().newPanel(),"wrap");

    return panel;
  }

  @Override
  public void fireViewEvents() {
    ViewEvents.fireWizardButtonEnabledEvent(ENTER_AMOUNT.name(), WizardButton.NEXT, false);
  }

  @Override
  public boolean updatePanelModel() {
    enterAmountMaV.getView().updateModel();
    return false;
  }

}
