package org.multibit.hd.ui.views;

import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.languages.MessageKey;
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
   * The detail screen that was selected (provide a sensible default)
   */
  private Screen lastSelectedScreen = Screen.WALLET;

  /**
   * The wallet tree node
   */
  private DefaultMutableTreeNode walletNode;

  public SidebarView() {

    CoreServices.uiEventBus.register(this);

    // Insets for top, left
    MigLayout layout = new MigLayout(
      Panels.migLayout("fill, insets 6 10"),
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
   * @param name The wallet summary name
   */
  public void updateWalletTreeNode(String name) {

    if (walletNode != null && name != null) {

      SidebarNodeInfo nodeInfo = new SidebarNodeInfo(name, Screen.WALLET);
      walletNode.setUserObject(nodeInfo);

    }

  }

  /**
   * @return The sidebar content
   */
  private JScrollPane createSidebarContent() {

    final JScrollPane sidebarPane = new JScrollPane();

    final JTree sidebarTree = new JTree(createSidebarTreeNodes());

    // Ensure FEST can find it
    sidebarTree.setName(MessageKey.SIDEBAR_TREE.getKey());

    sidebarTree.setShowsRootHandles(false);
    sidebarTree.setRootVisible(false);

    // Remove tree view selection
    NimbusDecorator.disableTreeViewSelection(sidebarTree);

    // Apply the theme
    sidebarTree.setBackground(Themes.currentTheme.sidebarPanelBackground());
    sidebarTree.setCellRenderer(new ThemeAwareTreeCellRenderer());

    sidebarTree.setVisibleRowCount(10);

    // Require 2 clicks to toggle to make UX smoother when simply selecting wallet
    // Collapsing should be a rare event in normal use
    sidebarTree.setToggleClickCount(2);

    // Ensure we always have the soft wallet open
    TreePath walletPath = sidebarTree.getPathForRow(0);
    sidebarTree.getSelectionModel().setSelectionPath(walletPath);
    sidebarTree.expandPath(walletPath);

    // Ensure we use the previous selection
    Screen startingScreen = Screen.valueOf(Configurations.currentConfiguration.getApplication().getCurrentScreen());
    for (int row = 0; row < sidebarTree.getRowCount(); row++) {
      TreePath screenPath = sidebarTree.getPathForRow(row);
      if (screenPath != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) screenPath.getLastPathComponent();

        SidebarNodeInfo nodeInfo = (SidebarNodeInfo) node.getUserObject();
        Screen detailScreen = nodeInfo.getDetailScreen();
        if (detailScreen.equals(startingScreen)) {
          sidebarTree.setSelectionRow(row);
        }

      }
    }

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

    walletNode = TreeNodes.newSidebarTreeNode("Wallet", Screen.WALLET);
    walletNode.add(TreeNodes.newSidebarTreeNode(MessageKey.SEND_OR_REQUEST, Screen.WALLET));
    walletNode.add(TreeNodes.newSidebarTreeNode(MessageKey.TRANSACTIONS, Screen.TRANSACTIONS));
    walletNode.add(TreeNodes.newSidebarTreeNode(MessageKey.CONTACTS, Screen.CONTACTS));
    root.add(walletNode);

    root.add(TreeNodes.newSidebarTreeNode(MessageKey.HELP, Screen.HELP));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.HISTORY, Screen.HISTORY));
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
          Panels.showLightBox(Wizards.newExitWizard().getWizardScreenHolder());
          break;
        default:
          Configurations.currentConfiguration.getApplication().setCurrentScreen(nodeInfo.getDetailScreen().name());
          ControllerEvents.fireShowDetailScreenEvent(nodeInfo.getDetailScreen());
      }
    }

    lastSelectedScreen = detailScreen;
    lastSelectionDateTime = Dates.nowUtc();
  }
}
