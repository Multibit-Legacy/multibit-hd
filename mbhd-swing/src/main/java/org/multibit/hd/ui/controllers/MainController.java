package org.multibit.hd.ui.controllers;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.SignOutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Controller for the main view </p>
 * <ul>
 * <li>Handles interaction between the model and the view</li>
 * </ul>
 * <p>To allow complete separation between Model, View and Controller all interactions are handled using application events</p>
 */
public class MainController {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  public MainController() {

    CoreServices.uiEventBus.register(this);

  }

  @Subscribe
  public void onSignOutEvent(SignOutEvent event) {

    // TODO Graceful shutdown required
    System.exit(0);

  }
}
