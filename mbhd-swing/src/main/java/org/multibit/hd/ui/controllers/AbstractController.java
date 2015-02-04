package org.multibit.hd.ui.controllers;

import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Abstract base class to provide the following to UI controllers:</p>
 * <ul>
 * <li>Provision of common methods</li>
 * </ul>
 *
 * @since 0.0.1
 */
public abstract class AbstractController {

  private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

  public AbstractController() {

    // All controllers are registered for Controller events
    ControllerEvents.subscribe(this);

    // All controllers are registered for Core events
    CoreEvents.subscribe(this);

    // All controllers are registered for hardware events
    // this allows easier targeting of specific event state
    // handling (e.g. alerts for attach/detach, wizards for PIN etc)
    HardwareWalletEvents.subscribe(this);

  }

  /**
   * Unsubscribe from events (this controller is no longer required)
   */
  public void unsubscribe() {

    ControllerEvents.unsubscribe(this);
    CoreEvents.unsubscribe(this);
    HardwareWalletEvents.unsubscribe(this);

  }

}
