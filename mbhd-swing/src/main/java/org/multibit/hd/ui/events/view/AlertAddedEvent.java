package org.multibit.hd.ui.events.view;

import org.multibit.hd.ui.models.AlertModel;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates an alert has been added to the UI</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class AlertAddedEvent implements ViewEvent {

  private final AlertModel alertModel;

  /**
   * @param alertModel The alert model
   */
  public AlertAddedEvent(AlertModel alertModel) {

    this.alertModel = alertModel;

  }

  /**
   * @return The alert model providing the information for the view
   */
  public AlertModel getAlertModel() {
    return alertModel;
  }
}
