package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Inform the user about attaching their hardware wallet</li>
 * </ul>
 *
 * @since 0.1.0
 */

public class WelcomeAttachHardwareWalletPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> {

  /**
   * Handles periodic increments of rotation
   */
  private final Timer timer;

  private int timerCount = 1;
  private JLabel note1Icon;
  private JLabel note1Label;
  private JLabel note2Icon;
  private JLabel note2Label;
  private JLabel note3Icon;
  private JLabel note3Label;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public WelcomeAttachHardwareWalletPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.ATTACH_HARDWARE_WALLET_TITLE, AwesomeIcon.SHIELD);

    // Timer needs to be fairly fast to appear responsive
    timer = new Timer(500, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Guaranteed to be on the EDT
        updateFromComponentModels(Optional.absent());

      }
    });

  }

  @Override
  public void newPanelModel() {

    // No need to bind this to the wizard model

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
        Panels.migXYLayout(),
        "[]20[]", // Column constraints
        "10[40]10[40]10[40]10[40]10[40]10[40]10" // Row constraints
      ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Note 1
    note1Icon = Labels.newIconLabel(AwesomeIcon.SHIELD, Optional.<MessageKey>absent(), null);
    contentPanel.add(note1Icon, "shrink");
    note1Label = Labels.newNoteLabel(MessageKey.ATTACH_HARDWARE_WALLET_NOTE_1, null);
    contentPanel.add(note1Label, MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    // Note 2
    note2Icon = Labels.newIconLabel(AwesomeIcon.PLUG, Optional.<MessageKey>absent(), null);
    contentPanel.add(note2Icon, "shrink");
    note2Label = Labels.newNoteLabel(MessageKey.ATTACH_HARDWARE_WALLET_NOTE_2, null);
    contentPanel.add(note2Label, MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    note2Icon.setVisible(false);
    note2Label.setVisible(false);

    // Note 3
    note3Icon = Labels.newIconLabel(AwesomeIcon.EDIT, Optional.<MessageKey>absent(), null);
    contentPanel.add(note3Icon, "shrink");
    note3Label = Labels.newNoteLabel(MessageKey.ATTACH_HARDWARE_WALLET_NOTE_3, null);
    contentPanel.add(note3Label, MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    note3Icon.setVisible(false);
    note3Label.setVisible(false);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Disable the next button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    switch (timerCount) {
      case 0:
        // Note 1
        note1Icon.setVisible(true);
        note1Label.setVisible(true);
        break;
      case 1:
        // Note 2
        note2Icon.setVisible(true);
        note2Label.setVisible(true);
        break;
      case 2:
        // Note 3
        note3Icon.setVisible(true);
        note3Label.setVisible(true);
        break;
      default:
        timer.stop();

        // Enable the Next button
        ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

    }

    timerCount++;
  }

  @Override
  public void afterShow() {

    getNextButton().requestFocusInWindow();

    // Run continuously
    timer.setRepeats(true);
    timer.start();

  }

}
