package org.multibit.hd.ui.events.view;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates the password status has changed</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class PasswordStatusChangedEvent implements ViewEvent {

  private final boolean status;

  /**
   * @param status True if the password is OK
   */
  public PasswordStatusChangedEvent(boolean status) {

    this.status = status;

  }

  /**
   * @return True if the password is OK
   */
  public boolean isOK() {
    return status;
  }
}
