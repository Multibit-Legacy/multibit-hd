package org.multibit.hd.ui.events;

import org.multibit.hd.core.api.RAGStatus;

/**
 * <p>Event to provide the following to views:</p>
 * <ul>
 * <li>Essential information for a system status change</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class SystemStatusChangeEvent {

  private final RAGStatus severity;

  /**
   * @param severity The severity
   */
  public SystemStatusChangeEvent(RAGStatus severity) {

    this.severity = severity;

  }

  /**
   * @return The severity of the system status
   */
  public RAGStatus getSeverity() {
    return severity;
  }
}
