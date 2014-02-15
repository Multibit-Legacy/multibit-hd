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

    // TODO bundle up the transactionSeenEvent as there could be a few of them
    if (transactionsTable != null) {
      ((TransactionTableModel)transactionsTable.getModel()).setTransactions(walletService.getTransactions(), true);
    }

    // Play a sound the first time a transaction is received
    // TODO some more filtering required - just set to play when it confirms for the first time for now
    if (transactionSeenEvent.getDepthInBlocks() == 1) {
      Sounds.playReceiveBitcoin();
    }
  }
}
