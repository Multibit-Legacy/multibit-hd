package org.multibit.hd.ui.views.wizards.application_settings;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.ApplicationConfiguration;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.ThemeKey;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Application settings: switch theme</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class ApplicationSettingsPanelView extends AbstractWizardPanelView<ApplicationSettingsWizardModel, ApplicationSettingsPanelModel> implements ActionListener {

  // Panel specific components
  private JComboBox<String> themesComboBox;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public ApplicationSettingsPanelView(AbstractWizard<ApplicationSettingsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SHOW_APPLICATION_WIZARD, AwesomeIcon.WRENCH);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(new ApplicationSettingsPanelModel(
      getPanelName(),
      configuration
    ));

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[][]" // Row constraints
    ));

    ApplicationConfiguration applicationConfiguration = Configurations.currentConfiguration.getApplicationConfiguration().deepCopy();

    themesComboBox = ComboBoxes.newThemesComboBox(this, applicationConfiguration);

    contentPanel.add(Labels.newThemeChangeNote(), "growx,span 2,wrap");

    contentPanel.add(Labels.newSelectThemeLabel(), "shrink");
    contentPanel.add(themesComboBox, "growx,shrinky,width min:250:,push,wrap");
    contentPanel.add(Labels.newBlankLabel(), "grow,span 2,push,wrap"); // Fill out the remainder

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ApplicationSettingsWizardModel> wizard) {

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

        themesComboBox.requestFocusInWindow();

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


  }


  /**
   * <p>Handle one of the combo boxes changing</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    String themeName = ThemeKey.values()[source.getSelectedIndex()].name();

    // Create a new configuration to reset the separators
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
    applicationConfiguration.setCurrentTheme(themeName);
    configuration.setApplicationConfiguration(applicationConfiguration);

    // Update the model
    getWizardModel().setConfiguration(configuration);

  }

}
