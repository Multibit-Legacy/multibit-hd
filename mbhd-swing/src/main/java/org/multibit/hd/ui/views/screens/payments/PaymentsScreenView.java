package org.multibit.hd.ui.views.screens.payments;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.events.TransactionCreationEvent;
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
import org.multibit.hd.ui.models.Models;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the payments detail display</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class PaymentsScreenView extends AbstractScreenView<PaymentsScreenModel> {

  private static final Logger log = LoggerFactory.getLogger(PaymentsScreenView.class);

  private JTable paymentsTable;

  private JButton detailsButton;

  private JButton deleteRequestButton;

  private JButton undoButton;

  /**
   * Handles update operations
   */
  private static final ExecutorService executorService = SafeExecutors.newSingleThreadExecutor("payment-update-service");

  // View components
  private ModelAndView<EnterSearchModel, EnterSearchView> enterSearchMaV;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public PaymentsScreenView(PaymentsScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {
  }

  @Override
  public JPanel initialiseScreenViewPanel() {
    MigLayout layout = new MigLayout(
      Panels.migXYDetailLayout(),
      "[][][][][]push[]", // Column constraints
      "[shrink][shrink][grow]" // Row constraints
    );

    // Create view components
    JPanel contentPanel = Panels.newPanel(layout);

    // Create view components
    enterSearchMaV = Components.newEnterSearchMaV(getScreen().name());

    detailsButton = Buttons.newDetailsButton(getDetailsAction());

    deleteRequestButton = Buttons.newDeletePaymentRequestButton(getDeletePaymentRequestAction());
    deleteRequestButton.setEnabled(false);

    undoButton = Buttons.newUndoButton(getUndoAction());
    undoButton.setEnabled(false);

    JButton exportButton = Buttons.newExportButton(getExportAction());

    WalletService walletService = CoreServices.getCurrentWalletService().get();
    Set<PaymentData> paymentSet = walletService.getPaymentDataSet();

    paymentsTable = Tables.newPaymentsTable(paymentSet, detailsButton);

    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(paymentsTable);
    scrollPane.setViewportBorder(null);

    // Detect double clicks on the table
    paymentsTable.addMouseListener(getTableMouseListener());

    // Detect row selection changes
    paymentsTable.getSelectionModel().addListSelectionListener(new TableRowModelListener());

    // Ensure we maintain the overall theme
    ScrollBarUIDecorator.apply(scrollPane, paymentsTable);

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
   * Update the payments when a transactionCreationEvent occurs
   */
  @Subscribe
  public void onTransactionCreationEvent(TransactionCreationEvent transactionCreationEvent) {
    log.trace("Received a TransactionCreationEvent.");
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

  /**
   * Limit access to the internal update process and ensure it runs off the EDT
   *
   * @param refreshData True if the wallet payment data set should be refreshed (expensive)
   */
  private void update(final boolean refreshData) {
    executorService.submit(
      new Runnable() {
        @Override
        public void run() {
          updateInternal(refreshData);
        }
      });
  }

  /**
   * Perform an update of the payment data table off the EDT
   *
   * @param refreshData True if the wallet payment data set should be refreshed (expensive)
   */
  private void updateInternal(final boolean refreshData) {
    if (paymentsTable != null && CoreServices.getCurrentWalletService().isPresent()) {

      WalletService walletService = CoreServices.getCurrentWalletService().get();

      // Refresh the wallet payment list if asked (may have created/deleted a new PaymentRequest)
      if (refreshData) {
        // Avoid logging here - gets called a lot during Repair and floods the logs
        walletService.getPaymentDataSet();
      }
      // Check the search MaV model for a query and apply it
      final List<PaymentData> filteredPaymentDataList = walletService.filterPaymentsByContent(enterSearchMaV.getModel().getValue());

      // Update the payments table model on the EDT to ensure row selection is correctly maintained for FEST tests
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            // Remember the selected row just before the update
            int selectedTableRow = paymentsTable.getSelectedRow();

            // Update the table with the new data
            ((PaymentTableModel) paymentsTable.getModel()).setPaymentData(filteredPaymentDataList, true);

            ((PaymentTableModel) paymentsTable.getModel()).fireTableDataChanged();

            // Reselect the selected row if possible
            if (selectedTableRow != -1 && selectedTableRow < paymentsTable.getModel().getRowCount()) {
              paymentsTable.changeSelection(selectedTableRow, 0, false, false);
            }
            // Update the delete request button
            updateDeleteRequestButton();
          }
        });
    } else {
      log.warn("Unexpected call to Payments without an open wallet");
    }
  }

  /**
   * @return The show transaction details action
   */
  private Action getDetailsAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        WalletService walletService = CoreServices.getCurrentWalletService().get();

        PaymentData paymentData = getPaymentDataForSelectedTableRow();

        if (paymentData == null) {
          return;
        }

        PaymentsWizard wizard = Wizards.newPaymentsWizard(paymentData);
        // If the payment is a transaction, then fetch the matching payment request data and put them in the model
        if (paymentData instanceof TransactionData) {
          wizard.getWizardModel()
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
        CoreServices.getCurrentWalletService().get().undoDeletePaymentData();
        undoButton.setEnabled(CoreServices.getCurrentWalletService().get().canUndo());

        // Update the delete request button
        updateDeleteRequestButton();

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

        PaymentData paymentData = getPaymentDataForSelectedTableRow();

        if (paymentData != null) {
          if (paymentData instanceof MBHDPaymentRequestData) {
            // Enable the undo button
            undoButton.setEnabled(true);

            // We can delete this
            CoreServices.getCurrentWalletService().get().deleteMBHDPaymentRequest((MBHDPaymentRequestData) paymentData);
            fireWalletDetailsChanged();
          } else if (paymentData instanceof PaymentRequestData) {
            // Enable the undo button
            undoButton.setEnabled(true);

            // We can delete this
            CoreServices.getCurrentWalletService().get().deletePaymentRequest((PaymentRequestData) paymentData);
            fireWalletDetailsChanged();
          }
        }

        // Update the delete request button
        PaymentData paymentData2 = getPaymentDataForSelectedTableRow();
        boolean enableDeleteRequestButton = paymentData2 != null && (paymentData2 instanceof PaymentRequestData || paymentData2 instanceof MBHDPaymentRequestData);
        deleteRequestButton.setEnabled(enableDeleteRequestButton);
      }
    };
  }

  private PaymentData getPaymentDataForSelectedTableRow() {
    int selectedTableRow = paymentsTable.getSelectedRow();
    if (selectedTableRow == -1 || selectedTableRow >= paymentsTable.getRowCount() || selectedTableRow >= ((PaymentTableModel) paymentsTable.getModel()).getPaymentDataList().size()) {
      // No row selected or out of bounds due to last payment request delete
      return null;
    }
    int selectedModelRow = paymentsTable.convertRowIndexToModel(selectedTableRow);

    return ((PaymentTableModel) paymentsTable.getModel()).getPaymentDataList().get(selectedModelRow);
  }

  private void updateDeleteRequestButton() {
    PaymentData paymentData = getPaymentDataForSelectedTableRow();
    boolean enableDeleteRequestButton = paymentData != null && (paymentData instanceof PaymentRequestData || paymentData instanceof MBHDPaymentRequestData);
    deleteRequestButton.setEnabled(enableDeleteRequestButton);
  }

  /**
   * @return The table mouse listener
   */
  private MouseAdapter getTableMouseListener() {
    return new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        // If user clicks a payment request enable the the delete button, otherwise disable it
        updateDeleteRequestButton();

        // If user double clicks then show the details
        if (e.getClickCount() == 2) {
          detailsButton.doClick();
        }
      }
    };
  }

  private void fireWalletDetailsChanged() {
    // Ensure the views that display payments update
    final WalletDetail walletDetail = new WalletDetail();
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();
      walletDetail.setApplicationDirectory(InstallationManager.getOrCreateApplicationDataDirectory().getAbsolutePath());

      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
      File walletFile = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get();
      walletDetail.setWalletDirectory(walletFile.getParentFile().getName());

      ContactService contactService = CoreServices.getOrCreateContactService(walletSummary.getWalletPassword());
      walletDetail.setNumberOfContacts(contactService.allContacts().size());

      walletDetail.setNumberOfPayments(CoreServices.getCurrentWalletService().get().getPaymentDataSetSize());

      ViewEvents.fireWalletDetailChangedEvent(walletDetail);
    }
  }

  class TableRowModelListener implements ListSelectionListener {

    public TableRowModelListener() {
    }

    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        if (!lsm.isSelectionEmpty()) {
          // If user clicks a payment request enable the the delete button, otherwise disable it
          updateDeleteRequestButton();
        }
      }
    }
  }
}
