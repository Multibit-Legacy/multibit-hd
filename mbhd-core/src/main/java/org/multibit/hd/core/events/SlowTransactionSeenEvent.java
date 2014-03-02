package org.multibit.hd.core.events;

/**
 * <p>Event to provide the following to UIEventbus subscribers:</p>
 * <ul>
 * <li>One or more payments have been seen in the last second.
 * </ul>
 * <p>This is a consolidated version of TransactionSeenEvent that you can use for slow updates.</p>
 */
public class SlowTransactionSeenEvent implements CoreEvent {

  public SlowTransactionSeenEvent() {
  }

}
