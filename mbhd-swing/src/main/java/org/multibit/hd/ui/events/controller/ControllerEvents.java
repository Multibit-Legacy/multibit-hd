package org.multibit.hd.ui.events.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to broadcast application events associated with UI controllers</li>
 * </ul>
 * <p>These events are guaranteed to be off the Swing EDT</p>
 *
 * @since 0.0.1
 */
public class ControllerEvents {

  private static final Logger log = LoggerFactory.getLogger(ControllerEvents.class);

  // Provide a ControllerEvent thread pool to ensure non-AWT events are isolated from the EDT
  private static ListeningExecutorService eventExecutor = SafeExecutors.newFixedThreadPool(10, "controller-events");

  /**
   * Use Guava to handle subscribers to events
   * Do not use this method directly, instead
   */
  private static final EventBus controllerEventBus = new EventBus(ExceptionHandler.newSubscriberExceptionHandler());

  /**
   * Keep track of the Guava UI event bus subscribers for a clean shutdown
   */
  private static final Set<Object> controllerEventBusSubscribers = Sets.newHashSet();

  /**
   * Utilities have a private constructor
   */
  private ControllerEvents() {
  }

  /**
   * <p>Subscribe to events. Repeating a subscribe will not affect the event bus.</p>
   * <p>This approach ensures all subscribers will be correctly removed during a shutdown or wizard hide event</p>
   *
   * @param subscriber The subscriber (use the Guava <code>@Subscribe</code> annotation to subscribe a method)
   */
  public static void subscribe(Object subscriber) {

    Preconditions.checkNotNull(subscriber, "'subscriber' must be present");

    if (controllerEventBusSubscribers.add(subscriber)) {
      log.debug("Register: " + subscriber.getClass().getSimpleName());
      try {
        controllerEventBus.register(subscriber);
      } catch (IllegalArgumentException e) {
        log.warn("Unexpected failure to register");
      }
    } else {
      log.warn("Subscriber already registered: " + subscriber.getClass().getSimpleName());
    }

  }

  /**
   * <p>Unsubscribe a known subscriber from events. Providing an unknown object will not affect the event bus.</p>
   * <p>This approach ensures all subscribers will be correctly removed during a shutdown or wizard hide event</p>
   *
   * @param subscriber The subscriber (use the Guava <code>@Subscribe</code> annotation to subscribe a method)
   */
  public static void unsubscribe(Object subscriber) {

    Preconditions.checkNotNull(subscriber, "'subscriber' must be present");

    if (controllerEventBusSubscribers.contains(subscriber)) {
      log.debug("Unregister: " + subscriber.getClass().getSimpleName());
      try {
        controllerEventBus.unregister(subscriber);
      } catch (IllegalArgumentException e) {
        log.warn("Unexpected failure to unregister");
      }
    } else {
      log.warn("Subscriber already unregistered: " + subscriber.getClass().getSimpleName());
    }

  }

  /**
   * <p>Unsubscribe all subscribers from events</p>
   * <p>This approach ensures all subscribers will be correctly removed during a shutdown or wizard hide event</p>
   */
  public static void unsubscribeAll() {

    for (Object subscriber : controllerEventBusSubscribers) {
      unsubscribe(subscriber);
    }
    log.info("All subscribers removed");

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
          controllerEventBus.post(new AddAlertEvent(alertModel));
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
          controllerEventBus.post(new RemoveAlertEvent());

          // Keep track of this
          CoreServices.logHistory(Languages.safeText(MessageKey.HIDE_ALERT));
        }
      });

  }

}