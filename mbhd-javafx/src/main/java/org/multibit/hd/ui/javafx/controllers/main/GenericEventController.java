package org.multibit.hd.ui.javafx.controllers.main;

import javafx.application.Platform;
import org.multibit.hd.ui.javafx.platform.listener.*;
import org.multibit.hd.ui.javafx.screens.Screen;
import org.multibit.hd.ui.javafx.screens.ScreenTransitionManager;
import org.multibit.hd.ui.javafx.screens.TransitionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *         
 */
public class GenericEventController implements
  GenericOpenURIEventListener,
  GenericPreferencesEventListener,
  GenericAboutEventListener,
  GenericQuitEventListener,
  TransitionAware
{

  private Logger log = LoggerFactory.getLogger(GenericEventController.class);

  private final GenericQuitResponse frameQuitResponse = new GenericQuitResponse() {
    @Override
    public void cancelQuit() {
      log.debug("Quit Canceled");
    }

    @Override
    public void performQuit() {
      log.debug("Performed Quit");
    }
  };

  private ScreenTransitionManager transitionManager;

  public void setScreenTransitionManager(ScreenTransitionManager screenTransitionManager) {
    transitionManager = screenTransitionManager;
  }

  @Override
  public void onPreferencesEvent(GenericPreferencesEvent event) {

    log.debug("Controller received 'Preferences' event");

  }

  @Override
  public void onAboutEvent(GenericAboutEvent event) {

    log.debug("Controller received 'About' event");

  }

  @Override
  public synchronized void onOpenURIEvent(GenericOpenURIEvent event) {

    log.debug("Controller received 'Open URI' event with URI='{}'", event.getURI().toASCIIString());

    transitionManager.transitionTo(Screen.MAIN_HOME);

  }

  @Override
  public void onQuitEvent(GenericQuitEvent event, GenericQuitResponse response) {

    log.debug("Controller received 'Quit' event");

    Platform.exit();

    System.exit(0);

  }
}
