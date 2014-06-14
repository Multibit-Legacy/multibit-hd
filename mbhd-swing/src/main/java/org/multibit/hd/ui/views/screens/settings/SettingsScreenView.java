package org.multibit.hd.ui.views.screens.settings;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.MessageKey;
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

  private JButton showLanguageSettingsWizardButton;

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
  public JPanel initialiseScreenViewPanel() {

    MigLayout layout = new MigLayout(
      Panels.migXYDetailLayout(),
      "6[]6[]6[]6", // Column constraints
      "6[]6[]6" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    Action showLanguageAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newLanguageSettingsWizard().getWizardScreenHolder());
      }
    };
    Action showBitcoinAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newUnitsSettingsWizard().getWizardScreenHolder());
      }
    };
    Action showExchangeAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newExchangeSettingsWizard().getWizardScreenHolder());
      }
    };
    Action showApplicationAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newApplicationSettingsWizard().getWizardScreenHolder());
      }
    };
    Action showSoundAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newSoundSettingsWizard().getWizardScreenHolder());
      }
    };
    Action showLabAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newLabSettingsWizard().getWizardScreenHolder());
      }
    };

    showLanguageSettingsWizardButton = Buttons.newShowLanguageSettingsWizardButton(showLanguageAction);

    contentPanel.add(showLanguageSettingsWizardButton, MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowUnitsSettingsWizardButton(showBitcoinAction), MultiBitUI.LARGE_BUTTON_MIG + ",align center, push");
    contentPanel.add(Buttons.newShowExchangeSettingsWizardButton(showExchangeAction), MultiBitUI.LARGE_BUTTON_MIG + ",align center, push,wrap");

    contentPanel.add(Buttons.newShowApplicationSettingsWizardButton(showApplicationAction), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowSoundSettingsWizardButton(showSoundAction), MultiBitUI.LARGE_BUTTON_MIG + ",align center, push");
    contentPanel.add(Buttons.newShowLabSettingsWizardButton(showLabAction), MultiBitUI.LARGE_BUTTON_MIG + ",align center, push,wrap");

    return contentPanel;
  }

}