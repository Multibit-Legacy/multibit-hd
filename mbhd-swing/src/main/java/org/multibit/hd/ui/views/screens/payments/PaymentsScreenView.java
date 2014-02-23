package org.multibit.hd.ui.views.screens.payments;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.WalletDetailChangedEvent;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.Tables;
import org.multibit.hd.ui.views.components.tables.PaymentTableModel;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Set;

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
            "fill, insets 0", // Layout constraints
            "[]", // Column constraints
            "[]" // Row constraints
    );

    // Create view components
    JPanel contentPanel = Panels.newPanel(layout);

    Set<PaymentData> paymentDatas;
    if (MultiBitHD.getWalletService() == null) {
      paymentDatas = Sets.newHashSet();
    } else {
      paymentDatas = MultiBitHD.getWalletService().getPaymentDatas();
    }
    paymentsTable = Tables.newPaymentsTable(paymentDatas);

    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(paymentsTable);
    scrollPane.setViewportBorder(null);

    // Add to the panel
    contentPanel.add(scrollPane, "grow");

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
      Sounds.playReceiveBitcoin();
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
          ((PaymentTableModel) paymentsTable.getModel()).setPaymentDatas(MultiBitHD.getWalletService().getPaymentDatas(), true);
        }
      });
    }
  }
}
