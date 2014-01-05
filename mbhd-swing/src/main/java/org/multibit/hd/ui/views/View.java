package org.multibit.hd.ui.views;

import org.multibit.hd.ui.models.Model;

import javax.swing.*;

/**
 * <p>Interface to provide the following to UI:</p>
 * <ul>
 * <li>Identification of generic View</li>
 * <li>Type safe setting of the associated Model</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public interface View<M extends Model> {

  /**
   * @param model The model to set
   */
  void setModel(M model);

  /**
   * Update the model with the current view contents
   */
  void updateModel();

  /**
   * @return A new panel containing the visual components based on the current locale
   */
  JPanel newPanel();
}
