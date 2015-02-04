package org.multibit.hd.ui.views.screens;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.ui.events.view.ViewEvents;

/**
 * <p>Abstract base class wizard models:</p>
 * <ul>
 * <li>Access to standard implementations of required methods</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public abstract class AbstractScreenModel implements ScreenModel {

  /**
   * The detail view
   */
  protected final Screen detailView;

  protected AbstractScreenModel(Screen detailView) {

    Preconditions.checkNotNull(detailView, "'detailView' must be present");

    this.detailView = detailView;

    // Register for Core events
    CoreEvents.subscribe(this);
    ViewEvents.subscribe(this);

  }

  @Override
  public Screen getScreen() {

    return detailView;

  }

}
