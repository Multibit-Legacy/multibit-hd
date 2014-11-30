package org.multibit.hd.ui.views;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.controller.ShowScreenEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
public class SidebarView extends AbstractView {

  private static final Logger log = LoggerFactory.getLogger(SidebarView.class);

  private final JPanel contentPanel;

  private JTree sidebarTree;

  /**
   * When the last selection was made
   */
  private DateTime lastSelectionDateTime = Dates.nowUtc();
  /**
   * The detail screen that was selected (provide a sensible default)
   */
  private Screen lastSelectedScreen = Screen.SEND_REQUEST;

  /**
   * The wallet tree node
   */
  private DefaultMutableTreeNode walletNode;

  /**
   * The settings tree node
   */
  private DefaultMutableTreeNode settingsNode;

  private final boolean multiWallet;

  /**
   * @param multiWallet True if the overall application is supporting hard- and soft-wallets or is in hierarchical mode
   */
  public SidebarView(boolean multiWallet) {

    super();

    this.multiWallet = multiWallet;

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
  public void updateWalletTreeNode(final String name) {

    Preconditions.checkNotNull(name, "'name' must be present");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Preconditions
        if (multiWallet) {

          // Multi wallet requires a tree node to be updated
          SidebarNodeInfo nodeInfo = new SidebarNodeInfo(name, Screen.SEND_REQUEST);
          walletNode.setUserObject(nodeInfo);

          // This is to ensure the tree resizes correctly
          ((DefaultTreeModel) sidebarTree.getModel()).nodeChanged(walletNode);

        }

        // Always update the title
        Panels.applicationFrame.setTitle(Languages.safeText(MessageKey.MULTIBIT_HD_TITLE) + " - " + name);

      }
    });

  }

  /**
   * @return The sidebar content
   */
  private JScrollPane createSidebarContent() {

    final JScrollPane sidebarPane = new JScrollPane();

    // Ensure we maintain the overall theme
    ScrollBarUIDecorator.apply(sidebarPane, true);

    sidebarTree = new JTree(createSidebarTreeNodes());

    // Ensure it is accessible
    AccessibilityDecorator.apply(sidebarTree, MessageKey.SIDEBAR_TREE);

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

    // Ensure we always have the soft wallet openWalletFromWalletId
    TreePath walletPath = sidebarTree.getPathForRow(0);
    sidebarTree.getSelectionModel().setSelectionPath(walletPath);
    sidebarTree.expandPath(walletPath);

    // Ensure we use the previous selection
    Screen startingScreen = Screen.valueOf(Configurations.currentConfiguration.getAppearance().getCurrentScreen());
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

    sidebarTree.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {

        TreePath path = sidebarTree.getSelectionPath();

        if (path != null) {
          handleTreeSelection((DefaultMutableTreeNode) path.getLastPathComponent());
        }

      }
    });

    sidebarPane.setViewportView(sidebarTree);
    sidebarPane.setBorder(null);

    return sidebarPane;
  }


  private DefaultMutableTreeNode createSidebarTreeNodes() {

    DefaultMutableTreeNode root = TreeNodes.newSidebarTreeNode("", Screen.SEND_REQUEST);

    // This node gets overwritten by WalletSummary.getName()
    walletNode = TreeNodes.newSidebarTreeNode(MessageKey.WALLET, Screen.SEND_REQUEST);

    // #61 At the moment all users don't use a Trezor or soft-wallet trezor
    if (multiWallet) {
      // Add standard wallet nodes at the soft-wallet level
      walletNode.add(TreeNodes.newSidebarTreeNode(MessageKey.SEND_OR_REQUEST, Screen.SEND_REQUEST));
      walletNode.add(TreeNodes.newSidebarTreeNode(MessageKey.PAYMENTS, Screen.TRANSACTIONS));
      walletNode.add(TreeNodes.newSidebarTreeNode(MessageKey.CONTACTS, Screen.CONTACTS));
      root.add(walletNode);
    } else {
      // Add standard wallet nodes at the root level
      root.add(TreeNodes.newSidebarTreeNode(MessageKey.SEND_OR_REQUEST, Screen.SEND_REQUEST));
      root.add(TreeNodes.newSidebarTreeNode(MessageKey.PAYMENTS, Screen.TRANSACTIONS));
      root.add(TreeNodes.newSidebarTreeNode(MessageKey.CONTACTS, Screen.CONTACTS));
    }

    // Add application nodes
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.HELP, Screen.HELP));
    settingsNode = TreeNodes.newSidebarTreeNode(MessageKey.SETTINGS, Screen.SETTINGS);
    root.add(settingsNode);
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.MANAGE_WALLET, Screen.MANAGE_WALLET));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.TOOLS, Screen.TOOLS));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.EXIT_OR_SWITCH, Screen.EXIT));

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
          Configurations.currentConfiguration.getAppearance().setCurrentScreen(nodeInfo.getDetailScreen().name());
          ControllerEvents.fireShowDetailScreenEvent(nodeInfo.getDetailScreen());
      }
    } else {
      log.debug("Ignoring selection: '{}'", detailScreen);
    }

    lastSelectedScreen = detailScreen;
    lastSelectionDateTime = Dates.nowUtc();

  }

  /**
   * Do everything to grab the focus without triggering a selection event
   * This is necessary to ensure keyboard navigation of the sidebar is retained after
   * a cancelled Exit operation
   */
  public void requestFocus() {

    lastSelectionDateTime = Dates.nowUtc();
    sidebarTree.setFocusable(true);
    sidebarTree.requestFocusInWindow();

  }

  @Subscribe
  public void onShowDetailScreen(final ShowScreenEvent event) {

    Preconditions.checkNotNull(event, "'event' must be present");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        Screen screen = event.getScreen();

        // Double check that the settings / preferences screen has just been selected
        if (Screen.SETTINGS.equals(screen)) {
          if (settingsNode != null && sidebarTree != null) {
            // Select the settings node
            sidebarTree.setSelectionPath(new TreePath(settingsNode.getPath()));
          }
        }
      }
    });
  }
}
