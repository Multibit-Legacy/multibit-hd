package org.multibit.hd.ui.views.screens.wallet;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentType;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetailModel;
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetailView;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the wallet detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WalletScreenView extends AbstractScreenView<WalletScreenModel> {

  private JButton sendBitcoin;
  private JButton requestBitcoin;

  private JPanel recentSendsPanel;
  private JPanel recentReceivesPanel;

  private JPanel dashedVerticalSeparator;
  private JPanel dashedHorizontalSeparator;

  private final static String PANEL_NAME = "walletDetail";

  private ModelAndView<WalletDetailModel, WalletDetailView> walletDetailMaV;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public WalletScreenView(WalletScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {

    // Only register when there is something to do
    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      Panels.migXYLayout(),
      "[]10[]", // Column constraints
      "10[]10[]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    Action showSendBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newSendBitcoinWizard().getWizardScreenHolder());
      }
    };

    Action showRequestBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newRequestBitcoinWizard().getWizardScreenHolder());
      }
    };

    sendBitcoin = Buttons.newSendBitcoinWizardButton(showSendBitcoinWizardAction);
    requestBitcoin = Buttons.newRequestBitcoinWizardButton(showRequestBitcoinWizardAction);

    recentReceivesPanel = Components.newRecenPaymentsPanel(PaymentType.RECEIVING);
    recentSendsPanel = Components.newRecenPaymentsPanel(PaymentType.SENDING);

    dashedVerticalSeparator = new JPanel();
    dashedVerticalSeparator.setMaximumSize(new Dimension(1, 10000));
    Paint paint = new GradientPaint(0, 0, Themes.currentTheme.detailPanelBackground(),
            30, 30, Themes.currentTheme.fadedText().brighter(), true);
    dashedVerticalSeparator.setBorder(BorderFactory.createDashedBorder(paint, 4, 0));

    dashedHorizontalSeparator = new JPanel();
    dashedHorizontalSeparator.setMaximumSize(new Dimension(10000, 1));
    dashedHorizontalSeparator.setBorder(BorderFactory.createDashedBorder(paint, 4, 0));

     walletDetailMaV = Components.newWalletDetailMaV(PANEL_NAME);
    contentPanel.add(sendBitcoin, MultiBitUI.LARGE_BUTTON_MIG + ",align center");
    contentPanel.add(dashedVerticalSeparator, "growy, spany 2");
    contentPanel.add(requestBitcoin, MultiBitUI.LARGE_BUTTON_MIG + ",align center, wrap");
    contentPanel.add(recentSendsPanel, "grow, push");
    contentPanel.add(recentReceivesPanel, "grow, push, wrap");
    contentPanel.add(dashedHorizontalSeparator, "span 3, growx, wrap");
    contentPanel.add(walletDetailMaV.getView().newComponentPanel(), "span 3");

    return contentPanel;

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        sendBitcoin.requestFocusInWindow();
      }
    });

  }

}
