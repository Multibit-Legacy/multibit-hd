package org.multibit.hd.ui.models;

/**
 * <p>Interface to provide the following to UI:</p>
 * <ul>
 * <li>Identification of generic Model</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public interface Model<M> {

  /**
   * @return The value of the model (usually user data)
   */
  M getValue();

  /**
   * @param value The value of the model
   */
  void setValue(M value);
}
