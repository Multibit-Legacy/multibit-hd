package org.multibit.hd.ui.views;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.ThemeAwareTreeCellRenderer;
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

  private JTree sidebarTree;

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

    sidebarTree = new JTree(createSidebarTreeNodes());
    sidebarTree.setShowsRootHandles(false);
    sidebarTree.setRootVisible(false);

    // Apply the theme
    sidebarTree.setBackground(Themes.currentTheme.sidebarPanelBackground());
    sidebarTree.setCellRenderer(new ThemeAwareTreeCellRenderer());

    sidebarTree.setVisibleRowCount(10);
    sidebarTree.setToggleClickCount(1);

    // Ensure we always have the soft wallet open
    TreePath walletPath =sidebarTree.getPathForRow(0);
    sidebarTree.getSelectionModel().setSelectionPath(walletPath);
    sidebarTree.expandPath(walletPath);

    // Get the tree cell renderer to handle the row height
    sidebarTree.setRowHeight(0);

    sidebarTree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    sidebarTree.setFont(sidebarTree.getFont().deriveFont(16.0f));


    sidebarTree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

        switch (node.toString()) {
          case "Wallet":
            ViewEvents.fireShowDetailScreenEvent(Screen.MAIN_WALLET);
            break;
          case "Trezor 1":
            ViewEvents.fireShowDetailScreenEvent(Screen.MAIN_WALLET);
            break;
          case "Trezor 2":
            ViewEvents.fireShowDetailScreenEvent(Screen.MAIN_WALLET);
            break;
          case "Exit":
            Panels.showLightBox(Wizards.newExitWizard().getContentPanel());
            break;
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

    // TODO Convert this to proper tree model with actions
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Wallet");

    DefaultMutableTreeNode wallet = new DefaultMutableTreeNode("Wallet");
    wallet.add(new DefaultMutableTreeNode("Contacts"));
    wallet.add(new DefaultMutableTreeNode("Transactions"));
    root.add(wallet);

    DefaultMutableTreeNode trezor1 = new DefaultMutableTreeNode("Trezor 1");
    trezor1.add(new DefaultMutableTreeNode("Contacts"));
    trezor1.add(new DefaultMutableTreeNode("Transactions"));
    root.add(trezor1);

    DefaultMutableTreeNode trezor2 = new DefaultMutableTreeNode("Trezor 2");
    trezor2.add(new DefaultMutableTreeNode("Contacts"));
    trezor2.add(new DefaultMutableTreeNode("Transactions"));
    root.add(trezor2);

    root.add(new DefaultMutableTreeNode("Help"));
    root.add(new DefaultMutableTreeNode("History"));
    root.add(new DefaultMutableTreeNode("Preferences"));
    root.add(new DefaultMutableTreeNode("Tools"));
    root.add(new DefaultMutableTreeNode("Exit"));

    return root;
  }

}
