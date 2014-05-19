package org.multibit.hd.ui.views.wizards.language_settings;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.LanguageKey;
import org.multibit.hd.ui.languages.Languages;
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
import java.util.Locale;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Edit Contact: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class LanguageSettingsPanelView extends AbstractWizardPanelView<LanguageSettingsWizardModel, LanguageSettingsPanelModel> implements ActionListener {

  // Panel specific components
  private JComboBox<String> languagesComboBox;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public LanguageSettingsPanelView(AbstractWizard<LanguageSettingsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SHOW_LANGUAGE_WIZARD, AwesomeIcon.GLOBE);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(new LanguageSettingsPanelModel(
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

    LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage().deepCopy();
    Locale locale = languageConfiguration.getLocale();

    Preconditions.checkNotNull(locale, "'locale' cannot be empty");

    languagesComboBox = ComboBoxes.newLanguagesComboBox(this, locale);

    contentPanel.add(Labels.newLanguageChangeNote(), "growx,span 2,wrap");

    contentPanel.add(Labels.newSelectLanguageLabel(), "shrink,aligny top");
    contentPanel.add(languagesComboBox, "growx,width min:350:,push,aligny top,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<LanguageSettingsWizardModel> wizard) {

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

        languagesComboBox.requestFocusInWindow();

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    // Always call super() before hiding
    super.beforeHide(isExitCancel);

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
    String localeCode = LanguageKey.values()[source.getSelectedIndex()].getKey();

    // Determine the new locale
    Locale newLocale = Languages.newLocaleFromCode(localeCode);

    // Create a new configuration to reset the separators
    Configuration configuration = Configurations.currentConfiguration.deepCopy();
    LanguageConfiguration languageConfiguration = new LanguageConfiguration(newLocale);
    configuration.setLanguage(languageConfiguration);

    // Update the model
    getWizardModel().setConfiguration(configuration);

  }

}
