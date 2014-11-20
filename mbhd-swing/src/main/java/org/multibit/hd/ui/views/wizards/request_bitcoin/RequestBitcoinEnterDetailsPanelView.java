package org.multibit.hd.ui.views.wizards.request_bitcoin;

import org.bitcoinj.core.Coin;
import org.bitcoinj.uri.BitcoinURI;
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
import org.multibit.hd.core.utils.Addresses;
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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Currency;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Request bitcoin: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */

public class RequestBitcoinEnterDetailsPanelView extends AbstractWizardPanelView<RequestBitcoinWizardModel, RequestBitcoinEnterDetailsPanelModel> {

  // Panel specific components
  private JTextArea notesTextArea;

  private ModelAndView<EnterAmountModel, EnterAmountView> enterAmountMaV;
  private ModelAndView<DisplayBitcoinAddressModel, DisplayBitcoinAddressView> displayBitcoinAddressMaV;
  private ModelAndView<DisplayQRCodeModel, DisplayQRCodeView> displayQRCodePopoverMaV;

  private JTextField transactionLabel;
  private JLabel addressCommentLabel;

  private JButton showQRCode;

  /**
   * @param wizard The wizard managing the states
   */
  public RequestBitcoinEnterDetailsPanelView(AbstractWizard<RequestBitcoinWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.REQUEST_BITCOIN_TITLE, AwesomeIcon.CLOUD_DOWNLOAD);

  }

  @Override
  public void newPanelModel() {

    enterAmountMaV = Components.newEnterAmountMaV(getPanelName());

    // See if there is a credentials entered for the wallet
    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    Optional<CharSequence> passwordParameter = Optional.absent();
    CharSequence password = currentWalletSummary.get().getPassword();
    if (currentWalletSummary.isPresent()) {
      if (!(password == null) && !"".equals(password)) {
        passwordParameter = Optional.of(password);
      }
    }
    // Get the next receiving address from the wallet service
    String nextAddress = CoreServices.getCurrentWalletService().generateNextReceivingAddress(passwordParameter);

    // Recreate bloom filter
    BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    Preconditions.checkState(bitcoinNetworkService.isStartedOk(), "'bitcoinNetworkService' should be started OK");
    bitcoinNetworkService.recalculateFastCatchupAndFilter();

    displayBitcoinAddressMaV = Components.newDisplayBitcoinAddressMaV(nextAddress);

    // Create the QR code display
    displayQRCodePopoverMaV = Popovers.newDisplayQRCodePopoverMaV(getPanelName());

    transactionLabel = TextBoxes.newEnterQRCodeLabel();
    showQRCode = Buttons.newQRCodeButton(getShowQRCodePopoverAction());
    addressCommentLabel = Labels.newLabel(MessageKey.ONE_OF_YOUR_ADDRESSES);

    // User entered text
    notesTextArea = TextBoxes.newEnterPrivateNotes(getWizardModel(), MultiBitUI.RECEIVE_ADDRESS_LABEL_LENGTH);

    // Configure the panel model
    setPanelModel(new RequestBitcoinEnterDetailsPanelModel(
      getPanelName(),
      enterAmountMaV.getModel(),
      displayBitcoinAddressMaV.getModel()
    ));

    getWizardModel().setEnterAmountModel(enterAmountMaV.getModel());
    getWizardModel().setTransactionLabel(transactionLabel.getText());
    getWizardModel().setNotes(Optional.of(notesTextArea.getText()));

    // Register components
    registerComponents(enterAmountMaV, displayBitcoinAddressMaV, displayQRCodePopoverMaV);

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
    contentPanel.add(transactionLabel, "span 2,wrap");
    contentPanel.add(Labels.newNotes());
    contentPanel.add(notesTextArea, "span 3,push,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<RequestBitcoinWizardModel> wizard) {

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
        enterAmountMaV.getView().requestInitialFocus();
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

    log.debug("Saving payment request");

    WalletService walletService = CoreServices.getCurrentWalletService();

    // Fail fast
    Preconditions.checkNotNull(walletService, "'walletService' must be present");
    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().isPresent(), "'currentWalletSummary' must be present");

    final PaymentRequestData paymentRequestData = new PaymentRequestData();
    paymentRequestData.setNote(notesTextArea.getText());
    paymentRequestData.setDate(DateTime.now());
    paymentRequestData.setAddress(Addresses.parse(displayBitcoinAddressMaV.getModel().getValue()).get());
    paymentRequestData.setLabel(transactionLabel.getText());
    paymentRequestData.setAmountCoin(enterAmountMaV.getModel().getCoinAmount());

    final FiatPayment fiatPayment = new FiatPayment();
    fiatPayment.setAmount(enterAmountMaV.getModel().getLocalAmount());

    final ExchangeKey exchangeKey = ExchangeKey.current();
    fiatPayment.setExchangeName(Optional.of(exchangeKey.getExchangeName()));

    final Optional<ExchangeRateChangedEvent> exchangeRateChangedEvent = CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent();
    if (exchangeRateChangedEvent.isPresent()) {
      fiatPayment.setRate(Optional.of(exchangeRateChangedEvent.get().getRate().toString()));
      fiatPayment.setCurrency(Optional.of(exchangeRateChangedEvent.get().getCurrency()));
    } else {
      fiatPayment.setRate(Optional.<String>absent());
      fiatPayment.setCurrency(Optional.<Currency>absent());
    }

    paymentRequestData.setAmountFiat(fiatPayment);

    walletService.addPaymentRequest(paymentRequestData);
    try {
      log.debug("Saving payment information");
      walletService.writePayments();
    } catch (PaymentsSaveException pse) {
      ExceptionHandler.handleThrowable(pse);
    }

    // Ensure the views that display payments update through a "wallet detail changed" event
    final WalletDetail walletDetail = new WalletDetail();

    final File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    final File walletFile = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get();

    final WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();
    ContactService contactService = CoreServices.getOrCreateContactService(walletSummary.getWalletId());

    walletDetail.setApplicationDirectory(applicationDataDirectory.getAbsolutePath());
    walletDetail.setWalletDirectory(walletFile.getParentFile().getName());
    walletDetail.setNumberOfContacts(contactService.allContacts().size());
    walletDetail.setNumberOfPayments(walletService.getPaymentDataList().size());

    ViewEvents.fireWalletDetailChangedEvent(walletDetail);

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

        RequestBitcoinEnterDetailsPanelModel model = getPanelModel().get();

        String bitcoinAddress = model.getDisplayBitcoinAddressModel().getValue();
        Coin coin = model.getEnterAmountModel().getCoinAmount();

        // Form a Bitcoin URI from the contents
        String bitcoinUri = BitcoinURI.convertToBitcoinURI(
          bitcoinAddress,
          coin,
          transactionLabel.getText(),
          null
        );

        displayQRCodePopoverMaV.getModel().setValue(bitcoinUri);
        displayQRCodePopoverMaV.getModel().setTransactionLabel(transactionLabel.getText());

        // Show the QR code as a popover
        Panels.showLightBoxPopover(displayQRCodePopoverMaV.getView().newComponentPanel());
      }

    };
  }
}
