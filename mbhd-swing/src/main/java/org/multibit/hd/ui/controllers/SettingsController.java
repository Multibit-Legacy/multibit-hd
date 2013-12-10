package org.multibit.hd.ui.controllers;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.ui.events.controller.ShowDetailScreenEvent;
import org.multibit.hd.ui.views.Screen;
import org.multibit.hd.ui.views.SettingsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Controller for the Mmin view </p>
 * <ul>
 * <li>Handles interaction between the model and the view</li>
 * </ul>
 */
public class SettingsController {

  private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

  private final SettingsView view;

  private final Screen screen = Screen.MAIN_SETTINGS;

  public SettingsController(SettingsView view) {

    this.view = view;

  }

  public void onApplyClicked() {

    Configuration newConfiguration = view.takeSnapshot();

  }

  /**
   * <p>Called when a detail screen is requested</p>
   *
   * @param event The exchange rate change event
   */
  @Subscribe
  public void onDetailScreenChangeEvent(ShowDetailScreenEvent event) {


    // Post the event
    //ViewEvents.fireBalanceChangeEvent(btcBalance, localBalance, event.getExchangeName());

  }

}
