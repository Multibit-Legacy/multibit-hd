package org.multibit.hd.ui.events.view;

import org.multibit.hd.hardware.core.events.HardwareWalletEventType;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates the hardware wallet status has changed</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class HardwareWalletStatusChangedEvent implements ViewEvent {

  private final HardwareWalletEventType eventType;

  /**
   * @param eventType The event type
   */
  public HardwareWalletStatusChangedEvent(HardwareWalletEventType eventType) {
    this.eventType = eventType;
  }

  /**
   * @return The hardware wallet event type
   */
  public HardwareWalletEventType getEventType() {
    return eventType;
  }
}
