package org.multibit.hd.ui.events.view;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates a wizard component model has changed</li>
 * </ul>
 * <p>A panel model will typically update in response to this event</p>
 *
 * @since 0.0.1
 *  
 */
public class ComponentChangedEvent implements ViewEvent {

  private final String panelName;
  private final Optional componentModel;

  public ComponentChangedEvent(String panelName, Optional componentModel) {

    Preconditions.checkNotNull(panelName, "'panelName' must be present");
    Preconditions.checkNotNull(componentModel, "'componentModel' must be present");

    this.panelName = panelName;
    this.componentModel = componentModel;
  }

  /**
   * @return The panel name (to target updates to specific panels)
   */
  public String getPanelName() {
    return panelName;
  }

  /**
   * @return The component model that triggered the update
   */
  public Optional getComponentModel() {
    return componentModel;
  }
}


