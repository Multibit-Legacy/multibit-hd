package org.multibit.hd.core.events;

import org.multibit.hd.core.dto.SecuritySummary;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of a security issue with the environment (file permissions, debugger attached etc)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SecurityEvent implements CoreEvent {

  private final SecuritySummary summary;

  public SecurityEvent(SecuritySummary summary) {
    this.summary = summary;
  }

  /**
   * @return The summary
   */
  public SecuritySummary getSummary() {
    return summary;
  }

  /**
   * <p>Convenience method to compare the security alert type</p>
   *
   * @param securityAlertType The security alert type to test against
   *
   * @return True if this security alert matches
   */
  public boolean is(SecuritySummary.AlertType securityAlertType) {

    return summary.getAlertType().equals(securityAlertType);
  }
}
