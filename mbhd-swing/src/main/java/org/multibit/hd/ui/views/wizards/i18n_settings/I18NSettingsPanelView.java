package org.multibit.hd.ui.views.wizards.i18n_settings;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.LanguageKey;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
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

public class I18NSettingsPanelView extends AbstractWizardPanelView<I18NSettingsWizardModel, I18NSettingsPanelModel> implements ActionListener {

  // Panel specific components
  private JComboBox<String> languagesComboBox;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public I18NSettingsPanelView(AbstractWizard<I18NSettingsWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.SHOW_I18N_WIZARD);

    PanelDecorator.addCancelApply(this, wizard);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(new I18NSettingsPanelModel(
      getPanelName(),
      configuration
    ));

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.GLOBE);

    panel.setLayout(new MigLayout(
      "fill,insets 0", // Layout constraints
      "[][]", // Column constraints
      "[][]" // Row constraints
    ));

    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration().deepCopy();
    Locale locale = i18nConfiguration.getLocale();

    Preconditions.checkNotNull(locale, "'locale' cannot be empty");

    languagesComboBox = ComboBoxes.newLanguagesComboBox(this, locale);

    panel.add(Labels.newI18NSettingsNote(), "growx,span 2,wrap");

    panel.add(Labels.newSelectLanguageLabel(),"shrink,aligny top");
    panel.add(languagesComboBox, "growx,width min:350:,push,aligny top,wrap");

    return panel;

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
    I18NConfiguration i18nConfiguration = new I18NConfiguration(newLocale);
    configuration.setI18NConfiguration(i18nConfiguration);

    // Retain the last selections (and current local currency)
    i18nConfiguration.setLocalCurrencyUnit(Configurations.currentConfiguration.getI18NConfiguration().getLocalCurrencyUnit());

    // Update the model
    getWizardModel().setConfiguration(configuration);

  }

}
