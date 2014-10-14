package org.multibit.hd.ui.events.controller;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.models.AlertModel;

/**
 * <p>Event to provide the following to  Controller Event API:</p>
 * <ul>
 * <li>Adding an alert model to the controller</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class AddAlertEvent implements ControllerEvent {

  private final AlertModel alertModel;

  /**
   * @param alertModel The alert model
   */
  public AddAlertEvent(AlertModel alertModel) {

    Preconditions.checkNotNull(alertModel,"'alertModel' must be present");

    this.alertModel = alertModel;
  }

  /**
   * @return The alert model
   */
  public AlertModel getAlertModel() {
    return alertModel;
  }
}
