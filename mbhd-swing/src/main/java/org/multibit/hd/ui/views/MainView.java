package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.LocaleChangeEvent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the main frame</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class MainView extends JFrame {

  private JTree sidebarTree;

  private final JPanel headerPanel;
  private final JPanel footerPanel;

  public MainView(
    JPanel headerPanel,
    JPanel footerPanel
  ) {

    this.headerPanel = headerPanel;
    this.footerPanel = footerPanel;

    CoreServices.uiEventBus.register(this);

    // TODO i18n
    setTitle("MultiBit HD");

    // TODO Configuration
    setBackground(Color.gray);

    // TODO Configuration
    setPreferredSize(new Dimension(1280, 1024));

  }

  @Subscribe
  public void onLocaleChangeEvent(LocaleChangeEvent event) {

    setVisible(false);

    // TODO Check if the Swing way can be made to work here
    setLocale(event.getLocale());

    getContentPane().removeAll();
    getContentPane().add(createMainContent());

    pack();
    setVisible(true);

  }


  /**
   * @return The contents of the main panel
   */
  private JPanel createMainContent() {

    // Create the main panel and place it in this frame
    JPanel mainPanel = new JPanel(new BorderLayout());

    // Add the supporting panels
    mainPanel.add(headerPanel, BorderLayout.PAGE_START);
    mainPanel.add(createCenterContent(), BorderLayout.CENTER);
    mainPanel.add(footerPanel, BorderLayout.PAGE_END);

    return mainPanel;
  }


  private JComponent createCenterContent() {

    // Create a splitter pane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    // Create the LHS
    JScrollPane sidebarPane = createSidebarContent();

    // Create the RHS
    JScrollPane detailPane = createDetailContent();

    splitPane.setLeftComponent(sidebarPane);
    splitPane.setRightComponent(detailPane);

    splitPane.setDividerSize(3);

    splitPane.setBackground(new Color(128, 128, 128));

    return splitPane;

  }

  /**
   * @return The sidebar content
   */
  private JScrollPane createSidebarContent() {

    JScrollPane sidebarPane = new JScrollPane();

    sidebarTree = new JTree(createSidebarTreeNodes());
    sidebarTree.setShowsRootHandles(false);
    sidebarTree.setRootVisible(false);
    // TODO Integrate with styles
    sidebarTree.setBackground(new Color(240, 240, 240));
    sidebarTree.setVisibleRowCount(10);
    sidebarTree.setExpandsSelectedPaths(true);

    sidebarPane.setViewportView(sidebarTree);
    // TODO Integrate with configuration
    sidebarPane.setPreferredSize(new Dimension(150, 1024));

    return sidebarPane;
  }

  /**
   * @return The detail content pane
   */
  private JScrollPane createDetailContent() {

    JScrollPane detailPane = new JScrollPane();
    detailPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    detailPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

    return detailPane;

  }

  private DefaultMutableTreeNode createSidebarTreeNodes() {

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Wallet");

    DefaultMutableTreeNode wallet = new DefaultMutableTreeNode("Wallet");
    wallet.add(new DefaultMutableTreeNode("Account 1"));
    wallet.add(new DefaultMutableTreeNode("Account 2"));
    wallet.add(new DefaultMutableTreeNode("Messages"));
    wallet.add(new DefaultMutableTreeNode("Contacts"));
    wallet.add(new DefaultMutableTreeNode("Transactions"));
    root.add(wallet);

    DefaultMutableTreeNode trezor1 = new DefaultMutableTreeNode("Trezor 1");
    trezor1.add(new DefaultMutableTreeNode("Account 1"));
    trezor1.add(new DefaultMutableTreeNode("Account 2"));
    trezor1.add(new DefaultMutableTreeNode("Messages"));
    trezor1.add(new DefaultMutableTreeNode("Contacts"));
    trezor1.add(new DefaultMutableTreeNode("Transactions"));
    root.add(trezor1);

    DefaultMutableTreeNode trezor2 = new DefaultMutableTreeNode("Trezor 2");
    trezor2.add(new DefaultMutableTreeNode("Account 1"));
    trezor2.add(new DefaultMutableTreeNode("Account 2"));
    trezor2.add(new DefaultMutableTreeNode("Messages"));
    trezor2.add(new DefaultMutableTreeNode("Contacts"));
    trezor2.add(new DefaultMutableTreeNode("Transactions"));
    root.add(trezor2);

    return root;
  }

}