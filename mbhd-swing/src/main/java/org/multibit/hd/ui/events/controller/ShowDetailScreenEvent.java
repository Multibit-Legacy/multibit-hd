package org.multibit.hd.ui.events.controller;

import org.multibit.hd.ui.views.detail_views.DetailView;

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

  private final DetailView detailScreen;

  public ShowDetailScreenEvent(DetailView detailScreen) {

    this.detailScreen = detailScreen;

  }

  /**
   * @return The screen to show
   */
  public DetailView getDetailScreen() {
    return detailScreen;
  }
}
