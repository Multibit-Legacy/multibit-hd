package org.multibit.hd.ui;

import com.google.common.util.concurrent.Uninterruptibles;
import com.xeiam.xchange.currency.MoneyUtils;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.hd.core.exceptions.WalletVersionException;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeTickerService;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.controllers.HeaderController;
import org.multibit.hd.ui.controllers.MainController;
import org.multibit.hd.ui.controllers.SidebarController;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.platform.GenericApplication;
import org.multibit.hd.ui.views.*;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>Main entry point to the application</p>
 */
public class MultiBitHD {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHD.class);

  // TODO Implement this
  private static GenericApplication genericApplication = null;

  /**
   * <p>Main entry point to the application</p>
   *
   * @param args None specified
   */
  public static void main(final String[] args) throws InterruptedException {

    // Start the core services
    CoreServices.main(args);

    // Pre-load sound library
    Sounds.initialise();

    ExchangeTickerService exchangeTickerService = CoreServices.newExchangeService(MtGoxExchange.class.getName());
    BitcoinNetworkService bitcoinNetworkService = CoreServices.newBitcoinNetworkService();

    // Create views
    HeaderView headerView = new HeaderView();
    SidebarView sidebarView = new SidebarView();
    DetailView detailView = new DetailView();
    FooterView footerView = new FooterView();

    // Allow time for initialisation
    Uninterruptibles.sleepUninterruptibly(500,TimeUnit.MILLISECONDS);

    MainView mainView = new MainView(
      headerView.getContentPanel(),
      sidebarView.getContentPanel(),
      detailView.getContentPanel(),
      footerView.getContentPanel()
    );

    // Create controllers
    MainController mainController = new MainController();
    HeaderController headerController = new HeaderController();
    SidebarController sidebarController = new SidebarController();

    // Create or load a simple wallet
    WalletManager walletManager = new WalletManager();
    try {
      walletManager.createSimpleWallet("password");  // TODO replace with HDWallet
    } catch (IOException | WalletLoadException |WalletVersionException e) {
      // TODO error should also appear on UI
      log.error(e.getClass().getName() + " " + e.getMessage());
    }

    // Start the services (triggers events)
    exchangeTickerService.start();
    bitcoinNetworkService.start();

    // If the network starts ok start downloading blocks to catch up with the current blockchain
    bitcoinNetworkService.downloadBlockChain();

    // Show the UI for the current locale
    ControllerEvents.fireChangeLocaleEvent(Configurations.currentConfiguration.getLocale());

    // Provide a starting balance
    // TODO Get this from CoreServices
    ViewEvents.fireBalanceChangedEvent(
      MoneyUtils.fromSatoshi(0),
      MoneyUtils.fromSatoshi(0),
      "Unknown"
    );

    // TODO Check configuration before determining lightbox
    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

    Panels.showLightBox(Wizards.newWelcomeWizard().getWizardPanel());

  }

  private void registerEventListeners() {

    // TODO Get this working

    log.info("Configuring native event handling");

  }
}
