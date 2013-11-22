package org.multibit.hd.ui.swing.controllers;

import org.multibit.hd.ui.swing.views.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Controller for the Mmin view </p>
 * <ul>
 *   <li>Handles interaction between the model and the view</li>
 * </ul>
 */
public class MainController {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  private final MainView mainView;

  public MainController(MainView mainView) {
    this.mainView = mainView;
  }


}
