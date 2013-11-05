package org.multibit.hd.ui.javafx.screens;

/**
 * <p>Interface to provide the following to UI:</p>
 * <ul>
 * <li>Common accessor method to the screen transition manager</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public interface TransitionAware {

  /**
   * @param screenTransitionManager The screen transition manager
   */
  public void setScreenTransitionManager(ScreenTransitionManager screenTransitionManager);
}