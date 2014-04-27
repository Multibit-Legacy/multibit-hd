package org.multibit.hd.ui.views.screens.payments;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WalletDetailChangedEvent;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchModel;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchView;
import org.multibit.hd.ui.views.components.tables.PaymentTableModel;
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetail;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.export_payments.ExportPaymentsWizardState;
import org.multibit.hd.ui.views.wizards.payments.PaymentsWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.List;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the payments detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PaymentsScreenView extends AbstractScreenView<PaymentsScreenModel> {

  private static final Logger log = LoggerFactory.getLogger(PaymentsScreenView.class);

  private JTable paymentsTable;

  // View components
  private ModelAndView<EnterSearchModel, EnterSearchView> enterSearchMaV;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public PaymentsScreenView(PaymentsScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);

    // Register for transaction seen events so it can update.
    CoreServices.uiEventBus.register(this);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      Panels.migLayout("fill,insets 10 5 0 0"),
      "[][][][][]push[]", // Column constraints
      "[shrink][shrink][grow]" // Row constraints
    );

    // Create view components
    JPanel contentPanel = Panels.newPanel(layout);

    // Create view components
    enterSearchMaV = Components.newEnterSearchMaV(getScreen().name());

    final JButton detailsButton = Buttons.newDetailsButton(getDetailsAction());
    final JButton deleteRequestButton = Buttons.newDeletePaymentRequestButton(getDeletePaymentRequestAction());
    JButton undoButton = Buttons.newUndoButton(getUndoAction());
    JButton exportButton = Buttons.newExportButton(getExportAction());

    WalletService walletService = CoreServices.getCurrentWalletService();
    List<PaymentData> paymentList = walletService.getPaymentDataList();

    paymentsTable = Tables.newPaymentsTable(paymentList);

    paymentsTable.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        // User is most likely to want the details button after losing table focus
        detailsButton.requestFocusInWindow();
      }
    });


    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(paymentsTable);
    scrollPane.setViewportBorder(null);

    // Add to the panel
    contentPanel.add(enterSearchMaV.getView().newComponentPanel(), "span 6,growx,push,wrap");
    contentPanel.add(detailsButton, "shrink");
    contentPanel.add(exportButton, "shrink");
    contentPanel.add(deleteRequestButton, "shrink");
    contentPanel.add(undoButton, "shrink");
    contentPanel.add(Labels.newBlankLabel(), "growx,push,wrap"); // Empty label to pack buttons

    contentPanel.add(scrollPane, "span 6, grow, push");

    return contentPanel;
  }

  @Subscribe
  public void onTransactionSeenEvent(TransactionSeenEvent transactionSeenEvent) {
    log.trace("Received the TransactionSeenEvent: " + transactionSeenEvent.toString());

    // Play a sound the first time a transaction is received
    // TODO some more filtering required - just set to play when it confirms for the first time for now
    if (transactionSeenEvent.getDepthInBlocks() == 1) {
      //  && transactionSeenEvent.getValue() != null && transactionSeenEvent.getValue().compareTo(BigInteger.ZERO) >0
      Sounds.playReceiveBitcoin();
    }

    if (transactionSeenEvent.isFirstAppearanceInWallet()) {
      AlertModel alertModel = new AlertModel("A new payment has been received.", RAGStatus.PINK);
      ControllerEvents.fireAddAlertEvent(alertModel);
    }
  }

  /**
   * Update the payments when a slowTransactionSeenEvent occurs
   */
  @Subscribe
  public void onSlowTransactionSeenEvent(SlowTransactionSeenEvent slowTransactionSeenEvent) {
    log.trace("Received a SlowTransactionSeenEvent.");

    update(true);
  }

  /**
   * Update the payments when a walletDetailsChangedEvent occurs
   */
  @Subscribe
  public void onWalletDetailChangedEvent(WalletDetailChangedEvent walletDetailChangedEvent) {
    log.trace("Received a WalletDetailsChangedEvent.");

    update(true);
  }

  /**
   * <p>Called when the search box is updated</p>
   *
   * @param event The "component changed" event
   */
  @Subscribe
  public void onComponentChangedEvent(ComponentChangedEvent event) {

    // Check if this event applies to us
    if (event.getPanelName().equals(getScreen().name())) {
      update(false);
    }

  }

  private void update(final boolean refreshData) {

    if (paymentsTable != null) {

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          WalletService walletService = CoreServices.getCurrentWalletService();

          // Refresh the wallet payment list if asked
          if (refreshData) {
            walletService.getPaymentDataList();
          }
          // Check the search MaV model for a query and apply it
          List<PaymentData> filteredPaymentDataList = walletService.filterPaymentsByContent(enterSearchMaV.getModel().getValue());

          ((PaymentTableModel) paymentsTable.getModel()).setPaymentData(filteredPaymentDataList, true);
        }
      });
    }

  }

  /**
   * @return The show transaction details action
   */
  private Action getDetailsAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        WalletService walletService = CoreServices.getCurrentWalletService();

        int selectedTableRow = paymentsTable.getSelectedRow();

        if (selectedTableRow == -1) {
          // No row selected
          return;
        }
        int selectedModelRow = paymentsTable.convertRowIndexToModel(selectedTableRow);
        PaymentData paymentData = ((PaymentTableModel) paymentsTable.getModel()).getPaymentData().get(selectedModelRow);
        //log.debug("getDetailsAction : selectedTableRow = " + selectedTableRow + ", selectedModelRow = " + selectedModelRow + ", paymentData = " + paymentData.toString());

        PaymentsWizard wizard = Wizards.newPaymentsWizard(paymentData);
        // If the payment is a transaction, then fetch the matching payment request data and put them in the model
        if (paymentData instanceof TransactionData) {
          wizard
            .getWizardModel()
            .setMatchingPaymentRequestList(
              walletService
                .findPaymentRequestsThisTransactionFunds((TransactionData) paymentData)
            );
        }
        Panels.showLightBox(wizard.getWizardScreenHolder());
      }
    };
  }

  /**
   * @return The export transaction details action
   */
  private Action getExportAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        log.debug("getExportAction called");
        Panels.showLightBox(Wizards.newExportPaymentsWizard(ExportPaymentsWizardState.SELECT_EXPORT_LOCATION).getWizardScreenHolder());
      }
    };
  }

  /**
   * @return The undo details action
   */
  private Action getUndoAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        CoreServices.getCurrentWalletService().undoDeletePaymentRequest();
        fireWalletDetailsChanged();

      }
    };
  }

  /**
   * @return The delete payment request  action
   */
  private Action getDeletePaymentRequestAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int selectedTableRow = paymentsTable.getSelectedRow();
        if (selectedTableRow == -1) {
          // No row selected
          return;
        }
        int selectedModelRow = paymentsTable.convertRowIndexToModel(selectedTableRow);
        log.debug("getExportAction : selectedTableRow = " + selectedTableRow + ", selectedModelRow = " + selectedModelRow);

        PaymentData paymentData = ((PaymentTableModel) paymentsTable.getModel()).getPaymentData().get(selectedModelRow);

        if (paymentData instanceof PaymentRequestData) {
          // We can delete this
          CoreServices.getCurrentWalletService().deletePaymentRequest((PaymentRequestData) paymentData);
          fireWalletDetailsChanged();
        }
      }
    };
  }

  private void fireWalletDetailsChanged() {

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

      walletDetail.setNumberOfPayments(CoreServices.getCurrentWalletService().getPaymentDataList().size());
      ViewEvents.fireWalletDetailChangedEvent(walletDetail);

    }
  }
}
