package org.multibit.hd.ui.views.screens.tools;

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
 */
public class ToolsScreenView extends AbstractScreenView<ToolsScreenModel> {

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public ToolsScreenView(ToolsScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {

    MigLayout layout = new MigLayout(
      Panels.migXYDetailLayout(),
      "6[]6[]6[]6[]6", // Column constraints
      "6[]6[]6" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    // Row 1
    contentPanel.add(Buttons.newLargeShowSignMessageWizardButton(getShowSignMessageWizardAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowVerifyMessageWizardButton(getShowVerifyMessageWizardAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowVerifyNetworkButton(getShowVerifyNetworkAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    // Trezor tool is in top right for good visibility
    contentPanel.add(Buttons.newShowUseTrezorWizardButton(getShowUseTrezorWizardAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push,wrap");

    // Row 2
    contentPanel.add(Buttons.newShowAboutButton(getShowAboutAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push, wrap");

    setInitialised(true);
    return contentPanel;
  }

  /**
   * @return An action to show the "welcome wizard"
   */
  private AbstractAction getShowAboutAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Show the About screen
        Panels.showLightBox(Wizards.newAboutWizard().getWizardScreenHolder());

      }
    };
  }

  /**
   * @return An action to show the "Use Trezor" wizard
   */
  private AbstractAction getShowUseTrezorWizardAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Show the Use Trezor screen
        Panels.showLightBox(Wizards.newUseTrezorWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * @return An action to show the "sign message" wizard
   */
  private AbstractAction getShowSignMessageWizardAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newSignMessageWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * @return An action to show the "verify message" wizard
   */
  private AbstractAction getShowVerifyMessageWizardAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newVerifyMessageWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * @return An action to show the "verify network" tool
   */
  private AbstractAction getShowVerifyNetworkAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newVerifyNetworkWizard().getWizardScreenHolder());
      }
    };
  }

}
