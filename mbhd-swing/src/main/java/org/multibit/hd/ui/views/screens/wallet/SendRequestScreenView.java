package org.multibit.hd.ui.views.screens.wallet;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.BitcoinNetworkStatus;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.PaymentType;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.WalletDetailChangedEvent;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_payments.DisplayPaymentsModel;
import org.multibit.hd.ui.views.components.display_payments.DisplayPaymentsView;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the wallet detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendRequestScreenView extends AbstractScreenView<SendRequestScreenModel> {

  private static final Logger log = LoggerFactory.getLogger(SendRequestScreenView.class);


  private JButton sendBitcoin;
  private JButton requestBitcoin;

  private ModelAndView<DisplayPaymentsModel, DisplayPaymentsView> displaySendingPaymentsMaV;

  private ModelAndView<DisplayPaymentsModel, DisplayPaymentsView> displayReceivingPaymentsMaV;

  private WalletService walletService;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public SendRequestScreenView(SendRequestScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {

    // Only register when there is something to do
    CoreServices.uiEventBus.register(this);

    walletService = CoreServices.getCurrentWalletService();

    MigLayout layout = new MigLayout(
      Panels.migXYLayout(),
      "10[]10[]", // Column constraints
      "1[]20[]10[]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    Action showSendBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        SendBitcoinParameter parameter = new SendBitcoinParameter(Optional.<BitcoinURI>absent(), false);

        Panels.showLightBox(Wizards.newSendBitcoinWizard(parameter).getWizardScreenHolder());
      }
    };

    Action showRequestBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newRequestBitcoinWizard().getWizardScreenHolder());
      }
    };

    sendBitcoin = Buttons.newSendBitcoinWizardButton(showSendBitcoinWizardAction);

    requestBitcoin = Buttons.newRequestBitcoinWizardButton(showRequestBitcoinWizardAction);

    if (InstallationManager.unrestricted) {
      // Start with enabled buttons and use Bitcoin network status to modify
      sendBitcoin.setEnabled(true);
      requestBitcoin.setEnabled(true);
    } else {
      // Start with disabled button and use Bitcoin network status to modify
      sendBitcoin.setEnabled(false);
      requestBitcoin.setEnabled(false);
    }
    // Initialise panel with a blank list of today's sending payments
    List<PaymentData> todaysSendingPayments = Lists.newArrayList(); // walletService.subsetPaymentsAndSort(allPayments, PaymentType.SENDING);
    displaySendingPaymentsMaV = Components.newDisplayPaymentsMaV(getScreen().name());
    displaySendingPaymentsMaV.getModel().setValue(todaysSendingPayments);
    JScrollPane sendingPaymentsScrollPane = new JScrollPane(displaySendingPaymentsMaV.getView().newComponentPanel(),
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    sendingPaymentsScrollPane.setBackground(Themes.currentTheme.detailPanelBackground());
    sendingPaymentsScrollPane.getViewport().setBackground(Themes.currentTheme.detailPanelBackground());
    sendingPaymentsScrollPane.setOpaque(true);
    sendingPaymentsScrollPane.setBorder(BorderFactory.createEmptyBorder());

    // Initialise panel with a blank list of today's receiving payments
    List<PaymentData> todaysReceivingPayments = Lists.newArrayList(); //walletService.subsetPaymentsAndSort(allPayments, PaymentType.RECEIVING);
    displayReceivingPaymentsMaV = Components.newDisplayPaymentsMaV(getScreen().name());
    displayReceivingPaymentsMaV.getModel().setValue(todaysReceivingPayments);

    JScrollPane receivingPaymentsScrollPane = new JScrollPane(displayReceivingPaymentsMaV.getView().newComponentPanel(),
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    receivingPaymentsScrollPane.getViewport().setBackground(Themes.currentTheme.detailPanelBackground());
    receivingPaymentsScrollPane.setOpaque(true);
    receivingPaymentsScrollPane.setBorder(BorderFactory.createEmptyBorder());

    contentPanel.add(Labels.newBlankLabel(), "width 47%, pushx");
    contentPanel.add(Labels.newBlankLabel(), "width 6%, pushx");
    contentPanel.add(Labels.newBlankLabel(), "width 47%, pushx, wrap");

    contentPanel.add(sendBitcoin, MultiBitUI.LARGE_BUTTON_MIG + ",align center");
    contentPanel.add(Panels.newVerticalDashedSeparator(), "growy,spany 2");
    contentPanel.add(requestBitcoin, MultiBitUI.LARGE_BUTTON_MIG + ",align center, wrap");

    contentPanel.add(sendingPaymentsScrollPane, "grow, push");
    contentPanel.add(receivingPaymentsScrollPane, "grow, push, wrap");

    return contentPanel;
  }

  @Override
  public boolean beforeShow() {
    return true;
  }

  @Override
  public void afterShow() {
    update();
  }

  @Subscribe
  public void onBitcoinNetworkChangeEvent(final BitcoinNetworkChangedEvent event) {

    log.trace("Received 'Bitcoin network changed' event");

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

    BitcoinNetworkSummary summary = event.getSummary();

    Preconditions.checkNotNull(summary.getSeverity(), "'severity' must be present");
    Preconditions.checkNotNull(summary.getMessageKey(), "'errorKey' must be present");
    Preconditions.checkNotNull(summary.getMessageData(), "'errorData' must be present");

    if (!isInitialised()) {
      return;
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // NOTE: Both send and request are disabled when the network is not available
        // because it is possible that a second wallet is generating transactions using
        // addresses that this one has not displayed yet. This would lead to the same
        // address being used twice.
        switch (event.getSummary().getSeverity()) {
          case RED:
            // Always disabled on RED
            sendBitcoin.setEnabled(false);
            requestBitcoin.setEnabled(false);

            break;
          case AMBER:
            if (InstallationManager.unrestricted) {
              // Enable on AMBER if unrestricted
              sendBitcoin.setEnabled(true);
              requestBitcoin.setEnabled(true);
            } else {

              // Disable on AMBER in production
              sendBitcoin.setEnabled(false);
              requestBitcoin.setEnabled(false);
            }
            break;
          case GREEN:
            if (BitcoinNetworkStatus.SYNCHRONIZED.equals(event.getSummary().getStatus())) {

              // Always enabled on GREEN and fully synchronized
              sendBitcoin.setEnabled(true);
              requestBitcoin.setEnabled(true);

            } else {

              // Enable if unrestricted to assist testing
              if (InstallationManager.unrestricted) {
                sendBitcoin.setEnabled(true);
                requestBitcoin.setEnabled(true);
              } else {
                sendBitcoin.setEnabled(false);
                requestBitcoin.setEnabled(false);
              }

            }
            break;
          default:
            // Unknown status
            throw new IllegalStateException("Unknown event severity " + event.getSummary().getStatus());
        }

      }
    });
  }

  @Subscribe
  public void onExchangeRateChangedEvent(ExchangeRateChangedEvent exchangeRateChangedEvent) {
    update();
  }

  @Subscribe
  public void onSlowTransactionSeenEvent(SlowTransactionSeenEvent slowTransactionSeenEvent) {
    update();
  }

  /**
   * Update the payments when a walletDetailsChangedEvent occurs
   */
  @Subscribe
  public void onWalletDetailChangedEvent(WalletDetailChangedEvent walletDetailChangedEvent) {
    update();
  }

  private void update() {
    if (isInitialised()) {

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          List<PaymentData> allPayments = walletService.getPaymentDataList();

          // Find the 'Sending' transactions for today
          List<PaymentData> todaysSendingPayments = walletService.subsetPaymentsAndSort(allPayments, PaymentType.SENDING);
          displaySendingPaymentsMaV.getModel().setValue(todaysSendingPayments);

          // Find the receiving events for today
          List<PaymentData> todaysReceivingPayments = walletService.subsetPaymentsAndSort(allPayments, PaymentType.RECEIVING);
          displayReceivingPaymentsMaV.getModel().setValue(todaysReceivingPayments);

          displaySendingPaymentsMaV.getView().createView();
          displaySendingPaymentsMaV.getView().updateView();

          displayReceivingPaymentsMaV.getView().createView();
          displayReceivingPaymentsMaV.getView().updateView();
          sendBitcoin.requestFocusInWindow();

        }
      });
    } else {
      log.debug("Not updating recent payments as panel is not initialised");
    }
  }
}
