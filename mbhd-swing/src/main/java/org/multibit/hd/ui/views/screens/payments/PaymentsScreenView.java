package org.multibit.hd.ui.views.screens.payments;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.WalletDetailChangedEvent;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchModel;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchView;
import org.multibit.hd.ui.views.components.tables.PaymentTableModel;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
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
  public JPanel newScreenViewPanel() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      Panels.migLayout("fill,insets 10 5 0 0"),
      "[][][][][]push[]", // Column constraints
      "[]" // Row constraints
    );

    // Create view components
    JPanel contentPanel = Panels.newPanel(layout);

      // Create view components
    enterSearchMaV = Components.newEnterSearchMaV(getScreen().name());

    JButton detailsButton = Buttons.newDetailsButton(getDetailsAction());
    JButton deleteRequestButton = Buttons.newDeletePaymentRequestButton(getDeletePaymentRequestAction());
    JButton undoButton = Buttons.newUndoButton(getUndoAction());
    JButton exportButton = Buttons.newExportButton(getExportAction());

    List<PaymentData> paymentDatas;
    if (MultiBitHD.getWalletService() == null) {
      paymentDatas = Lists.newArrayList();
    } else {
      paymentDatas = MultiBitHD.getWalletService().getPaymentDatas();
    }
    paymentsTable = Tables.newPaymentsTable(paymentDatas);

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

    contentPanel.add(scrollPane, "span 6, grow");

    return contentPanel;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        // TODO Set the search as the focus;
      }
    });

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

    update();
  }

  /**
   * Update the payments when a walletDetailsChangedEvent occurs
   */
  @Subscribe
  public void onWalletDetailChangedEvent(WalletDetailChangedEvent walletDetailChangedEvent) {
    log.trace("Received a WalletDetailsChangedEvent.");

    update();
  }

  private void update() {
    if (paymentsTable != null) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          ((PaymentTableModel) paymentsTable.getModel()).setPaymentData(MultiBitHD.getWalletService().getPaymentDatas(), true);
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
         int selectedTableRow = paymentsTable.getSelectedRow();

         if (selectedTableRow == -1) {
           // No row selected
           return;
         }
         int selectedModelRow = paymentsTable.convertRowIndexToModel(selectedTableRow);
         log.debug("getDetailsAction : selectedTableRow = " + selectedTableRow + ", selectedModelRow = " + selectedModelRow);
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
          MultiBitHD.getWalletService().undoDeletePaymentRequest();
          update();
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

          PaymentData paymentData = ((PaymentTableModel)paymentsTable.getModel()).getPaymentData().get(selectedModelRow);

          if (paymentData instanceof PaymentRequestData) {
            // We can delete this
            MultiBitHD.getWalletService().deletePaymentRequest((PaymentRequestData)paymentData);
            update();
          }
        }
      };
    }
}
