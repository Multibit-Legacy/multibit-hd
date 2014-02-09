package org.multibit.hd.ui.views.screens.transactions;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.WalletId;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.Tables;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;

import javax.swing.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the exit confirmation display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class TransactionsPanelView extends AbstractScreenView<TransactionsPanelModel> {

  private JTable transactionsTable;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public TransactionsPanelView(TransactionsPanelModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
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

    WalletService walletService = CoreServices.newWalletService();

    transactionsTable = Tables.newTransactionsTable(walletService.getTransactions());

    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(transactionsTable);
    scrollPane.setViewportBorder(null);

    // Add to the panel
    contentPanel.add(scrollPane, "grow");

    return contentPanel;
  }

  private WalletId getCurrentWalletId() {
    return WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId();
  }


}
