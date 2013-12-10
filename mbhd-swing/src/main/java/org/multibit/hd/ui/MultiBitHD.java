package org.multibit.hd.ui;

import com.xeiam.xchange.currency.MoneyUtils;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.hd.core.exceptions.WalletVersionException;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeTickerService;
import org.multibit.hd.ui.controllers.HeaderController;
import org.multibit.hd.ui.controllers.MainController;
import org.multibit.hd.ui.controllers.SidebarController;
import org.multibit.hd.ui.events.LocaleChangeEvent;
import org.multibit.hd.ui.events.ViewEvents;
import org.multibit.hd.ui.platform.GenericApplication;
import org.multibit.hd.ui.views.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
    CoreServices.uiEventBus.post(new LocaleChangeEvent(Configurations.currentConfiguration.getLocale()));

    // Provide a starting balance
    // TODO Get this from CoreServices
    ViewEvents.fireBalanceChangeEvent(
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
