package org.multibit.hd.ui.views.wizards.appearance_settings;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.AppearanceConfiguration;
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
 * <li>Appearance settings: switch theme</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */

public class AppearanceSettingsPanelView extends AbstractWizardPanelView<AppearanceSettingsWizardModel, AppearanceSettingsPanelModel> implements ActionListener {

  // Panel specific components
  private JComboBox<String> themesComboBox;
  private JComboBox<String> showBalanceComboBox;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public AppearanceSettingsPanelView(AbstractWizard<AppearanceSettingsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SHOW_APPEARANCE_WIZARD, AwesomeIcon.DESKTOP);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(new AppearanceSettingsPanelModel(
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

    AppearanceConfiguration appearanceConfiguration = Configurations.currentConfiguration.getAppearance().deepCopy();

    themesComboBox = ComboBoxes.newThemesComboBox(this);
    showBalanceComboBox = ComboBoxes.newShowBalanceYesNoComboBox(this, appearanceConfiguration.isShowBalance());

    contentPanel.add(Labels.newSelectTheme(), "shrink");
    contentPanel.add(themesComboBox, "growx,shrinky,width min:250:,push,wrap");

    contentPanel.add(Labels.newShowBalance(), "shrink");
    contentPanel.add(showBalanceComboBox, "growx,shrinky,width min:250:,push,wrap");

    contentPanel.add(Labels.newBlankLabel(), "grow,span 2,push,wrap"); // Fill out the remainder

  }

  @Override
  protected void initialiseButtons(AbstractWizard<AppearanceSettingsWizardModel> wizard) {

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

    // Do nothing

  }

  /**
   * <p>Handle one of the combo boxes changing</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();

    // Themes
    if (ComboBoxes.THEMES_COMMAND.equalsIgnoreCase(e.getActionCommand())) {

      String themeName = ThemeKey.values()[source.getSelectedIndex()].name();

      Configuration configuration = getWizardModel().getConfiguration();
      configuration.getAppearance().setCurrentTheme(themeName);

      // Update the model
      getWizardModel().setConfiguration(configuration);

    }

    // Show balance
    if (ComboBoxes.SHOW_BALANCE_COMMAND.equalsIgnoreCase(e.getActionCommand())) {

      boolean showBalance = source.getSelectedIndex() == 0;

      Configuration configuration = getWizardModel().getConfiguration();
      configuration.getAppearance().setShowBalance(showBalance);

      // Update the model
      getWizardModel().setConfiguration(configuration);

    }

  }

}
