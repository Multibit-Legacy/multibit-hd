package org.multibit.hd.core.events;

import org.multibit.hd.core.dto.EnvironmentSummary;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of an issue with the environment (file permissions, debugger attached, clock drift etc)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnvironmentEvent implements CoreEvent {

  private final EnvironmentSummary summary;

  public EnvironmentEvent(EnvironmentSummary summary) {
    this.summary = summary;
  }

  /**
   * @return The summary
   */
  public EnvironmentSummary getSummary() {
    return summary;
  }

  /**
   * <p>Convenience method to compare the alert type</p>
   *
   * @param alertType The alert type to test against
   *
   * @return True if this alert matches
   */
  public boolean is(EnvironmentSummary.AlertType alertType) {

    return summary.getAlertType().equals(alertType);
  }
}
