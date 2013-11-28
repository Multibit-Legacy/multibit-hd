package org.multibit.hd.ui.controllers;

import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.ui.views.SettingsLanguageView;
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

  public SettingsController(SettingsView view) {

    this.view = view;

  }

  public void onApplyClicked() {

    Configuration newConfiguration = view.takeSnapshot();

  }
}
