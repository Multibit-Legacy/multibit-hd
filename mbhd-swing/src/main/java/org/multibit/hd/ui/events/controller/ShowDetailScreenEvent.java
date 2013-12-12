package org.multibit.hd.ui.events.controller;

import org.multibit.hd.ui.views.Screen;

/**
 * <p>Event to provide the following to views:</p>
 * <ul>
 * <li>Essential information to show a detail screen</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ShowDetailScreenEvent {

  private final Screen screen;

  public ShowDetailScreenEvent(Screen screen) {

    this.screen = screen;

  }

  /**
   * @return The screen to show
   */
  public Screen getScreen() {
    return screen;
  }
}
