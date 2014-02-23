package org.multibit.hd.core.events;

/**
 *  <p>Event to provide the following to UIEventbus subscribers
 *  <ul>
 *  <li>One or more payments have been seen in the last second.
 *   This is a consolidated version of TransactionSeenEvent that you can use for slow updates.</li>
 *  </ul>
 *
 */
public class SlowTransactionSeenEvent {
  public SlowTransactionSeenEvent() {
  }
}
