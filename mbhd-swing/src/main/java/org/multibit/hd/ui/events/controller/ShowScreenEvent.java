package org.multibit.hd.ui.events.controller;

import org.multibit.hd.ui.events.view.ViewEvent;
import org.multibit.hd.ui.views.screens.Screen;

/**
 * <p>Event to provide the following to the Controller Event API:</p>
 * <ul>
 * <li>Essential information to show a screen</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ShowScreenEvent implements ViewEvent {

  private final Screen screen;

  public ShowScreenEvent(Screen screen) {

    this.screen = screen;

  }

  /**
   * @return The screen to show
   */
  public Screen getScreen() {
    return screen;
  }
}
