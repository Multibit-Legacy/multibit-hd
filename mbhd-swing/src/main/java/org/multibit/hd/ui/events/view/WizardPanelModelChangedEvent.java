package org.multibit.hd.ui.events.view;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates a wizard panel model has changed</li>
 * </ul>
 * <p>A wizard model will typically update in response to this event</p>
 *
 * @since 0.0.1
 *  
 */
public class WizardPanelModelChangedEvent implements ViewEvent {

  private final String panelName;
  private final Optional panelModel;

  public WizardPanelModelChangedEvent(String panelName, Optional panelModel) {

    Preconditions.checkNotNull(panelName, "'panelName' must be present");
    Preconditions.checkNotNull(panelModel, "'panelModel' must be present");

    this.panelName = panelName;
    this.panelModel = panelModel;
  }

  /**
   * @return The panel name (to target updates to specific panels)
   */
  public String getPanelName() {
    return panelName;
  }

  /**
   * @return The panel model
   */
  public Optional getPanelModel() {
    return panelModel;
  }
}


