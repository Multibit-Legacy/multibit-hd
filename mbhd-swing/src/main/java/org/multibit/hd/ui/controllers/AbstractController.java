package org.multibit.hd.ui.controllers;

import org.multibit.hd.core.services.CoreServices;

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

  }
}
