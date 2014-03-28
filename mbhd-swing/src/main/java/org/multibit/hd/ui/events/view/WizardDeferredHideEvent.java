package org.multibit.hd.ui.events.view;

import com.google.common.base.Preconditions;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates a wizard hide has been deferred and should now take place</li>
 * </ul>
 * <p>A wizard will typically subscribe and update in response to this event</p>
 *
 * @since 0.0.1
 *  
 */
public class WizardDeferredHideEvent implements ViewEvent {

  private final String panelName;
  private final boolean isExitCancel;

  /**
   * @param panelName    The panel name (usually taken from the state enum)
   * @param isExitCancel True if the hide event comes from an exit or cancel
   */
  public WizardDeferredHideEvent(String panelName, boolean isExitCancel) {

    Preconditions.checkNotNull(panelName, "'panelName' must be present");

    this.panelName = panelName;
    this.isExitCancel = isExitCancel;
  }

  /**
   * @return The panel name (to identify the correct subscriber)
   */
  public String getPanelName() {
    return panelName;
  }

  /**
   * @return True if the hide event comes from an exit or cancel
   */
  public boolean isExitCancel() {
    return isExitCancel;
  }

}


