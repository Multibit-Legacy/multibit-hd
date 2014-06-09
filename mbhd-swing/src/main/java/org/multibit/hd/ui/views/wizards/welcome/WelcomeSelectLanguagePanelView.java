package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.LanguageKey;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertModel;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertView;
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
 * <p>Wizard panel to provide the following to UI:</p>
 * <ul>
 * <li>Welcome users to the application and allow them to select a language</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WelcomeSelectLanguagePanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> implements ActionListener {

  private ModelAndView<DisplaySecurityAlertModel, DisplaySecurityAlertView> displaySecurityPopoverMaV;
  private JComboBox<String> languagesComboBox;
  private ListeningExecutorService localeExecutorService = SafeExecutors.newSingleThreadExecutor("locale-change");

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public WelcomeSelectLanguagePanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.WELCOME_TITLE, AwesomeIcon.GLOBE);

  }

  @Override
  public void newPanelModel() {

    displaySecurityPopoverMaV = Popovers.newDisplaySecurityPopoverMaV(getPanelName());

    String localeCode = Languages.currentLocale().getLanguage();
    setPanelModel(localeCode);

    // Bind it to the wizard model
    getWizardModel().setLocaleCode(localeCode);

    // Register components
    registerComponents(displaySecurityPopoverMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[][]20[]30" // Row constraints
    ));

    languagesComboBox = ComboBoxes.newLanguagesComboBox(this, Languages.currentLocale());

    contentPanel.add(Labels.newSelectLanguageLabel(), "shrink");
    contentPanel.add(languagesComboBox, "growx," + MultiBitUI.COMBO_BOX_WIDTH_MIG + ",push,wrap");
    contentPanel.add(Labels.newWelcomeNote(), "grow,push,span 2,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the "next" button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        languagesComboBox.requestFocus();

        // Check for any security alerts
        Optional<SecurityEvent> securityEvent = CoreServices.getApplicationEventService().getLatestSecurityEvent();
        if (securityEvent.isPresent()) {

          displaySecurityPopoverMaV.getModel().setValue(securityEvent.get());

          // Show the security alert as a popover
          Panels.showLightBoxPopover(displaySecurityPopoverMaV.getView().newComponentPanel());

        }

      }
    });
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing - panel model is updated via an action and wizard model is not applicable

  }

  /**
   * <p>Handle the change locale action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(final ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    String localeCode = LanguageKey.values()[source.getSelectedIndex()].getKey();

    log.debug("Language changed to '{}'", localeCode);

    // Prevent further events
    source.setEnabled(false);

    // Determine the new locale
    Locale newLocale = Languages.newLocaleFromCode(localeCode);

    // Update the main configuration
    Configuration newConfiguration = Configurations.currentConfiguration.deepCopy();
    newConfiguration.getLanguage().setLocale(newLocale);

    log.debug("Simulating a wizard close event with configuration change");

    // Trigger the wizard hide process manually (no suitable button available)
    // using a deferred hide (control passes directly to handleHide)
    ViewEvents.fireWizardDeferredHideEvent(getPanelName(), false);

    // Make the switch immediately
    Configurations.switchConfiguration(newConfiguration);


  }
}
