package org.multibit.hd.ui.events.view;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.ui.views.detail_views.DetailView;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates a detail view component model has changed</li>
 * </ul>
 * <p>A detail model will typically update in response to this event</p>
 *
 * @since 0.0.1
 *  
 */
public class DetailComponentModelChangedEvent implements ViewEvent {

  private final DetailView detailView;
  private final Optional componentModel;

  public DetailComponentModelChangedEvent(DetailView detailView, Optional componentModel) {

    Preconditions.checkNotNull(detailView, "'detailView' must be present");
    Preconditions.checkNotNull(componentModel, "'componentModel' must be present");

    this.detailView = detailView;
    this.componentModel = componentModel;
  }

  /**
   * @return The detail view (to target updates to specific views)
   */
  public DetailView getDetailView() {
    return detailView;
  }

  /**
   * @return The component model that triggered the update
   */
  public Optional getComponentModel() {
    return componentModel;
  }
}


