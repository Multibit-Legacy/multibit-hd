package org.multibit.hd.core.events;

import org.multibit.hd.core.dto.CoreMessageKey;

/**
 * <p>Event to provide the following to UIEventbus subscribers:</p>
 * <ul>
 * <li>Success/ failure of export transactions and payment requests</li>
 * </ul>
 * <p>This is an infrequent event</p>
 *
 * @since 0.0.1
 */
public class ExportPerformedEvent implements CoreEvent {

  private final String transactionsExportFilename;

  private final String paymentRequestsExportFilename;

  private final boolean exportWasSuccessful;

  private final CoreMessageKey exportFailureReasonKey;

  public ExportPerformedEvent(
    String transactionsExportFilename,
    String paymentRequestsExportFilename,
    boolean exportWasSuccessful,
    CoreMessageKey exportFailureReasonKey,
    String[] exportFailureReasonData) {

    this.transactionsExportFilename = transactionsExportFilename;
    this.paymentRequestsExportFilename = paymentRequestsExportFilename;
    this.exportWasSuccessful = exportWasSuccessful;
    this.exportFailureReasonKey = exportFailureReasonKey;
    this.exportFailureReasonData = exportFailureReasonData;

  }

  private final String[] exportFailureReasonData;

  public boolean isExportWasSuccessful() {
    return exportWasSuccessful;
  }

  public CoreMessageKey getExportFailureReasonKey() {
    return exportFailureReasonKey;
  }

  public String[] getExportFailureReasonData() {
    return exportFailureReasonData;
  }

  public String getTransactionsExportFilename() {
    return transactionsExportFilename;
  }

  public String getPaymentRequestsExportFilename() {
    return paymentRequestsExportFilename;
  }

}
