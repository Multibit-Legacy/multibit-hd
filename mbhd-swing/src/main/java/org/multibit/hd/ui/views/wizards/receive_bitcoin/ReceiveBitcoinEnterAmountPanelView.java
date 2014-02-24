package org.multibit.hd.ui.views.wizards.receive_bitcoin;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
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
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetail;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
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
  private JLabel addressCommentLabel;

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

    // See if there is a password entered for the wallet
    // TODO - remove when HDwallets supported - won't need a password to generate the next address
    Optional<WalletData> walletDataOptional = WalletManager.INSTANCE.getCurrentWalletData();
    Optional<CharSequence> passwordParameter = Optional.absent();
    if (walletDataOptional.isPresent()) {
      if (!"".equals(walletDataOptional.get().getPassword())) {
        passwordParameter = Optional.of(walletDataOptional.get().getPassword());
      }
    }
    // Get the next receiving address from the wallet service
    String nextAddress = MultiBitHD.getWalletService().generateNextReceivingAddress(passwordParameter);
    displayBitcoinAddressMaV = Components.newDisplayBitcoinAddressMaV(nextAddress);

    // Create the QR code display
    displayQRCodeMaV = Components.newDisplayQRCodeMaV();

    label = TextBoxes.newEnterLabel();
    showQRCode = Buttons.newQRCodeButton(getShowQRCodePopoverAction());
    addressCommentLabel = Labels.newLabel(MessageKey.ONE_OF_YOUR_ADDRESSES);

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
    panel.add(Labels.newBlankLabel());
    panel.add( addressCommentLabel, "wrap");
    panel.add(Labels.newQRCodeLabelLabel());
    panel.add(label, "span 2,wrap");
    panel.add(Labels.newNotes());
    panel.add(notesTextArea, "span 3,push,wrap");

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
      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {
    savePaymentRequest();
    return true;
  }

  /**
   * Save the displayed payment request
   */
  private void savePaymentRequest() {
    WalletService walletService = MultiBitHD.getWalletService();

    Preconditions.checkNotNull(walletService, "The wallet service was null so cannot save the payment request");

    PaymentRequestData paymentRequestData = new PaymentRequestData();
    paymentRequestData.setNote(notesTextArea.getText());
    paymentRequestData.setDate(DateTime.now());
    paymentRequestData.setAddress(displayBitcoinAddressMaV.getModel().getValue());
    paymentRequestData.setLabel(label.getText());
    paymentRequestData.setAmountBTC(enterAmountMaV.getModel().getSatoshis());
    FiatPayment fiatPayment = new FiatPayment();
    fiatPayment.setAmount(enterAmountMaV.getModel().getLocalAmount().toString());
    fiatPayment.setCurrency(Configurations.currentConfiguration.getI18NConfiguration().getLocalCurrencyUnit().getCurrencyCode());
    fiatPayment.setExchange(Configurations.currentConfiguration.getBitcoinConfiguration().getExchangeName());
    paymentRequestData.setAmountFiat(fiatPayment);

    walletService.addPaymentRequest(paymentRequestData);
    walletService.writePayments();

    // Ensure the views that display payments update
    WalletDetail walletDetail = new WalletDetail();
    if (WalletManager.INSTANCE.getCurrentWalletData().isPresent()) {
      WalletData walletData = WalletManager.INSTANCE.getCurrentWalletData().get();
      walletDetail.setApplicationDirectory(InstallationManager.getOrCreateApplicationDataDirectory().getAbsolutePath());

      File walletFile = WalletManager.INSTANCE.getCurrentWalletFilename().get();
      walletDetail.setWalletDirectory(walletFile.getParentFile().getName());

      ContactService contactService = CoreServices.getOrCreateContactService(Optional.of(walletData.getWalletId()));
      walletDetail.setNumberOfContacts(contactService.allContacts().size());

      walletDetail.setNumberOfPayments(MultiBitHD.getWalletService().getPaymentDatas().size());
      ViewEvents.fireWalletDetailChangedEvent(walletDetail);
    }
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
