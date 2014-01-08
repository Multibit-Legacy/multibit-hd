package org.multibit.hd.ui.views;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.SidebarNodeInfo;
import org.multibit.hd.ui.views.components.ThemeAwareTreeCellRenderer;
import org.multibit.hd.ui.views.components.TreeNodes;
import org.multibit.hd.ui.views.detail_views.DetailScreen;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the sidebar display (LHS of split pane)</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class SidebarView {

  private final JPanel contentPanel;

  public SidebarView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "filly, insets 6 10, ", // Layout
      "[]", // Columns
      "[]" // Rows
    );
    contentPanel = Panels.newPanel(layout);

    // Apply the sidebar theme
    contentPanel.setBackground(Themes.currentTheme.sidebarPanelBackground());

    contentPanel.add(createSidebarContent(), "push");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  /**
   * @return The sidebar content
   */
  private JScrollPane createSidebarContent() {

    JScrollPane sidebarPane = new JScrollPane();

    JTree sidebarTree = new JTree(createSidebarTreeNodes());
    sidebarTree.setShowsRootHandles(false);
    sidebarTree.setRootVisible(false);

    // Remove tree view selection
    NimbusDecorator.disableTreeViewSelection(sidebarTree);

    // Apply the theme
    sidebarTree.setBackground(Themes.currentTheme.sidebarPanelBackground());
    sidebarTree.setCellRenderer(new ThemeAwareTreeCellRenderer());


    sidebarTree.setVisibleRowCount(10);
    sidebarTree.setToggleClickCount(2);

    // Ensure we always have the soft wallet open
    TreePath walletPath = sidebarTree.getPathForRow(0);
    sidebarTree.getSelectionModel().setSelectionPath(walletPath);
    sidebarTree.expandPath(walletPath);

    // Get the tree cell renderer to handle the row height
    sidebarTree.setRowHeight(0);

    sidebarTree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    sidebarTree.setFont(sidebarTree.getFont().deriveFont(16.0f));

    sidebarTree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

        SidebarNodeInfo nodeInfo = (SidebarNodeInfo) node.getUserObject();

        switch (nodeInfo.getDetailScreen()) {
          // Add special cases
          case EXIT:
            Panels.showLightBox(Wizards.newExitWizard().getWizardPanel());
            break;
          default:
            ControllerEvents.fireShowDetailScreenEvent(nodeInfo.getDetailScreen());
        }

      }
    });

    sidebarPane.setViewportView(sidebarTree);
    sidebarPane.setBorder(null);

    // TODO Integrate with configuration
    sidebarPane.setPreferredSize(new Dimension(150, 1024));

    return sidebarPane;
  }


  private DefaultMutableTreeNode createSidebarTreeNodes() {

    DefaultMutableTreeNode root = TreeNodes.newSidebarTreeNode("", DetailScreen.WALLET);

    DefaultMutableTreeNode wallet = TreeNodes.newSidebarTreeNode("Wallet", DetailScreen.WALLET);
    wallet.add(TreeNodes.newSidebarTreeNode(MessageKey.CONTACTS, DetailScreen.CONTACTS));
    wallet.add(TreeNodes.newSidebarTreeNode(MessageKey.TRANSACTIONS, DetailScreen.TRANSACTIONS));
    root.add(wallet);

    DefaultMutableTreeNode trezor1 = TreeNodes.newSidebarTreeNode("Trezor 1", DetailScreen.WALLET);
    trezor1.add(TreeNodes.newSidebarTreeNode(MessageKey.CONTACTS, DetailScreen.CONTACTS));
    trezor1.add(TreeNodes.newSidebarTreeNode(MessageKey.TRANSACTIONS, DetailScreen.TRANSACTIONS));
    root.add(trezor1);

    DefaultMutableTreeNode trezor2 = TreeNodes.newSidebarTreeNode("Trezor 2", DetailScreen.WALLET);
    trezor2.add(TreeNodes.newSidebarTreeNode(MessageKey.CONTACTS, DetailScreen.CONTACTS));
    trezor2.add(TreeNodes.newSidebarTreeNode(MessageKey.TRANSACTIONS, DetailScreen.TRANSACTIONS));
    root.add(trezor2);

    root.add(TreeNodes.newSidebarTreeNode(MessageKey.HELP, DetailScreen.HELP));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.HISTORY, DetailScreen.HISTORY));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.PREFERENCES, DetailScreen.PREFERENCES));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.TOOLS, DetailScreen.TOOLS));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.EXIT, DetailScreen.EXIT));

    return root;
  }

}
