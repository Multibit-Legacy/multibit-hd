package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.View;

/**
 * <p>Value object to provide the following to UI:</p>
 * <ul>
 * <li>Provision of a model and view for a component</li>
 * </ul>
 *
 * <p>All ModelAndView components are registered for UI events by default (both model and view)</p>
 *
 * @since 0.0.1
 */
public class ModelAndView<M extends Model, V extends View> {

  private final M model;
  private final V view;

  public ModelAndView(M model, V view) {

    this.model = model;
    this.view = view;

    // Convenience method to ensure UI events work out of the box
    ViewEvents.subscribe(model);
    ViewEvents.subscribe(view);

    CoreEvents.subscribe(model);
    CoreEvents.subscribe(view);

  }

  /**
   * <p>This ModelAndView should unsubscribe from events as it is about to close</p>
   */
  public void unsubscribe() {

    ViewEvents.unsubscribe(model);
    ViewEvents.unsubscribe(view);

    CoreEvents.unsubscribe(model);
    CoreEvents.unsubscribe(view);

  }

  /**
   * @return The model (any changes will cause an immediate update event to the view)
   */
  public M getModel() {
    return model;
  }

  /**
   * @return The view (will reflect the model state)
   */
  public V getView() {
    return view;
  }
}
