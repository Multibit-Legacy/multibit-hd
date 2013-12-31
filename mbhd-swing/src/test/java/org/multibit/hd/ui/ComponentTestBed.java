package org.multibit.hd.ui;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.LocaleChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Locale;

/**
 * <p>Utility to provide the following to custom components:</p>
 * <ul>
 * <li>Create a variety of components</li>
 * <li>Verify behaviour under different locales</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ComponentTestBed {

  private JFrame frame = null;

  /**
   * @param args Any command line arguments for the CoreServices
   */
  public ComponentTestBed(String[] args) {

    // Start the core services
    CoreServices.main(args);

    // Register for events
    CoreServices.uiEventBus.register(this);

  }

  /**
   * <p>Main entry point - see {@link org.multibit.hd.ui.ComponentTestBed#createTestPanel()} to configure the panel under test</p>
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {

    ComponentTestBed testBed = new ComponentTestBed(args);

    // See createTestPanel() to configure panel under test

    testBed.show();

  }

  /**
   * <p>Creates the panel under test</p>
   * <h3>Examples</h3>
   * <pre>
   *   return Wizards.newWelcomeWizard().getWizardPanel();
   * </pre>
   *
   * @return The panel under test
   */
  public JPanel createTestPanel() {

    // Choose a panel to test
    WelcomeWizard wizard = Wizards.newWelcomeWizard();
    wizard.show(Panels.CREATE_WALLET_PASSWORD_ACTION_NAME);
    return wizard.getWizardPanel();

  }

  @Subscribe
  public void onLocaleChangedEvent(LocaleChangedEvent event) {

    show();

  }

  @Subscribe
  public void onShutdownEvent(ShutdownEvent event) {

    frame.dispose();

  }

  /**
   * Show the frame with a fresh content pane made from the test panel and ancillary controls
   */
  private void show() {

    // Create the toggle button action
    Action toggleLocaleAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (Languages.currentLocale().getLanguage().startsWith("ar")) {
          Configurations.currentConfiguration.getI18NConfiguration().setLocale(Locale.UK);
        } else {
          Configurations.currentConfiguration.getI18NConfiguration().setLocale(new Locale("ar"));
        }

        JButton button = (JButton) e.getSource();
        button.setText(Languages.safeText(MessageKey.SELECT_LANGUAGE));

        ViewEvents.fireLocaleChangedEvent();

      }
    };

    // Create test bed controls
    JButton toggleLocaleButton = new JButton(toggleLocaleAction);
    toggleLocaleButton.setText(Languages.safeText(MessageKey.SELECT_LANGUAGE));

    // Set up the wrapping panel
    JPanel contentPanel = Panels.newPanel();
    contentPanel.setOpaque(true);

    contentPanel.add(createTestPanel(), "wrap");
    contentPanel.add(toggleLocaleButton,"center");

    // Set up the frame to use the minimum size

    if (frame == null) {
      frame = new JFrame("MultiBit HD Component Tester");
    }
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setContentPane(contentPanel);
    frame.pack();
    frame.setVisible(true);
  }

}
