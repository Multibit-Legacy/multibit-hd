package org.multibit.hd.core.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.multibit.hd.core.dto.CoreMessageKey;

import java.util.Arrays;

/**
 * <p>Event to provide the following to UIEventbus subscribers:</p>
 * <ul>
 * <li>Success/ failure of sending payment details to a BIP70 payment requestor</li>
 * </ul>
 * <p>This is an infrequent event</p>
 *
 * @since 0.0.1
 */
public class PaymentSentToRequestorEvent implements CoreEvent {

  private final boolean sendWasSuccessful;

  private final CoreMessageKey sendFailureReason;

  private final String[] sendFailureReasonData;

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP2"})
  public PaymentSentToRequestorEvent(
          boolean sendWasSuccessful,
          CoreMessageKey sendFailureReason,
          String[] sendFailureReasonData
  ) {

    this.sendWasSuccessful = sendWasSuccessful;
    this.sendFailureReason = sendFailureReason;
    this.sendFailureReasonData = sendFailureReasonData;
  }

  /**
   * @return True if the send was successful
   */
  public boolean isSendWasSuccessful() {
    return sendWasSuccessful;
  }

  /**
   * @return The reason for the failure
   */
  public CoreMessageKey getSendFailureReason() {
    return sendFailureReason;
  }

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP"})
  public String[] getSendFailureReasonData() {
    return sendFailureReasonData;
  }

  @Override
  public String toString() {
    return "PaymentSentToRequestorEvent{" +

            ", sendWasSuccessful=" + sendWasSuccessful +
            ", sendFailureReason=" + sendFailureReason +
            ", sendFailureReasonData=" + Arrays.toString(sendFailureReasonData) +
            '}';
  }
}
