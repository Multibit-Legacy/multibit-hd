package org.multibit.hd.ui.events.view;

import org.multibit.hd.ui.views.ViewKey;

/**
 * <p>Event to provide the following to the Controller Event API:</p>
 * <ul>
 * <li>Information about how to show a View (header/footer/sidebar/detail)</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ViewChangedEvent implements ViewEvent {

  private final ViewKey viewKey;
  private final boolean visible;

  /**
   * @param viewKey The view name
   * @param visible True if the view should be "visible" (could be reduced height etc)
   */
  public ViewChangedEvent(ViewKey viewKey, boolean visible) {

    this.viewKey = viewKey;
    this.visible = visible;

  }

  /**
   * @return The screen to show
   */
  public ViewKey getViewKey() {
    return viewKey;
  }

  /**
   * @return True if the view is in its "visible" state (could be reduced height etc)
   */
  public boolean isVisible() {
    return visible;
  }

  @Override
  public String toString() {
    return "ViewChangedEvent{" +
            "viewKey=" + viewKey +
            ", visible=" + visible +
            '}';
  }
}
