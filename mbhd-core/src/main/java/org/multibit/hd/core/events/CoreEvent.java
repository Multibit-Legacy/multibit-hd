package org.multibit.hd.core.events;

/**
 * <p>Signature interface to provide the following to Core Event API:</p>
 * <ul>
 * <li>Identification of core events</li>
 * </ul>
 * <p>A core event should be named using a noun as the first part of the name (e.g. ExchangeRateChangedEvent)</p>
 * <p>A core event can occur at any time and will not be synchronized with other events.</p>
 *
 * @since 0.0.1
 *         
 */
public interface CoreEvent {
}
