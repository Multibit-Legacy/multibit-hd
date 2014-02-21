package org.multibit.hd.ui.views.wizards.receive_bitcoin;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.store.FiatPayment;
import org.multibit.hd.core.store.PaymentRequest;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressModel;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressView;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeModel;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeView;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountView;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Receive bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class ReceiveBitcoinEnterAmountPanelView extends AbstractWizardPanelView<ReceiveBitcoinWizardModel, ReceiveBitcoinEnterAmountPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(ReceiveBitcoinEnterAmountPanelView.class);

  // Panel specific components
  private JTextArea notesTextArea;

  private ModelAndView<EnterAmountModel, EnterAmountView> enterAmountMaV;
  private ModelAndView<DisplayBitcoinAddressModel, DisplayBitcoinAddressView> displayBitcoinAddressMaV;
  private ModelAndView<DisplayQRCodeModel, DisplayQRCodeView> displayQRCodeMaV;

  private JTextField label;

  private JButton showQRCode;

  /**
   * @param wizard The wizard managing the states
   */
  public ReceiveBitcoinEnterAmountPanelView(AbstractWizard<ReceiveBitcoinWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.RECEIVE_BITCOIN_TITLE);

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void newPanelModel() {

    enterAmountMaV = Components.newEnterAmountMaV(getPanelName());

    // TODO get the next address from WalletService
    Address nextAddress = WalletManager.INSTANCE.getCurrentWalletData().get().getWallet().getKeys().get(0).toAddress(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));
    displayBitcoinAddressMaV = Components.newDisplayBitcoinAddressMaV(nextAddress.toString());

    // Create the QR code display
    displayQRCodeMaV = Components.newDisplayQRCodeMaV();

    label = TextBoxes.newEnterLabel();
    showQRCode = Buttons.newQRCodeButton(getShowQRCodePopoverAction());

    // User entered text
    notesTextArea = TextBoxes.newEnterNotes(MultiBitUI.RECEIVE_ADDRESS_LABEL_LENGTH);

    // Configure the panel model
    setPanelModel(new ReceiveBitcoinEnterAmountPanelModel(
      getPanelName(),
      enterAmountMaV.getModel(),
      displayBitcoinAddressMaV.getModel()
    ));

    getWizardModel().setEnterAmountModel(enterAmountMaV.getModel());
    getWizardModel().setTransactionLabel(label.getText());
    getWizardModel().setNotes(Optional.of(notesTextArea.getText()));
  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.CLOUD_DOWNLOAD);

    panel.setLayout(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[][][]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(enterAmountMaV.getView().newComponentPanel(), "span 3,wrap");
    panel.add(Labels.newRecipient());
    panel.add(displayBitcoinAddressMaV.getView().newComponentPanel(), "growx,push");
    panel.add(showQRCode, "wrap");
    panel.add(Labels.newQRCodeLabelLabel());
    panel.add(label, "span 2,wrap");
    panel.add(Labels.newNotes());
    panel.add(notesTextArea, "span 3,wrap");

    return panel;
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Finish button is always enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getFinishButton().requestFocusInWindow();

        getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              savePaymentRequest();
            }
          });
      }
    });

  }

  /**
   * Save the displayed payment request
   */
  private void savePaymentRequest() {
    WalletService walletService = MultiBitHD.getWalletService();

    Preconditions.checkNotNull(walletService, "The wallet service was null so cannot save the payment request");

    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setNote(notesTextArea.getText());
    paymentRequest.setDate(DateTime.now());
    paymentRequest.setAddress(displayBitcoinAddressMaV.getModel().getValue());
    paymentRequest.setLabel(label.getText());
    paymentRequest.setAmountBTC(enterAmountMaV.getModel().getSatoshis());
    FiatPayment fiatPayment = new FiatPayment();
    fiatPayment.setAmount(enterAmountMaV.getModel().getLocalAmount().toString());
    // TODO add exchange rate and exchange name
    paymentRequest.setAmountFiat(fiatPayment);

    walletService.getPayments().getPaymentRequests().add(paymentRequest);
    walletService.writePayments();
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // No need to update since we expose the component models

    // No view events to fire

  }

  /**
   * @return A new action for showing the QR code popover
   */
  private Action getShowQRCodePopoverAction() {

    // Show or hide the QR code
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        ReceiveBitcoinEnterAmountPanelModel model = getPanelModel().get();

        String bitcoinAddress = model.getDisplayBitcoinAddressModel().getValue();
        BigInteger satoshis = model.getEnterAmountModel().getSatoshis();

        // Form a Bitcoin URI from the contents
        String bitcoinUri = BitcoinURI.convertToBitcoinURI(
          bitcoinAddress,
          satoshis,
          label.getText(),
          null
        );

        displayQRCodeMaV.getModel().setValue(bitcoinUri);
        displayQRCodeMaV.getModel().setLabel(label.getText());

        // Show the QR code as a popover
        Panels.showLightBoxPopover(displayQRCodeMaV.getView().newComponentPanel());
      }

    };
  }
}
