package org.multibit.hd.ui.views.screens.wallet;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.PaymentType;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.WalletDetailChangedEvent;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_payments.DisplayPaymentsModel;
import org.multibit.hd.ui.views.components.display_payments.DisplayPaymentsView;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;

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
public class WalletScreenView extends AbstractScreenView<WalletScreenModel> {

  private JButton sendBitcoin;

  private ModelAndView<DisplayPaymentsModel, DisplayPaymentsView> displaySendingPaymentsMaV;

  private ModelAndView<DisplayPaymentsModel, DisplayPaymentsView> displayReceivingPaymentsMaV;

  private WalletService walletService;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public WalletScreenView(WalletScreenModel panelModel, Screen screen, MessageKey title) {
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
      "20[]10[]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    Action showSendBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newSendBitcoinWizard(Optional.<BitcoinURI>absent()).getWizardScreenHolder());
      }
    };

    Action showRequestBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newRequestBitcoinWizard().getWizardScreenHolder());
      }
    };

    sendBitcoin = Buttons.newSendBitcoinWizardButton(showSendBitcoinWizardAction);
    JButton requestBitcoin = Buttons.newRequestBitcoinWizardButton(showRequestBitcoinWizardAction);

    List<PaymentData> allPayments = walletService.getPaymentDataList();
    // Find the 'Sending' transactions for today
    List<PaymentData> todaysSendingPayments = walletService.subsetPaymentsAndSort(allPayments, PaymentType.SENDING);
    displaySendingPaymentsMaV = Components.newDisplayPaymentsMaV(getScreen().name());
    displaySendingPaymentsMaV.getModel().setValue(todaysSendingPayments);
    JScrollPane sendingPaymentsScrollPane = new JScrollPane(displaySendingPaymentsMaV.getView().newComponentPanel(),
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    sendingPaymentsScrollPane.setBackground(Themes.currentTheme.detailPanelBackground());
    sendingPaymentsScrollPane.getViewport().setBackground(Themes.currentTheme.detailPanelBackground());
    sendingPaymentsScrollPane.setOpaque(true);
    sendingPaymentsScrollPane.setBorder(BorderFactory.createEmptyBorder());

    // Find the 'Receiving' (+ requested) transactions for today
    List<PaymentData> todaysReceivingPayments = walletService.subsetPaymentsAndSort(allPayments, PaymentType.RECEIVING);
    displayReceivingPaymentsMaV = Components.newDisplayPaymentsMaV(getScreen().name());
    displayReceivingPaymentsMaV.getModel().setValue(todaysReceivingPayments);

    JScrollPane receivingPaymentsScrollPane = new JScrollPane(displayReceivingPaymentsMaV.getView().newComponentPanel(),
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    receivingPaymentsScrollPane.getViewport().setBackground(Themes.currentTheme.detailPanelBackground());
    receivingPaymentsScrollPane.setOpaque(true);
    receivingPaymentsScrollPane.setBorder(BorderFactory.createEmptyBorder());

    contentPanel.add(sendBitcoin, MultiBitUI.LARGE_BUTTON_MIG + ",align center");
    contentPanel.add(Panels.newVerticalDashedSeparator(), "growy, spany 2");
    contentPanel.add(requestBitcoin, MultiBitUI.LARGE_BUTTON_MIG + ",align center, wrap");

    contentPanel.add(sendingPaymentsScrollPane, "grow, push");
    contentPanel.add(receivingPaymentsScrollPane, "grow, push, wrap");

    return contentPanel;
  }

  @Subscribe
  public void onSlowTransactionSeenEvent(SlowTransactionSeenEvent slowTransactionSeenEvent) {
    update(true);
  }

  /**
   * Update the payments when a walletDetailsChangedEvent occurs
   */
  @Subscribe
  public void onWalletDetailChangedEvent(WalletDetailChangedEvent walletDetailChangedEvent) {
    update(true);
  }

  private void update(final boolean refreshData) {

    if (isInitialised()) {

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          if (refreshData) {
            List<PaymentData> allPayments = walletService.getPaymentDataList();
            // Find the 'Sending' transactions for today
            List<PaymentData> todaysSendingPayments = walletService.subsetPaymentsAndSort(allPayments, PaymentType.SENDING);
            displaySendingPaymentsMaV.getModel().setValue(todaysSendingPayments);

            List<PaymentData> todaysReceivingPayments = walletService.subsetPaymentsAndSort(allPayments, PaymentType.RECEIVING);
            displayReceivingPaymentsMaV.getModel().setValue(todaysReceivingPayments);

          }

          displaySendingPaymentsMaV.getView().createView();
          displaySendingPaymentsMaV.getView().updateView();

          displayReceivingPaymentsMaV.getView().createView();
          displayReceivingPaymentsMaV.getView().updateView();
          sendBitcoin.requestFocusInWindow();

        }
      });
    }
  }
}
