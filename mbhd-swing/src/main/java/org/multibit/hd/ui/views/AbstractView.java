package org.multibit.hd.ui.views;

import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ViewEvents;

/**
 * <p>Abstract base class to provide the following to UI controllers:</p>
 * <ul>
 * <li>Provision of common methods</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public abstract class AbstractView {

  public AbstractView() {

    // All views are registered for ViewEvents
    ViewEvents.subscribe(this);

    // All views are registered for Controller events
    ControllerEvents.subscribe(this);

    // All views are registered for hardware events
    HardwareWalletEvents.subscribe(this);

  }

  public void unregister() {

    ViewEvents.unsubscribe(this);
    ControllerEvents.unsubscribe(this);
    HardwareWalletEvents.unsubscribe(this);

  }

}
