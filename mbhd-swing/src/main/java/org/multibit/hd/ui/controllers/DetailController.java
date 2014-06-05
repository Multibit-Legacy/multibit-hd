package org.multibit.hd.ui.controllers;

import org.multibit.hd.core.services.CoreServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Controller for the detail view that manages various screens</p>
 * <ul>
 * <li>Handles interaction between the model and the view</li>
 * </ul>
 */
public class DetailController {

  private static final Logger log = LoggerFactory.getLogger(DetailController.class);

  public DetailController() {

    CoreServices.uiEventBus.register(this);

  }

}
