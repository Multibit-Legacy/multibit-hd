package org.multibit.hd.ui.swing.views;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.ui.fonts.AwesomeDecorator;
import org.multibit.hd.ui.fonts.AwesomeIcon;
import org.multibit.hd.ui.i18n.BitcoinSymbol;
import org.multibit.hd.ui.i18n.Formats;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

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

  // TODO Add controller
  public MainView() {

    // TODO Add custom L&F

    initComponents();

  }

  private void initComponents() {


    // TODO i18n
    setTitle("MultiBit HD");

    // TODO Configuration
    setBackground(Color.gray);

    // TODO Configuration
    setPreferredSize(new Dimension(1280, 1024));

    getContentPane().add(createMainContent());

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

    JPanel headerPanel = new JPanel(new BorderLayout());

    JPanel balancePanel = createBalanceContent();
    balancePanel.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);

    JPanel supportPanel = createSupportContent();
    supportPanel.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);

    headerPanel.add(balancePanel, BorderLayout.LINE_START);
    headerPanel.add(supportPanel, BorderLayout.LINE_END);

    return headerPanel;
  }

  /**
   * @return The support display content
   */
  private JPanel createSupportContent() {

    JPanel supportPanel = new JPanel();

    helpLabel = AwesomeDecorator.createIconLabel(AwesomeIcon.QUESTION, "Help");
    settingsLabel = AwesomeDecorator.createIconLabel(AwesomeIcon.GEAR, "Settings");
    signOutLabel = AwesomeDecorator.createIconLabel(AwesomeIcon.SIGN_OUT, "Sign Out");

    supportPanel.add(helpLabel, BorderLayout.LINE_END);
    supportPanel.add(settingsLabel, BorderLayout.LINE_END);
    supportPanel.add(signOutLabel, BorderLayout.LINE_END);

    return supportPanel;
  }

  /**
   * @return The balance display content
   */
  private JPanel createBalanceContent() {

    JPanel balancePanel = new JPanel();

    balanceLHSLabel = new JLabel("0.00");
    balanceRHSLabel = new JLabel("000000");
    balanceRHSSymbolLabel = new JLabel("BTC");
    exchangeLabel = new JLabel("~ 0.00 (Mt Gox)");

    Font balanceFont = balanceLHSLabel.getFont().deriveFont(42.0f);
    Font decimalFont = balanceLHSLabel.getFont().deriveFont(28.0f);

    balanceLHSLabel.setFont(balanceFont);
    balanceRHSLabel.setFont(decimalFont);
    balanceRHSSymbolLabel.setFont(balanceFont);
    exchangeLabel.setFont(balanceFont);

    // TODO Colors for a ColorPalette
    // TODO Fix the decimal alignment
    balanceRHSLabel.setForeground(new Color(128, 128, 128));

    balancePanel.add(balanceLHSLabel);
    balancePanel.add(balanceRHSLabel);
    balancePanel.add(balanceRHSSymbolLabel);
    balancePanel.add(exchangeLabel);

    return balancePanel;
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
    splitPane.setBorder(Borders.EMPTY);

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
    sidebarTree.setBackground(new Color(240, 240, 240));
    sidebarTree.setVisibleRowCount(10);
    sidebarTree.setExpandsSelectedPaths(true);

    sidebarPane.setViewportView(sidebarTree);

    return sidebarPane;
  }

  /**
   * @return The detail content pane
   */
  private JScrollPane createDetailContent() {

    JScrollPane detailPane = new JScrollPane();
    detailPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    detailPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    detailPane.setBorder(Borders.EMPTY);

    JTabbedPane tabbedPane = new JTabbedPane();

    JComponent panel1 = makeTextPanel("Panel #1");
    tabbedPane.addTab("Tab 1", panel1);
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

    JComponent panel2 = makeTextPanel("Panel #2");
    tabbedPane.addTab("Tab 2", panel2);
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

    JComponent panel3 = makeTextPanel("Panel #3");
    tabbedPane.addTab("Tab 3", panel3);
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

    JComponent panel4 = makeTextPanel("Panel #4 (has a preferred size of 410 x 50).");
    panel4.setPreferredSize(new Dimension(410, 50));
    tabbedPane.addTab("Tab 4", panel4);
    tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

    detailPane.setViewportView(tabbedPane);

    return detailPane;

  }

  protected JComponent makeTextPanel(String text) {

    JPanel panel = new JPanel(false);
    JLabel filler = new JLabel(text);
    filler.setHorizontalAlignment(JLabel.CENTER);
    panel.setLayout(new GridLayout(1, 1));
    panel.add(filler);

    return panel;
  }


  public JPanel createSettingsExchangePanel() {

    FormLayout layout = new FormLayout("pref");
    DefaultFormBuilder rowBuilder = new DefaultFormBuilder(layout);
    rowBuilder.border(Borders.DIALOG);

    rowBuilder.append(buildSample("Left to Right", true));
    rowBuilder.append(buildSample("Right to Left", false));
    rowBuilder.append(buildSample("Default Orientation", new PanelBuilder(layout).isLeftToRight()));

    return rowBuilder.getPanel();
  }

  public JPanel createSettingsLanguagePanel() {

    FormLayout layout = new FormLayout("pref");
    DefaultFormBuilder rowBuilder = new DefaultFormBuilder(layout);
    rowBuilder.border(Borders.DIALOG);

    rowBuilder.append(buildSample("Left to Right", true));
    rowBuilder.append(buildSample("Right to Left", false));
    rowBuilder.append(buildSample("Default Orientation", new PanelBuilder(layout).isLeftToRight()));

    return rowBuilder.getPanel();
  }

  /**
   * Creates and returns a sample panel that consists of a titled
   * separator and two component lines each with a 'leading' label.
   * Honors the specified component orientation.<p>
   *
   * The builder code avoids creating individual cell constraints;
   * all cell constraints used in the example below will be created
   * on-the-fly by the builder layer.<p>
   *
   * Note that cell constraints should be flipped and repositioned
   * if they are intended for being used with left-to-right and
   * right-to-left layouts.
   *
   * @return the sample panel
   */
  private Component buildSample(String title, boolean leftToRight) {

    String leftToRightSpecs = "right:pref, 4dlu, pref:grow, 3dlu, pref:grow";

    FormLayout layout = leftToRight
      ? new FormLayout(leftToRightSpecs)
      : new FormLayout(OrientationUtils.flipped(leftToRightSpecs),
      new RowSpec[]{});

    DefaultFormBuilder builder = new DefaultFormBuilder(layout);
    builder.setLeftToRight(leftToRight);
    builder.border(Borders.DIALOG);

    builder.appendSeparator(title);
    builder.append("Level");
    builder.append(new JTextField(10), 3);

    builder.append("Radar", new JTextField(10));
    builder.append(new JTextField(10));

    return builder.getPanel();
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

  /**
   * <p>Handles the representation of the balance based on the current configuration</p>
   *
   * @param amount The amount
   * @param rate The equivalent rate in local currency
   */
  public void updateBalance(BigDecimal amount, String rate) {

    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();
    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration();

    String[] balance = Formats.formatBitcoinBalance(amount);

    BitcoinSymbol symbol = BitcoinSymbol.valueOf(bitcoinConfiguration.getBitcoinSymbol());

    if (i18nConfiguration.isCurrencySymbolPrefixed()) {

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

    } else {

      // Place currency symbol after the number
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

    balanceLHSLabel.setText(balance[0]);
    balanceRHSLabel.setText(balance[1]);
    exchangeLabel.setText(rate);
  }


  public JLabel getBalanceLHSLabel() {
    return balanceLHSLabel;
  }

  public JLabel getBalanceRHSLabel() {
    return balanceRHSLabel;
  }

  public JLabel getBalanceRHSSymbolLabel() {
    return balanceRHSSymbolLabel;
  }

  public JTree getSidebarTree() {
    return sidebarTree;
  }

  public JLabel getHelpLabel() {
    return helpLabel;
  }

  public JLabel getSettingsLabel() {
    return settingsLabel;
  }

  public JLabel getSignOutLabel() {
    return signOutLabel;
  }

  public JLabel getExchangeLabel() {
    return exchangeLabel;
  }
}