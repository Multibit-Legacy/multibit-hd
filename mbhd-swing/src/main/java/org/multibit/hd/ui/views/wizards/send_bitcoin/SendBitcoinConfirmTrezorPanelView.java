package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.MainNetParams;
import org.multibit.hd.core.dto.SendRequestSummary;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.messages.ButtonRequest;
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

  JLabel detailsLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
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

    detailsLabel = Labels.newBlankLabel();
    contentPanel.add(Labels.newPressConfirmOnTrezorNoteShort(), "wrap");
    contentPanel.add(detailsLabel);

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

  }

  /**
   * <p>Handle the hardware wallet events </p>
   *
   * @param event The hardware wallet event indicating a state change
   */
  @Subscribe
  public void onHardwareWalletEvent(final HardwareWalletEvent event) {

    log.debug("Received hardware event: '{}'.{}", event.getEventType().name(), event.getMessage());


    switch (event.getEventType()) {
      case SHOW_BUTTON_PRESS:
        // Update label
        ButtonRequest buttonRequest = (ButtonRequest)event.getMessage().get();
        updateDetailsLabel(buttonRequest.getButtonRequestType().name() + ": " + buttonRequest.getButtonMessage());

        break;

      case SHOW_DEVICE_FAILED:
      case SHOW_DEVICE_DETACHED:
      case SHOW_DEVICE_READY:
      case ADDRESS:
      case SHOW_PIN_ENTRY:
      case SHOW_OPERATION_FAILED:
      case PUBLIC_KEY:
        // Update label
        updateDetailsLabel(event.getEventType().name() + " " +
                event.getMessage().get().toString().subSequence(0, Math.min(event.getMessage().toString().length(), 60)));
        break;
      case SHOW_OPERATION_SUCCEEDED:
        SwingUtilities.invokeLater(
                new Runnable() {
                  @Override
                  public void run() {
                    // Enable next button
                    ViewEvents.fireWizardButtonEnabledEvent(
                            getPanelName(),
                            WizardButton.NEXT,
                            true
                    );

                    // The tx is now complete so commit and broadcast it
                    // Trezor will provide a signed serialized transaction
                    byte[] deviceTxPayload = CoreServices.getOrCreateHardwareWalletService().get().getContext().getSerializedTx().toByteArray();

                    log.info("DeviceTx payload:\n{}", Utils.HEX.encode(deviceTxPayload));

                    // Load deviceTx
                    Transaction deviceTx = new Transaction(MainNetParams.get(), deviceTxPayload);

                    log.info("deviceTx:\n{}", deviceTx.toString());

                    log.debug("Committing and broadcasting the last tx");
                    updateDetailsLabel("Success - transaction is signed"); // TODO localise
                    BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
                    if (bitcoinNetworkService.getLastSendRequestSummaryOptional().isPresent() && bitcoinNetworkService.getLastWalletOptional().isPresent()) {
                      SendRequestSummary sendRequestSummary = bitcoinNetworkService.getLastSendRequestSummaryOptional().get();
                      // Substitute the signed tx from the trezor
                      log.debug("Substituting the Trezor signed tx for the unsigned version '{}'", deviceTx.toString());
                      sendRequestSummary.getSendRequest().get().tx = deviceTx;
                      Wallet wallet = bitcoinNetworkService.getLastWalletOptional().get();

                      // Clear the previous remembered tx so that it is not committed twice
                      bitcoinNetworkService.setLastSendRequestSummaryOptional(Optional.<SendRequestSummary>absent());
                      bitcoinNetworkService.setLastWalletOptional(Optional.<Wallet>absent());
                      bitcoinNetworkService.commitAndBroadcast(sendRequestSummary, wallet);
                    } else {
                      log.debug("Cannot commit and broadcast the last send as it is not present in bitcoinNetworkService");
                    }

                    // Move to the next panel
                    getWizardModel().showNext();
                  }
                });

        break;
    }
  }

  private void updateDetailsLabel(final String text) {
    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                if (detailsLabel != null) {
                  detailsLabel.setText(text);
                }
              }
            });

  }
}
