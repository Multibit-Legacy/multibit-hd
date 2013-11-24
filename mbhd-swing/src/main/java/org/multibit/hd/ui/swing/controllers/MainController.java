package org.multibit.hd.ui.swing.controllers;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.api.BalanceChangeEvent;
import org.multibit.hd.ui.swing.views.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * <p>Controller for the Mmin view </p>
 * <ul>
 * <li>Handles interaction between the model and the view</li>
 * </ul>
 */
public class MainController {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  private final MainView mainView;

  public MainController(MainView mainView) {

    this.mainView = mainView;

  }

  @Subscribe
  public void onBalanceChanged(BalanceChangeEvent event) {

    // Build the exchange string
    // TODO (GR) i18n
    String rate = String.format("~ %s (%s)",
      event.getLocalAmount().multiply(new BigDecimal("12.3456")).toPlainString(),
      event.getExchangeName()
    );

    // Perform an update
    mainView.updateBalance(new BigDecimal("12.3456"), rate);

  }

}
