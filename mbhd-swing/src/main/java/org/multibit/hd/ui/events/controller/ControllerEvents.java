package org.multibit.hd.ui.events.controller;

import com.google.common.util.concurrent.ListeningExecutorService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to broadcast application events associated with UI controllers</li>
 * </ul>
 * <p>An application event is a high level event with specific semantics. Normally a
 * low level event (such as a mouse click) will initiate it.</p>
 *
 * @since 0.0.1
 *
 */
public class ControllerEvents {

  private static final Logger log = LoggerFactory.getLogger(ControllerEvents.class);

  // Provide a ControllerEvent thread pool to ensure non-AWT events are isolated from the EDT
  private static ListeningExecutorService eventExecutor = SafeExecutors.newFixedThreadPool(10, "controller-events");

  /**
   * Utilities have a private constructor
   */
  private ControllerEvents() {
  }

  /**
   * <p>Broadcast a new "show detail screen" event</p>
   *
   * @param detailScreen The screen to show
   */
  public static void fireShowDetailScreenEvent(final Screen detailScreen) {

    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'show detail screen' event");
          CoreServices.uiEventBus.post(new ShowScreenEvent(detailScreen));
        }
      });

  }

  /**
   * <p>Broadcast a new "add alert" event</p>
   *
   * @param alertModel The alert model
   */
  public static void fireAddAlertEvent(final AlertModel alertModel) {

    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'add alert' event");
          CoreServices.uiEventBus.post(new AddAlertEvent(alertModel));
        }
      });

  }

  /**
   * <p>Broadcast a new "remove alert" event</p>
   */
  public static void fireRemoveAlertEvent() {

    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'remove alert' event");
          CoreServices.uiEventBus.post(new RemoveAlertEvent());

          // Keep track of this
          CoreServices.logHistory(Languages.safeText(MessageKey.HIDE_ALERT));
        }
      });

  }

}