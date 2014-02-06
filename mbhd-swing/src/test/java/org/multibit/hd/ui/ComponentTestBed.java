package org.multibit.hd.ui;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ChangeLocaleEvent;
import org.multibit.hd.ui.events.view.LocaleChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

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

  private static final Logger log = LoggerFactory.getLogger(ComponentTestBed.class);

  private JFrame frame = null;
  private JPanel contentPanel = null;

  /**
   * @param args Any command line arguments for the CoreServices
   */
  public ComponentTestBed(String[] args) {

    log.info("Starting CoreServices");

    // Start the core services
    CoreServices.main(args);

    Configurations.currentConfiguration.getBitcoinConfiguration().setBitcoinSymbol("satoshi");

    // Register for events
    CoreServices.uiEventBus.register(this);

    // Standard support services
    CoreServices.newExchangeService(MtGoxExchange.class.getName()).start();
    CoreServices.newBitcoinNetworkService().start();

  }

  /**
   * <p>Main entry point - see {@link org.multibit.hd.ui.ComponentTestBed#createTestPanel()} to configure the panel under test</p>
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) throws UnsupportedLookAndFeelException {

    log.info("Starting component test bed");

    // We guarantee the JDK version through the packager so we can use this direct
    UIManager.setLookAndFeel(new NimbusLookAndFeel());

    ComponentTestBed testBed = new ComponentTestBed(args);

    // See createTestPanel() to configure panel under test

    log.info("Showing component");

    testBed.show();

  }

  /**
   * <p>Creates the panel under test</p>
   * <h3>Wizards</h3>
   * <pre>
   * AbstractWizard wizard = Wizards.newExitingWelcomeWizard(WelcomeWizardState.WELCOME_SELECT_LANGUAGE);
   * wizard.show(WelcomeWizardState.WELCOME_SELECT_LANGUAGE.name());
   * return wizard.getWizardPanel();
   * </pre>
   * <h3>Detail views</h3>
   * <pre>
   * AbstractScreenView screen = Screens.newScreen(Screen.CONTACTS);
   * return screen.getScreenPanel();
   * </pre>
   *
   * @return The panel under test
   */
  public JPanel createTestPanel() {

    // Choose a panel to test
//    AbstractScreenView screen = Screens.newScreen(Screen.CONTACTS);
//    return screen.newScreenViewPanel();

    AbstractWizard wizard = Wizards.newExitingWelcomeWizard(WelcomeWizardState.CREATE_WALLET_SEED_PHRASE);
    wizard.show(WelcomeWizardState.CREATE_WALLET_SEED_PHRASE.name());
    return wizard.getWizardPanel();
  }

  @Subscribe
  public void onChangeLocaleEvent(ChangeLocaleEvent event) {

    Locale locale = event.getLocale();

    Locale.setDefault(locale);
    frame.setLocale(locale);

    // Ensure the resource bundle is reset
    ResourceBundle.clearCache();

    // Update the main configuration
    Configurations.currentConfiguration.getI18NConfiguration().setLocale(locale);

    // Ensure LTR and RTL language formats are in place
    frame.applyComponentOrientation(ComponentOrientation.getOrientation(locale));

    // Update the views
    ViewEvents.fireLocaleChangedEvent();

    // Allow time for the views to update
    Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

    // Ensure the Swing thread can perform a complete refresh
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        frame.invalidate();
      }
    });

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

    log.info("Adding test bed controls");

    // Create test bed controls
    JButton toggleLocaleButton = new JButton(toggleLocaleAction);
    toggleLocaleButton.setText(Languages.safeText(MessageKey.SELECT_LANGUAGE));

    // Set up the frame to use the minimum size

    log.info("Set up frame");

    if (frame == null) {
      frame = new JFrame("MultiBit HD Component Tester");
    } else {
      frame.remove(contentPanel);
    }

    // Set up the wrapping panel
    contentPanel = Panels.newPanel();
    contentPanel.setOpaque(true);

    log.info("Adding test panel");
    contentPanel.add(createTestPanel(), "wrap");
    contentPanel.add(toggleLocaleButton, "center");

    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.add(contentPanel);
    frame.pack();
    frame.setVisible(true);

    log.info("Done");

  }

}
