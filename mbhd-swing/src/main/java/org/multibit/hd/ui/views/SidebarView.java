package org.multibit.hd.ui.views;

import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.SidebarNodeInfo;
import org.multibit.hd.ui.views.components.ThemeAwareTreeCellRenderer;
import org.multibit.hd.ui.views.components.TreeNodes;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

  /**
   * When the last selection was made
   */
  private DateTime lastSelectionDateTime = Dates.nowUtc();
  /**
   * The detail screen that was selected
   */
  private Screen lastSelectedScreen = Screen.WALLET;

  public SidebarView() {

    CoreServices.uiEventBus.register(this);

    // Insets for top, left
    MigLayout layout = new MigLayout(
      "fill, insets 6 10" + (Languages.isLeftToRight() ? "" : ",rtl"), // Layout
      "[]", // Columns
      "[]" // Rows
    );
    contentPanel = Panels.newPanel(layout);

    // Apply the sidebar theme
    contentPanel.setBackground(Themes.currentTheme.sidebarPanelBackground());

    // Apply opacity
    contentPanel.setOpaque(true);

    contentPanel.add(createSidebarContent(), "grow,push");

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

    final JScrollPane sidebarPane = new JScrollPane();

    final JTree sidebarTree = new JTree(createSidebarTreeNodes());
    sidebarTree.setShowsRootHandles(false);
    sidebarTree.setRootVisible(false);

    // Remove tree view selection
    NimbusDecorator.disableTreeViewSelection(sidebarTree);

    // Apply the theme
    sidebarTree.setBackground(Themes.currentTheme.sidebarPanelBackground());
    sidebarTree.setCellRenderer(new ThemeAwareTreeCellRenderer());

    sidebarTree.setVisibleRowCount(10);

    // Require 2 clicks to toggle to make UX smoother when simply selecting wallet
    sidebarTree.setToggleClickCount(2);

    // Ensure we always have the soft wallet open
    TreePath walletPath = sidebarTree.getPathForRow(0);
    sidebarTree.getSelectionModel().setSelectionPath(walletPath);
    sidebarTree.expandPath(walletPath);

    // Get the tree cell renderer to handle the row height
    sidebarTree.setRowHeight(0);

    sidebarTree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    sidebarTree.setFont(sidebarTree.getFont().deriveFont(16.0f));

    sidebarTree.addMouseListener(new MouseAdapter() {

      public void mouseReleased(MouseEvent e) {

        TreePath path = sidebarTree.getPathForLocation(e.getX(), e.getY());

        if (path != null) {
          handleTreeSelection((DefaultMutableTreeNode) path.getLastPathComponent());
        }
      }
    });

    sidebarTree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

        handleTreeSelection(node);

      }
    });

    sidebarPane.setViewportView(sidebarTree);
    sidebarPane.setBorder(null);

    return sidebarPane;
  }

  private DefaultMutableTreeNode createSidebarTreeNodes() {

    DefaultMutableTreeNode root = TreeNodes.newSidebarTreeNode("", Screen.WALLET);

    DefaultMutableTreeNode wallet = TreeNodes.newSidebarTreeNode("Wallet", Screen.WALLET);
    wallet.add(TreeNodes.newSidebarTreeNode(MessageKey.CONTACTS, Screen.CONTACTS));
    wallet.add(TreeNodes.newSidebarTreeNode(MessageKey.TRANSACTIONS, Screen.TRANSACTIONS));
    wallet.add(TreeNodes.newSidebarTreeNode(MessageKey.HISTORY, Screen.HISTORY));
    root.add(wallet);

    root.add(TreeNodes.newSidebarTreeNode(MessageKey.HELP, Screen.HELP));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.SETTINGS, Screen.SETTINGS));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.TOOLS, Screen.TOOLS));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.EXIT, Screen.EXIT));

    return root;
  }


  /**
   * @param node The selected node
   */
  private void handleTreeSelection(DefaultMutableTreeNode node) {

    SidebarNodeInfo nodeInfo = (SidebarNodeInfo) node.getUserObject();

    Screen detailScreen = nodeInfo.getDetailScreen();

    // Filter out multiple events for the same screen, but allow repeats to occur (such as the exit screen)
    boolean ignore = detailScreen.equals(lastSelectedScreen) && Dates.nowUtc().isBefore(lastSelectionDateTime.plusSeconds(1));
    if (!ignore) {

      switch (detailScreen) {
        // Add special cases
        case EXIT:
          Panels.showLightBox(Wizards.newExitWizard().getWizardPanel());
          break;
        default:
          ControllerEvents.fireShowDetailScreenEvent(nodeInfo.getDetailScreen());
      }
    }

    lastSelectedScreen = detailScreen;
    lastSelectionDateTime = Dates.nowUtc();
  }

}
