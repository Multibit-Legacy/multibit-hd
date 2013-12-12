package org.multibit.hd.ui.events.controller;

import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.Screen;

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

  /**
   * Utilities have a private constructor
   */
  private ControllerEvents() {
  }

  /**
   * <p>Broadcast a new show detail screen event</p>
   *
   * @param screen The screen to show
   */
  public static void fireShowDetailScreenEvent(Screen screen) {
    CoreServices.uiEventBus.post(new ShowDetailScreenEvent(screen));
  }

  /**
   * <p>Broadcast a new add alert event</p>
   *
   * @param alertModel The alert model
   */
  public static void fireAddAlertEvent(AlertModel alertModel) {
    CoreServices.uiEventBus.post(new AddAlertEvent(alertModel));
  }

  /**
   * <p>Broadcast a new remove alert event</p>
   */
  public static void fireRemoveAlertEvent() {
    CoreServices.uiEventBus.post(new RemoveAlertEvent());
  }

}