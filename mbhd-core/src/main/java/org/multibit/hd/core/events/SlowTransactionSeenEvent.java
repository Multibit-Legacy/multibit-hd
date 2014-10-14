package org.multibit.hd.core.events;

/**
 * <p>Event to provide the following to Core event subscribers:</p>
 * <ul>
 * <li>One or more payments have been seen in the last second</li>
 * </ul>
 * <p>This is a consolidated version of TransactionSeenEvent that you can use for slow updates.</p>
 *
 * @since 0.0.1
 */
public class SlowTransactionSeenEvent implements CoreEvent {

  public SlowTransactionSeenEvent() {
  }

}
