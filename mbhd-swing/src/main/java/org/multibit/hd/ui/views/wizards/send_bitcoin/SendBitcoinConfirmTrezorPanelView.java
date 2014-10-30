package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Ask the user to press ok on their Trezor whilst sending bitcoin</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class SendBitcoinConfirmTrezorPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinConfirmTrezorPanelModel> {

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public SendBitcoinConfirmTrezorPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PRESS_CONFIRM_ON_TREZOR_TITLE, AwesomeIcon.SHIELD);


    HardwareWalletService.hardwareWalletEventBus.register(this);

  }

  @Override
  public void newPanelModel() {

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    contentPanel.add(Labels.newPressConfirmOnTrezorNote(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<SendBitcoinWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

    // A Trezor button click will call this method
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      true
    );

    getWizardModel().showNext();
  }

  /**
    * <p>Handle the hardware wallet events </p>
    *
    * @param event The hardware wallet event indicating a state change
    */
   @Subscribe
   public void onHardwareWalletEvent(HardwareWalletEvent event) {

     log.debug("Received hardware event: '{}'.{}", event.getEventType().name(), event.getMessage());

     switch (event.getEventType()) {
       case SHOW_DEVICE_FAILED:
       case SHOW_DEVICE_DETACHED:
       case SHOW_DEVICE_READY:
       case ADDRESS:
       case SHOW_PIN_ENTRY:
       case SHOW_OPERATION_SUCCEEDED:
       case SHOW_OPERATION_FAILED:
       case PUBLIC_KEY:
         // Do nothing
         break;
     }
   }
}
