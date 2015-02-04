package org.multibit.hd.ui.events.view;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates the credentials status has changed</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class VerificationStatusChangedEvent implements ViewEvent {

  private final String panelName;
  private final boolean status;


  /**
   * @param panelName The panel name to uniquely identify a particular instance of a credentials status
   * @param status    True if the verification is OK
   */
  public VerificationStatusChangedEvent(String panelName, boolean status) {

    this.panelName = panelName;
    this.status = status;

  }

  /**
   * @return True if the verification status is OK
   */
  public boolean isOK() {
    return status;
  }

  /**
   * @return The panel name
   */
  public String getPanelName() {
    return panelName;
  }
}
