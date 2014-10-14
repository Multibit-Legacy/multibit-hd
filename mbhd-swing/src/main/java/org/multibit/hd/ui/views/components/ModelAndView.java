package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Value object to provide the following to UI:</p>
 * <ul>
 * <li>Provision of a model and view for a component</li>
 * </ul>
 *
 * <p>All ModelAndView components are registered for UI events by default (both model and view)</p>
 *
 * @since 0.0.1
 *  
 */
public class ModelAndView<M extends Model, V extends View> {

  private static final Logger log = LoggerFactory.getLogger(ModelAndView.class);

  private final M model;
  private final V view;

  public ModelAndView(M model, V view) {

    this.model = model;
    this.view = view;

    // Convenience method to ensure UI events work out of the box
    CoreServices.uiEventBus.register(model);
    CoreServices.uiEventBus.register(view);

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

  /**
   * <p>Close this ModelAndView and deregister from UI events</p>
   */
  public void close() {

    try {
      CoreServices.uiEventBus.unregister(model);
    } catch (IllegalArgumentException e) {
      log.warn("Model was not registered: {}", model.getClass().getCanonicalName());
    }
    try {
      CoreServices.uiEventBus.unregister(view);
    } catch (IllegalArgumentException e) {
      log.warn("View was not registered: {}", view.getClass().getCanonicalName());
    }

  }
}
