package org.multibit.hd.ui.events.view;

/**
 * <p>Signature interface to provide the following to the View Event API:</p>
 * <ul>
 * <li>Identification of view events</li>
 * </ul>
 * <p>A view event should be named using a noun as the first part of the name (e.g. LocaleChangedEvent).</p>
 * <p>A view event can occur at any time and will not be synchronized with any other
 * events. It is up to the view to simply update its display in accordance with the
 * semantics of the event.</p>
 *
 * @since 0.0.1
 *         
 */
public interface ViewEvent {
}
