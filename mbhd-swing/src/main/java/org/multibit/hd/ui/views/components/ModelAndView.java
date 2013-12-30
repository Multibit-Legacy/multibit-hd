package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.View;

/**
 * <p>Value object to provide the following to UI:</p>
 * <ul>
 * <li>Provision of a model and view for a component</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ModelAndView<M extends Model, V extends View> {

  private final M model;
  private final V view;

  public ModelAndView(M model, V view) {
    this.model = model;
    this.view = view;
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
