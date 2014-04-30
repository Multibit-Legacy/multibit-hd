package org.multibit.hd.ui;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import com.xeiam.xchange.currency.MoneyUtils;
import net.miginfocom.swing.MigLayout;
import org.joda.money.CurrencyUnit;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.events.ConfigurationChangedEvent;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.HistoryService;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.controllers.HeaderController;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.SettingsChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.views.HeaderView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.themes.DarkTheme;
import org.multibit.hd.ui.views.themes.LightTheme;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.math.BigInteger;
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
   * <p>Creates the panel under test</p>
   * <h3>Welcome wizard</h3>
   * <pre>
   * AbstractWizard wizard = Wizards.newExitingWelcomeWizard(WelcomeWizardState.WELCOME_SELECT_LANGUAGE);
   * wizard.show(WelcomeWizardState.WELCOME_SELECT_LANGUAGE.name());
   * return wizard.getWizardPanel();
   * </pre>
   * <h3>Send bitcoin wizard</h3>
   * <pre>
   * AbstractWizard wizard = Wizards.newSendBitcoinWizard();
   * return wizard.getWizardPanel();
   * </pre>
   * <h3>Footer</h3>
   * <pre>
   *   return newHeaderView();
   *   return newFooterView();
   * </pre>
   * <h3>Detail screen</h3>
   * <pre>
   * AbstractScreenView screen = Screens.newScreen(Screen.CONTACTS);
   * return screen.newScreenViewPanel();
   * </pre>
   *
   */
  public JPanel createTestPanel() {

    AbstractWizard wizard = Wizards.newExitingWelcomeWizard(WelcomeWizardState.WELCOME_SELECT_LANGUAGE);
    wizard.show(WelcomeWizardState.WELCOME_SELECT_LANGUAGE.name());

    return wizard.getWizardScreenHolder();

  }
  /**
   * @param args Any command line arguments for the CoreServices
   */
  public ComponentTestBed(String[] args) {

    log.info("Starting CoreServices");

    // Start the core services
    CoreServices.main(args);

    BitcoinConfiguration bitcoinConfiguration = new BitcoinConfiguration();
    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.MICON.name());
    bitcoinConfiguration.setCurrentExchange(ExchangeKey.BITSTAMP.name());
    bitcoinConfiguration.setLocalCurrencyUnit(CurrencyUnit.USD);

    Configurations.currentConfiguration.setBitcoin(bitcoinConfiguration);

    // Register for events
    CoreServices.uiEventBus.register(this);

    // Standard support services
    CoreServices.newExchangeService(bitcoinConfiguration).start();
    CoreServices.getOrCreateBitcoinNetworkService().start();

    // Initialise the wallet manager, which will loadContacts the current wallet if available
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // TODO Refactor this into wallet service
//    WalletManager.INSTANCE.open(applicationDataDirectory);

    ContactService contactService = CoreServices.getOrCreateContactService(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletId());
    contactService.addDemoContacts();

    HistoryService historyService = CoreServices.getOrCreateHistoryService(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletId());
    historyService.addDemoHistory();
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

    testBed.createTestPanel();

    log.info("Showing component");

    testBed.show();

  }

  @Subscribe
  public void onConfigurationChangedEvent(ConfigurationChangedEvent event) {

    Locale locale = Configurations.currentConfiguration.getLocale();

    frame.setLocale(locale);

    // Ensure the resource bundle is reset
    ResourceBundle.clearCache();

    // Ensure LTR and RTL language formats are in place
    frame.applyComponentOrientation(ComponentOrientation.getOrientation(locale));

    // Update the views
    ViewEvents.fireSettingsChangedEvent();

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
  public void onSettingsChangedEvent(SettingsChangedEvent event) {

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
          Configurations.currentConfiguration.getLanguage().setLocale(Locale.UK);
        } else {
          Configurations.currentConfiguration.getLanguage().setLocale(new Locale("ar"));
        }

        JButton button = (JButton) e.getSource();
        button.setText(Languages.safeText(MessageKey.SELECT_LANGUAGE));

        ViewEvents.fireSettingsChangedEvent();

      }
    };

    // Create the toggle button action
    Action toggleThemeAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (Themes.currentTheme instanceof DarkTheme) {
          Themes.switchTheme(new LightTheme());
        } else {
          Themes.switchTheme(new DarkTheme());
        }

        ViewEvents.fireSettingsChangedEvent();

      }
    };

    log.info("Adding test bed controls");

    // Create test bed controls
    JButton toggleLocaleButton = new JButton(toggleLocaleAction);
    toggleLocaleButton.setText(Languages.safeText(MessageKey.SELECT_LANGUAGE));
    JButton toggleThemeButton = new JButton(toggleThemeAction);
    toggleThemeButton.setText("Theme");

    // Set up the frame to use the minimum size

    log.info("Set up frame");

    if (frame == null) {
      frame = new JFrame("MultiBit HD Component Tester");
    } else {
      frame.remove(contentPanel);
    }

    // Set up the wrapping panel
    contentPanel = Panels.newPanel(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Columns
      "[][]" // Rows
    ));
    contentPanel.setOpaque(true);

    log.info("Adding test panel");
    contentPanel.add(createTestPanel(), "grow,push,span 2,wrap");

    contentPanel.add(toggleLocaleButton, "push");
    contentPanel.add(toggleThemeButton, "shrink");

    log.info("Packing and presenting");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.add(contentPanel);
    frame.pack();
    frame.setVisible(true);

    log.info("Done");

  }

  /**
   * @return A new header view with simulated events
   */
  private JPanel newHeaderView() {

    HeaderController controller = new HeaderController();
    HeaderView view = new HeaderView();

    SafeExecutors.newFixedThreadPool(1,"testbed").execute(new Runnable() {

      int i = 0;

      @Override
      public void run() {

        while (i < 10) {

          Uninterruptibles.sleepUninterruptibly(800, TimeUnit.MILLISECONDS);

          ViewEvents.fireBalanceChangedEvent(
            BigInteger.valueOf(100_000_000_000L),
            MoneyUtils.parse("USD 999999999.00"),
            Optional.of("Example")
          );

          if (i % 2 == 0) {

            ControllerEvents.fireAddAlertEvent(Models.newAlertModel("Something happened", RAGStatus.RED));

          }
          if (i % 3 == 0) {

            ControllerEvents.fireAddAlertEvent(Models.newAlertModel("Something happened", RAGStatus.AMBER));

          }
          if (i % 5 == 0) {

            ControllerEvents.fireAddAlertEvent(Models.newAlertModel("Something happened", RAGStatus.GREEN));
            Sounds.playReceiveBitcoin();

          }

          i++;
        }
      }
    });

    return view.getContentPanel();
  }

}
