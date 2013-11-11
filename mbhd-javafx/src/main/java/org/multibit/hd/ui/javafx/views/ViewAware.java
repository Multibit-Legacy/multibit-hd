package org.multibit.hd.ui.javafx.views;

/**
 * <p>Interface to provide the following to UI:</p>
 * <ul>
 * <li>Common accessor method to the screen transition managers</li>
 * </ul>
 * <p>The parent handles transitions that cause the surrounding frame to change, such as a transition
 * from login to the home page. The child handles transitions that do not cause the surrounding frame
 * to change, such as a transition from the home page to the contacts page.</p>
 *
 * @since 0.0.1
 *         
 */
public interface ViewAware {

  void setView(View view);

  View getView();

}