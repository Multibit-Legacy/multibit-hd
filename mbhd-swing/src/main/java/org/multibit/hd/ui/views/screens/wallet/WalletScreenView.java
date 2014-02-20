package org.multibit.hd.ui.views.screens.wallet;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetailModel;
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetailView;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;
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
  public JPanel newScreenViewPanel() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill,insets 0", // Layout constraints
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    Action showSendBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newSendBitcoinWizard().getWizardPanel());
      }
    };

    Action showRequestBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newRequestBitcoinWizard().getWizardPanel());
      }
    };

    sendBitcoin = Buttons.newSendBitcoinWizardButton(showSendBitcoinWizardAction);
    requestBitcoin = Buttons.newRequestBitcoinWizardButton(showRequestBitcoinWizardAction);

    walletDetailMaV = Components.newWalletDetailMaV(PANEL_NAME);
    contentPanel.add(sendBitcoin, MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(requestBitcoin, MultiBitUI.LARGE_BUTTON_MIG + ",align center,push,wrap");
    contentPanel.add(walletDetailMaV.getView().newComponentPanel(), "span 2,grow");

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
