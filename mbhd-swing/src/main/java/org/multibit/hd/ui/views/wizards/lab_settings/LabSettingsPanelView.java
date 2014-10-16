package org.multibit.hd.ui.views.wizards.lab_settings;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Lab settings: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */

public class LabSettingsPanelView extends AbstractWizardPanelView<LabSettingsWizardModel, LabSettingsPanelModel> implements ActionListener {

  // Panel specific components
  private JComboBox<String> torYesNoComboBox;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public LabSettingsPanelView(AbstractWizard<LabSettingsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SHOW_LAB_WIZARD, AwesomeIcon.FLASK);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(new LabSettingsPanelModel(
      getPanelName(),
      configuration
    ));

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[][][]" // Row constraints
    ));

    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    torYesNoComboBox = ComboBoxes.newTorYesNoComboBox(this, configuration.isTor());

    contentPanel.add(Labels.newLabChangeNote(), "growx,span 2,wrap");

    contentPanel.add(Labels.newSelectTor(), "shrink");
    contentPanel.add(torYesNoComboBox, "growx,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<LabSettingsWizardModel> wizard) {

    PanelDecorator.addCancelApply(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        torYesNoComboBox.requestFocusInWindow();

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Switch the main configuration over to the new one
      Configurations.switchConfiguration(getWizardModel().getConfiguration());

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing

  }


  /**
   * <p>Handle one of the combo boxes changing</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    // Create a new configuration
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    JComboBox source = (JComboBox) e.getSource();
    if (ComboBoxes.TOR_COMMAND.equals(e.getActionCommand())) {
      configuration.setTor(source.getSelectedIndex() == 0);
    }

    // Update the model

    getWizardModel().setConfiguration(configuration);

  }

}
