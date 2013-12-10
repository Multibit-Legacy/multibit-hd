package org.multibit.hd.ui.events.view;

import org.multibit.hd.ui.models.AlertModel;

/**
 * <p>Event to provide the following to views:</p>
 * <ul>
 * <li>Essential information for an alert change</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class AlertChangedEvent {

  private final AlertModel alertModel;

  /**
   * @param alertModel The alert model
   */
  public AlertChangedEvent(AlertModel alertModel) {

    this.alertModel = alertModel;

  }

  /**
   * @return The alert model providing the information for the view
   */
  public AlertModel getAlertModel() {
    return alertModel;
  }
}
