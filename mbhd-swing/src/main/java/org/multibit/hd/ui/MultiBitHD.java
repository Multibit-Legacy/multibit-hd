package org.multibit.hd.ui;

import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeTickerService;
import org.multibit.hd.ui.controllers.FooterController;
import org.multibit.hd.ui.controllers.HeaderController;
import org.multibit.hd.ui.controllers.MainController;
import org.multibit.hd.ui.controllers.SidebarController;
import org.multibit.hd.ui.events.LocaleChangeEvent;
import org.multibit.hd.ui.platform.GenericApplication;
import org.multibit.hd.ui.views.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public static void main(final String[] args) {

    // Start the core services
    CoreServices.main(args);

    ExchangeTickerService exchangeTickerService = CoreServices.newExchangeService(MtGoxExchange.class.getName());
    exchangeTickerService.initialise();

    // Create views
    HeaderView headerView = new HeaderView();
    FooterView footerView = new FooterView();
    SidebarView sidebarView = new SidebarView();
    DetailView detailView = new DetailView();

    MainView mainView = new MainView(
      headerView.getContentPanel(),
      footerView.getContentPanel(),
      sidebarView.getContentPanel(),
      detailView.getContentPanel()
    );

    // Create controllers
    MainController mainController = new MainController();
    HeaderController headerController = new HeaderController();
    SidebarController sidebarController = new SidebarController();
    FooterController footerController = new FooterController();

    // Start the services (triggers events)
    exchangeTickerService.start();

    // Show the UI for the current locale
    CoreServices.uiEventBus.post(new LocaleChangeEvent(Configurations.currentConfiguration.getLocale()));

  }

  private void registerEventListeners() {

    // TODO Get this working

    log.info("Configuring native event handling");

  }
}
