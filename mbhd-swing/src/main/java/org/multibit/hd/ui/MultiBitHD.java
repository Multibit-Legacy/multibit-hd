package org.multibit.hd.ui;

import com.google.bitcoin.core.Wallet;
import com.google.common.util.concurrent.Uninterruptibles;
import com.xeiam.xchange.currency.MoneyUtils;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.managers.InstallationManager;
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

import java.io.File;
import java.util.List;
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

    // See if there are any existing wallets stored in the application data directory
    String applicationDataDirectoryName = InstallationManager.createApplicationDataDirectory();

    WalletManager walletManager = new WalletManager();
    List<File> walletDirectories = walletManager.findWalletDirectories(new File(applicationDataDirectoryName));

    if (walletDirectories.isEmpty()) {
      // Start the services (triggers events)
      exchangeTickerService.start();
      bitcoinNetworkService.start();

      // Show the UI for the current locale
      ControllerEvents.fireChangeLocaleEvent(Configurations.currentConfiguration.getLocale());

      Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

      // There are no existing wallets so start the new wallet wizard
      Panels.showLightBox(Wizards.newWelcomeWizard().getWizardPanel());
    } else {
      // Load up the first wallet
      String walletToLoad = walletDirectories.get(0).getAbsoluteFile() + File.separator + WalletManager.MBHD_WALLET_NAME;
      log.debug("Loading wallet '" + walletToLoad + "'  . . .");
      Wallet wallet = walletManager.loadFromFile(new File(walletToLoad));
      log.debug("Loaded wallet '" + walletToLoad + "' successfully.");

      // TODO enable the user to switch between the existing wallets
      // TODO actually do something with the wallet

      // Start the services (triggers events)
      exchangeTickerService.start();
      bitcoinNetworkService.start();

      // If the network starts ok start downloading blocks to catch up with the current blockchain
      bitcoinNetworkService.downloadBlockChain();

      // Show the UI for the current locale
      ControllerEvents.fireChangeLocaleEvent(Configurations.currentConfiguration.getLocale());

    }


    // Provide a starting balance
    // TODO Get this from CoreServices
    ViewEvents.fireBalanceChangedEvent(
            MoneyUtils.fromSatoshi(0),
            MoneyUtils.fromSatoshi(0),
            "Unknown"
    );
  }

  private void registerEventListeners() {

    // TODO Get this working

    log.info("Configuring native event handling");

  }
}
