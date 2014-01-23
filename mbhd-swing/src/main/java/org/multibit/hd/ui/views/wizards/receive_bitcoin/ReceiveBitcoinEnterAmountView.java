package org.multibit.hd.ui.views.wizards.receive_bitcoin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressModel;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressView;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeModel;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeView;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Receive bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class ReceiveBitcoinEnterAmountView extends AbstractWizardView<ReceiveBitcoinWizardModel, ReceiveBitcoinEnterAmountPanelModel> {

  // Panel specific components
  private ModelAndView<EnterAmountModel, EnterAmountView> enterAmountMaV;
  private ModelAndView<DisplayBitcoinAddressModel, DisplayBitcoinAddressView> displayBitcoinAddressMaV;
  private ModelAndView<DisplayQRCodeModel, DisplayQRCodeView> displayQRCodeMaV;

  private JTextField label;

  private JButton showQRCode;

  /**
   * @param wizard The wizard managing the states
   */
  public ReceiveBitcoinEnterAmountView(AbstractWizard<ReceiveBitcoinWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.RECEIVE_BITCOIN_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    enterAmountMaV = Components.newEnterAmountMaV(getPanelName());
    displayBitcoinAddressMaV = Components.newDisplayBitcoinAddressMaV("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");
    displayQRCodeMaV = Components.newDisplayQRCodeMaV("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    label = TextBoxes.newEnterLabel();
    showQRCode = Buttons.newQRCodeButton(getToggleQRCodeAction());

    // Configure the panel model
    setPanelModel(new ReceiveBitcoinEnterAmountPanelModel(
      getPanelName(),
      enterAmountMaV.getModel(),
      displayBitcoinAddressMaV.getModel()
    ));

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[][][]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(enterAmountMaV.getView().newPanel(),"span 3,wrap");
    panel.add(Labels.newRecipient());
    panel.add(displayBitcoinAddressMaV.getView().newPanel(),"growx,push");
    panel.add(showQRCode,"wrap");
    panel.add(Labels.newNotes());
    panel.add(label,"span 2,grow,wrap");

    return panel;
  }

  /**
   * @return A new action for toggling the display of the QR code
   */
  private Action getToggleQRCodeAction() {

    // Show or hide the QR code
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // Show the QR code as a popover
        Panels.showLightBoxPopover(displayBitcoinAddressMaV.getView().newPanel());

      }

    };
  }

  @Override
  public void fireViewEvents() {

    // Disable the next button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);
  }

  @Override
  public boolean updatePanelModel() {

    enterAmountMaV.getView().updateModel();

    // The panel model has changed so alert the wizard
    ViewEvents.fireWizardPanelModelChangedEvent(getPanelName(), getPanelModel());

    return false;
  }

}
