package org.multibit.hd.ui.views;

import org.multibit.hd.ui.events.view.ViewEvents;

/**
 * <p>Abstract base class to provide the following to UI controllers:</p>
 * <ul>
 * <li>Provision of common methods</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public abstract class AbstractView {

  public AbstractView() {

    // All views are registered for ViewEvents
    ViewEvents.subscribe(this);

    // Note Core and Controller events are not appropriate here

  }

  public void unregister() {

    ViewEvents.unsubscribe(this);

  }

}
