package org.multibit.hd.ui.events.view;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates a wizard model has changed</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WizardModelChangedEvent implements ViewEvent {

  private final String panelName;

  /**
   * @param panelName The panel name to isolate a particular panel when responding
   */
  public WizardModelChangedEvent(String panelName) {
    this.panelName = panelName;
  }

  /**
   * @return The panel name
   */
  public String getPanelName() {
    return panelName;
  }
}
