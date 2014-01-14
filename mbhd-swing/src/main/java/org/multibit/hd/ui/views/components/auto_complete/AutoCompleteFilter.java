package org.multibit.hd.ui.views.components.auto_complete;

/**
 * <p>Interface to provide the following to combo boxes:</p>
 * <ul>
 * <li>Callback to a filter mechanism to populate the popup</li>
 * </ul>
 *
 * @since 0.0.1
 */
public interface AutoCompleteFilter<T> {

  /**
   * @return An array containing the initial items to be shown in the popup
   */
  T[] create();

  /**
   * @param fragment The entered text fragment
   *                  
   * @return An array containing the items to be shown in the popup
   */
  T[] update(String fragment);

}
