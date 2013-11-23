package org.multibit.hd.ui;

import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeService;
import org.multibit.hd.ui.platform.GenericApplication;
import org.multibit.hd.ui.swing.controllers.MainController;
import org.multibit.hd.ui.swing.views.MainView;
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

    ExchangeService exchangeService = CoreServices.newExchangeService();
    exchangeService.start();

    // Create the views
    final MainView mainView = new MainView();
    mainView.pack();
    mainView.setVisible(true);

    // Create the controllers
    final MainController mainController = new MainController(mainView, exchangeService.getTickerQueue());
    mainController.start();
  }

  private void registerEventListeners() {

    // TODO Get this working

    log.info("Configuring native event handling");

  }
}
