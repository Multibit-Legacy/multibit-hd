package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.View;

import javax.swing.*;

/**
 * <p>Abstract base class to provide the following to components:</p>
 * <ul>
 * <li>Standard implementations of lifecycle methods between a component view and its model</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractComponentView<M extends Model> implements View<M> {

  private Optional<M> model = Optional.absent();

  protected JPanel panel;

  /**
   * @param model The model backing this view
   */
  public AbstractComponentView(M model) {
    this.model = Optional.fromNullable(model);

    // Views are automatically registered through ModelAndView
  }

  @Override
  public Optional<M> getModel() {
    return model;
  }

  @Override
  public void setModel(M model) {
    this.model = Optional.fromNullable(model);
  }

  /**
   * <p>Called when the view components have changed and need to update the model</p>
   *
   * <p>Implementers must update the model (if present) with the contents of the view components</p>
   */
  @Override
  public abstract void updateModelFromView();

  /**
   * <p>Called when the model has changed and the view components need to update</p>
   * <p>Default implementation is to do nothing since most components are pulling data</p>
   */
  @Override
  public void updateViewFromModel() {

    // Do nothing

  }

  /**
   * <p>Called when a new instance of the component is required</p>
   *
   * <p>Implementers must create a new panel taking the current locale into account</p>
   *
   * @return A new panel enclosing this component
   */
  @Override
  public abstract JPanel newComponentPanel();

  /**
   * @return The current panel enclosing this component
   */
  @Override
  public JPanel currentComponentPanel() {
    return panel;
  }

  /**
   * <p>Called in the Swing event thread when this component is first shown</p>
   *
   * <p>Implementers should select a primary component to <code>requestFocusInWindow()</code></p>
   */
  public abstract void requestInitialFocus();

}
