package org.multibit.hd.ui.events.controller;

import org.multibit.hd.ui.views.screens.Screen;

/**
 * <p>Event to provide the following to the Controller Event API:</p>
 * <ul>
 * <li>Essential information to show a detail screen</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ShowDetailScreenEvent {

  private final Screen detailScreen;

  public ShowDetailScreenEvent(Screen detailScreen) {

    this.detailScreen = detailScreen;

  }

  /**
   * @return The screen to show
   */
  public Screen getDetailScreen() {
    return detailScreen;
  }
}
