package org.multibit.hd.ui.views;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.events.controller.ShowScreenEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
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
 * @TODO THe multi wallet support is not needed now so this class can be simplified
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
   * The settings tree node
   */
  private DefaultMutableTreeNode settingsNode;

  /**
   * The tools tree node
   */
  private DefaultMutableTreeNode toolsNode;

  public SidebarView() {

    super();

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

        // Always update the title
        Panels.getApplicationFrame().setTitle(Languages.safeText(MessageKey.MULTIBIT_HD_TITLE) + " - " + name);

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

    // Ensure we always have the soft wallet open
    TreePath walletPath = sidebarTree.getPathForRow(0);
    sidebarTree.getSelectionModel().setSelectionPath(walletPath);
    sidebarTree.expandPath(walletPath);

    // Ensure we use the previous selection
    Screen startingScreen;
    try {
      startingScreen = Screen.valueOf(Configurations.currentConfiguration.getAppearance().getCurrentScreen());
    } catch (IllegalArgumentException e) {
      // Unknown starting screen - possibly an old configuration
      // Default to same as configuration (safest option given network connectivity)
      startingScreen = Screen.SEND_REQUEST;
    }
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

    // Add nodes
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.BUY_OR_SELL, Screen.BUY_SELL));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.SEND_OR_REQUEST, Screen.SEND_REQUEST));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.PAYMENTS, Screen.TRANSACTIONS));
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.CONTACTS, Screen.CONTACTS));

    // Add application nodes
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.HELP, Screen.HELP));
    settingsNode = TreeNodes.newSidebarTreeNode(MessageKey.SETTINGS, Screen.SETTINGS);
    root.add(settingsNode);
    root.add(TreeNodes.newSidebarTreeNode(MessageKey.MANAGE_WALLET, Screen.MANAGE_WALLET));
    toolsNode = TreeNodes.newSidebarTreeNode(MessageKey.TOOLS, Screen.TOOLS);
    root.add(toolsNode);
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
        case BUY_SELL:
          Panels.showLightBox(Wizards.newBuySellWizard().getWizardScreenHolder());
          break;
        case EXIT:
          Panels.showLightBox(Wizards.newExitWizard().getWizardScreenHolder());
          break;
        default:
          Configurations.currentConfiguration.getAppearance().setCurrentScreen(nodeInfo.getDetailScreen().name());
          ViewEvents.fireShowDetailScreenEvent(nodeInfo.getDetailScreen());
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

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          lastSelectionDateTime = Dates.nowUtc();
          sidebarTree.setFocusable(true);
          sidebarTree.requestFocusInWindow();
        }
      });

  }

  @Subscribe
  public void onShowDetailScreen(final ShowScreenEvent event) {

    Preconditions.checkNotNull(event, "'event' must be present");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        Screen screen = event.getScreen();

        if (sidebarTree != null) {
          // Check that the settings / preferences screen has just been selected
          if (Screen.SETTINGS.equals(screen)) {
            if (settingsNode != null) {
              // Select the settings node
              sidebarTree.setSelectionPath(new TreePath(settingsNode.getPath()));
            }
          }

          // Check that the tools screen has just been selected
          if (Screen.TOOLS.equals(screen)) {
            if (toolsNode != null) {
              // Select the tools node
              sidebarTree.setSelectionPath(new TreePath(toolsNode.getPath()));
            }
          }
        }
      }
    });
  }
}
