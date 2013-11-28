package org.multibit.hd.ui.controllers;

import org.multibit.hd.core.services.CoreServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Controller for the footer view</p>
 * <ul>
 * <li>Handles interaction between the model and the view</li>
 * </ul>
 */
public class FooterController {

  private static final Logger log = LoggerFactory.getLogger(FooterController.class);

  public FooterController() {

    CoreServices.uiEventBus.register(this);

  }

}
