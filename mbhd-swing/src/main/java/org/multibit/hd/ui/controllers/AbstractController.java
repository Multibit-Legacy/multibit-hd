package org.multibit.hd.ui.controllers;

import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;

/**
 * <p>Abstract base class to provide the following to UI controllers:</p>
 * <ul>
 * <li>Provision of common methods</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public abstract class AbstractController {

  public AbstractController() {

    // All controllers are registered for UI events
    CoreServices.uiEventBus.register(this);

    // All controllers are registered for hardware events
    // this allows easier targeting of specific event state
    // handling (e.g. alerts for attach/detach, wizards for PIN etc)
    HardwareWalletService.hardwareWalletEventBus.register(this);

  }

  public void unregister() {

    CoreServices.uiEventBus.unregister(this);
    HardwareWalletService.hardwareWalletEventBus.unregister(this);

  }

}
