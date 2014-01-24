package org.multibit.hd.ui.views;

import com.google.common.base.Optional;
import org.multibit.hd.ui.models.Model;

import javax.swing.*;

/**
 * <p>Interface to provide the following to UI:</p>
 * <ul>
 * <li>Identification of generic View</li>
 * <li>Type safe setting of the associated Model</li>
 * <li>Lifecycle methods for create/update/locale change/model data transfer</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public interface View<M extends Model> {

  /**
   * @return The model backing the view
   */
  Optional<M> getModel();

  /**
   * @param model The model backing the view
   */
  void setModel(M model);

  /**
   * Update the model with the current view contents
   */
  void updateModelFromView();

  /**
   * @return A new panel containing the visual components based on the current locale (normally used at creation/locale change)
   */
  JPanel newComponentPanel();

  /**
   * @return The current panel containing the visual components based on the current locale (normally used for model updates)
   */
  JPanel currentComponentPanel();

}
