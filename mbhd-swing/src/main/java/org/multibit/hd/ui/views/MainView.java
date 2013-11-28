package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.ui.events.BalanceChangeEvent;
import org.multibit.hd.ui.events.LocaleChangeEvent;
import org.multibit.hd.ui.i18n.BitcoinSymbol;
import org.multibit.hd.ui.i18n.Formats;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class MainView extends JFrame {

  private JLabel balanceLHSLabel;
  private JLabel balanceRHSLabel;
  private JLabel balanceRHSSymbolLabel;
  private JLabel exchangeLabel;

  private JTree sidebarTree;

  private JLabel helpLabel;
  private JLabel settingsLabel;
  private JLabel signOutLabel;

  public MainView() {

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
   * <p>Handles the representation of the balance based on the current configuration</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public void onBalanceChangeEvent(BalanceChangeEvent event) {

    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();
    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration();

    String[] balance = Formats.formatBitcoinBalance(event.getBtcBalance().getAmount());
    String localBalance = Formats.formatLocalBalance(event.getLocalBalance().getAmount());

    BitcoinSymbol symbol = BitcoinSymbol.valueOf(bitcoinConfiguration.getBitcoinSymbol());

    if (i18nConfiguration.isCurrencySymbolPrefixed()) {
      handlePrefixedSymbol(balance, symbol);
    } else {
      handleSuffixSymbol(symbol);
    }

    balanceLHSLabel.setText(balance[0]);
    balanceRHSLabel.setText(balance[1]);

    // TODO Add this to resource bundles
    String exchangeText = "~ ${0} ({1})";
    exchangeLabel.setText(
      Languages.safeText(
        exchangeText,
        localBalance,
        event.getRateProvider()
      ));
  }

  /**
   * <p>Place currency symbol before the number</p>
   *
   * @param symbol The symbol to use
   */
  private void handlePrefixedSymbol(String[] balance, BitcoinSymbol symbol) {

    // Place currency symbol before the number
    if (BitcoinSymbol.ICON.equals(symbol)) {
      // Add icon to LHS, remove from elsewhere
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, balanceLHSLabel);
      AwesomeDecorator.removeIcon(balanceRHSSymbolLabel);
      balanceRHSSymbolLabel.setText("");
    } else {
      // Add symbol to LHS, remove from elsewhere
      balance[0] = symbol.getSymbol() + " " + balance[0];
      AwesomeDecorator.removeIcon(balanceLHSLabel);
    }

  }

  /**
   * <p>Place currency symbol after the number</p>
   *
   * @param symbol The symbol to use
   */
  private void handleSuffixSymbol(BitcoinSymbol symbol) {

    if (BitcoinSymbol.ICON.equals(symbol)) {
      // Add icon to RHS, remove from elsewhere
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, balanceRHSSymbolLabel);
      AwesomeDecorator.removeIcon(balanceLHSLabel);
      balanceRHSSymbolLabel.setText("");
    } else {
      // Add symbol to RHS, remove from elsewhere
      balanceRHSSymbolLabel.setText(symbol.getSymbol());
      AwesomeDecorator.removeIcon(balanceLHSLabel);
      AwesomeDecorator.removeIcon(balanceRHSSymbolLabel);
    }

  }

  /**
   * @return The contents of the main panel
   */
  private JPanel createMainContent() {

    // Create the main panel and place it in this frame
    JPanel mainPanel = new JPanel(new BorderLayout());

    // Add the supporting panels
    mainPanel.add(createHeaderContent(), BorderLayout.PAGE_START);
    mainPanel.add(createCenterContent(), BorderLayout.CENTER);
    mainPanel.add(createFooterContent(), BorderLayout.PAGE_END);

    return mainPanel;
  }

  private JComponent createFooterContent() {

    JPanel footerPanel = new JPanel();

    JProgressBar progressBar = new JProgressBar();

    footerPanel.add(progressBar, BorderLayout.LINE_END);

    return footerPanel;
  }

  private JComponent createHeaderContent() {

    MigLayout layout = new MigLayout("fillx");
    JPanel headerPanel = new JPanel(layout);

    // Add the balance
    balanceLHSLabel = new JLabel();
    balanceRHSLabel = new JLabel();
    balanceRHSSymbolLabel = new JLabel();
    exchangeLabel = new JLabel();

    Font balanceFont = balanceLHSLabel.getFont().deriveFont(42.0f);
    Font decimalFont = balanceLHSLabel.getFont().deriveFont(28.0f);

    balanceLHSLabel.setFont(balanceFont);
    balanceRHSLabel.setFont(decimalFont);
    balanceRHSSymbolLabel.setFont(balanceFont);
    exchangeLabel.setFont(decimalFont);

    // TODO Fix the decimal alignment
    balanceRHSLabel.setForeground(Themes.H1.foreground);

    headerPanel.add(balanceLHSLabel, "baseline grow");
    headerPanel.add(balanceRHSLabel, "gap related");
    headerPanel.add(balanceRHSSymbolLabel, "gap related");
    headerPanel.add(exchangeLabel, "gap unrelated");
    headerPanel.add(new JLabel(), "push");

    // Add the right hand controls
    helpLabel = Labels.newHelpLabel();
    settingsLabel = Labels.newSettingsLabel();
    signOutLabel = Labels.newSignOutLabel();

    headerPanel.add(helpLabel, "gap unrelated");
    headerPanel.add(settingsLabel, "gap unrelated");
    headerPanel.add(signOutLabel, "gap unrelated");

    return headerPanel;
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

  protected JPanel makeTextPanel(String text) {

    JPanel panel = new JPanel(false);
    JLabel filler = new JLabel(text);
    filler.setHorizontalAlignment(JLabel.CENTER);
    panel.setLayout(new MigLayout());
    panel.add(filler);

    return panel;
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