package org.multibit.hd.ui.events.controller;

import org.multibit.hd.ui.views.detail_views.DetailScreen;

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

  private final DetailScreen detailScreen;

  public ShowDetailScreenEvent(DetailScreen detailScreen) {

    this.detailScreen = detailScreen;

  }

  /**
   * @return The screen to show
   */
  public DetailScreen getDetailScreen() {
    return detailScreen;
  }
}
