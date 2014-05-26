package org.multibit.hd.ui.views.wizards.receive_bitcoin;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exceptions.PaymentsSaveException;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressModel;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressView;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeModel;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeView;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountView;
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
  private ModelAndView<DisplayQRCodeModel, DisplayQRCodeView> displayQRCodePopoverMaV;

  private JTextField label;
  private JLabel addressCommentLabel;

  private JButton showQRCode;

  /**
   * @param wizard The wizard managing the states
   */
  public ReceiveBitcoinEnterAmountPanelView(AbstractWizard<ReceiveBitcoinWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.RECEIVE_BITCOIN_TITLE, AwesomeIcon.CLOUD_DOWNLOAD);

  }

  @Override
  public void newPanelModel() {

    enterAmountMaV = Components.newEnterAmountMaV(getPanelName());

    // See if there is a password entered for the wallet
    // TODO - remove when HDwallets supported - won't need a password to generate the next address
    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    Optional<CharSequence> passwordParameter = Optional.absent();
    CharSequence password = currentWalletSummary.get().getPassword();
    if (currentWalletSummary.isPresent()) {
      if (!( password == null) && !"".equals(password)) {
        passwordParameter = Optional.of(password);
      }
    }
    // Get the next receiving address from the wallet service
    String nextAddress = CoreServices.getCurrentWalletService().generateNextReceivingAddress(passwordParameter);

    // Recreate bloom filter
    BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    Preconditions.checkState(bitcoinNetworkService.isStartedOk(),"'bitcoinNetworkService' should be started OK");
    bitcoinNetworkService.recalculateFastCatchupAndFilter();

    displayBitcoinAddressMaV = Components.newDisplayBitcoinAddressMaV(nextAddress);

    // Create the QR code display
    displayQRCodePopoverMaV = Popovers.newDisplayQRCodePopoverMaV(getPanelName());

    label = TextBoxes.newEnterLabel();
    showQRCode = Buttons.newQRCodeButton(getShowQRCodePopoverAction());
    addressCommentLabel = Labels.newLabel(MessageKey.ONE_OF_YOUR_ADDRESSES);

    // User entered text
    notesTextArea = TextBoxes.newEnterPrivateNotes(getWizardModel(), MultiBitUI.RECEIVE_ADDRESS_LABEL_LENGTH);

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
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[]10[]" // Row constraints
    ));

    contentPanel.add(enterAmountMaV.getView().newComponentPanel(), "span 3,wrap");
    contentPanel.add(Labels.newRecipient());
    contentPanel.add(displayBitcoinAddressMaV.getView().newComponentPanel(), "growx,push");
    contentPanel.add(showQRCode, "wrap");
    contentPanel.add(Labels.newBlankLabel());
    contentPanel.add(addressCommentLabel, "wrap");
    contentPanel.add(Labels.newQRCodeLabel());
    contentPanel.add(label, "span 2,wrap");
    contentPanel.add(Labels.newNotes());
    contentPanel.add(notesTextArea, "span 3,push,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ReceiveBitcoinWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

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

    // Always call super() before hiding
    super.beforeHide(isExitCancel);

    savePaymentRequest();

    return true;
  }

  /**
   * Save the displayed payment request
   */
  private void savePaymentRequest() {

    WalletService walletService = CoreServices.getCurrentWalletService();

    Preconditions.checkNotNull(walletService, "The wallet service was null so cannot save the payment request");

    PaymentRequestData paymentRequestData = new PaymentRequestData();
    paymentRequestData.setNote(notesTextArea.getText());
    paymentRequestData.setDate(DateTime.now());
    paymentRequestData.setAddress(displayBitcoinAddressMaV.getModel().getValue());
    paymentRequestData.setLabel(label.getText());
    paymentRequestData.setAmountBTC(enterAmountMaV.getModel().getSatoshis());

    FiatPayment fiatPayment = new FiatPayment();
    fiatPayment.setAmount(Optional.of(enterAmountMaV.getModel().getLocalAmount()));

    ExchangeKey exchangeKey = ExchangeKey.current();
    fiatPayment.setExchangeName(exchangeKey.getExchangeName());

    Optional<ExchangeRateChangedEvent> exchangeRateChangedEvent = CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent();
    if (exchangeRateChangedEvent.isPresent()) {
      fiatPayment.setRate(exchangeRateChangedEvent.get().getRate().toString());
    } else {
      fiatPayment.setRate("");
    }
    paymentRequestData.setAmountFiat(fiatPayment);

    walletService.addPaymentRequest(paymentRequestData);
    try {
      walletService.writePayments();
    } catch (PaymentsSaveException pse) {
      ExceptionHandler.handleThrowable(pse);
    }
    // Ensure the views that display payments update
    WalletDetail walletDetail = new WalletDetail();
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();
      walletDetail.setApplicationDirectory(InstallationManager.getOrCreateApplicationDataDirectory().getAbsolutePath());

      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
      File walletFile = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get();
      walletDetail.setWalletDirectory(walletFile.getParentFile().getName());

      ContactService contactService = CoreServices.getOrCreateContactService(walletSummary.getWalletId());
      walletDetail.setNumberOfContacts(contactService.allContacts().size());

      walletDetail.setNumberOfPayments(walletService.getPaymentDataList().size());
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

        displayQRCodePopoverMaV.getModel().setValue(bitcoinUri);
        displayQRCodePopoverMaV.getModel().setLabel(label.getText());

        // Show the QR code as a popover
        Panels.showLightBoxPopover(displayQRCodePopoverMaV.getView().newComponentPanel());
      }

    };
  }
}
