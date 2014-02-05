package org.multibit.hd.ui.views.detail_views;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.services.CoreServices;

/**
 * <p>Abstract base class wizard models:</p>
 * <ul>
 * <li>Access to standard implementations of required methods</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractDetailModel implements DetailModel {

  /**
   * The detail view
   */
  protected final DetailView detailView;

  protected AbstractDetailModel(DetailView detailView) {

    Preconditions.checkNotNull(detailView, "'detailView' must be present");

    this.detailView = detailView;

    // Register for events
    CoreServices.uiEventBus.register(this);

  }

  @Override
  public DetailView getDetailView() {

    return detailView;

  }

}
