package org.multibit.hd.ui.events.controller;

/**
 * <p>Signature interface to provide the following to Controller Event API:</p>
 * <ul>
 * <li>Identification of controller events</li>
 * </ul>
 * <p>A controller event should be named using a verb as the first part of the name (e.g. ChangeLocaleEvent)</p>
 * <p>A controller event can occur at any time and will not be synchronized with other
 * events, although it is likely that a single controller will cascade updates to many
 * views (such as a locale change).</p>
 * <p>It is up to the controller to issue view events to cascade any changes into
 * the user interface.</p>
 *
 * @since 0.0.1
 *         
 */
public interface ControllerEvent {
}
