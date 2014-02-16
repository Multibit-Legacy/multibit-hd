package org.multibit.hd.ui.views.screens.transactions;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.Tables;
import org.multibit.hd.ui.views.components.tables.TransactionTableModel;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the transactions detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class TransactionsPanelView extends AbstractScreenView<TransactionsPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(TransactionsPanelView.class);

  private WalletService walletService;
  private JTable transactionsTable;

  private long lastSeenEventTime = 0;
  private static final long CONSOLIDATION_INTERVAL = 1000; // milliseconds
  private boolean waitingToFire = false;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public TransactionsPanelView(TransactionsPanelModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);

    // Register for transaction seen events so it can update.
    CoreServices.uiEventBus.register(this);

    walletService = CoreServices.newWalletService();
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

    transactionsTable = Tables.newTransactionsTable(walletService.getTransactions());

    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(transactionsTable);
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
    log.debug("Received the TransactionSeenEvent: " + transactionSeenEvent.toString());

    consolidateEvents();

    // Play a sound the first time a transaction is received
    // TODO some more filtering required - just set to play when it confirms for the first time for now
    if (transactionSeenEvent.getDepthInBlocks() == 1) {
      Sounds.playReceiveBitcoin();
    }
  }

  /**
   * Once transactionSeenEvents are consolidated this method is called
   */
  public void onSlowTransactionSeen() {
    // Can now schedule another onSlowTransactionSeen
    waitingToFire = false;

    if (transactionsTable != null) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          ((TransactionTableModel) transactionsTable.getModel()).setTransactions(walletService.getTransactions(), true);
        }
      });
    }
  }

  /**
   * Consolidate many transactionSeenEvents into a single call
   */
  private synchronized void consolidateEvents() {
    if (waitingToFire) {
      // Absorb event
      return;
    } else {
      // Fire in the future
      waitingToFire = true;
      Executors.newSingleThreadScheduledExecutor().schedule(new Callable() {
        @Override
        public Object call() throws Exception {
          onSlowTransactionSeen();
          return null;
        }
      }, CONSOLIDATION_INTERVAL, TimeUnit.MILLISECONDS);
    }
  }
}
