package org.multibit.hd.ui.views.screens.settings;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the tools detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class SettingsScreenView extends AbstractScreenView<SettingsScreenModel>  {

  private JButton showI18NSettingsWizardButton;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public SettingsScreenView(SettingsScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel newScreenViewPanel() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill", // Layout constraints
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    Action showI18nAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newI18NSettingsWizard().getWizardPanel());
      }
    };
    Action showBitcoinAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        //Panels.showLightBox(Wizards.newBitcoinSettingsWizard().getWizardPanel());
      }
    };
    Action showApplicationAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        //Panels.showLightBox(Wizards.newApplicationSettingsWizard().getWizardPanel());
      }
    };
    Action showSoundAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        //Panels.showLightBox(Wizards.newSoundSettingsWizard().getWizardPanel());
      }
    };

    showI18NSettingsWizardButton = Buttons.newShowI18NSettingsWizardButton(showI18nAction);

    contentPanel.add(showI18NSettingsWizardButton, MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowBitcoinSettingsWizardButton(showBitcoinAction), MultiBitUI.LARGE_BUTTON_MIG + ",align center, push,wrap");

    contentPanel.add(Buttons.newShowApplicationSettingsWizardButton(showApplicationAction), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowSoundSettingsWizardButton(showSoundAction), MultiBitUI.LARGE_BUTTON_MIG + ",align center, push,wrap");


    return contentPanel;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(
      new Runnable() {
      @Override
      public void run() {

        showI18NSettingsWizardButton.requestFocusInWindow();

      }
    });

  }
}