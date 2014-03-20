package org.multibit.hd.core.events;

import org.multibit.hd.core.dto.CoreMessageKey;

/**
 *  <p>Event to provide the following to UIEventbus subscribers
 *  <ul>
 *  <li>Success/ failure of export transactions and payment requests</li>
 *  </ul>
 */
public class ExportPerformedEvent implements CoreEvent {

  private final String transactionsExportFilename;

  private final String paymentRequestsExportFilename;

  private final boolean exportWasSuccessful;

  private final CoreMessageKey exportFailureReasonKey;

  public ExportPerformedEvent(String transactionsExportFilename, String paymentRequestsExportFilename, boolean exportWasSuccessful, CoreMessageKey sendFailureReasonKey, String[] sendFailureReasonData) {
    this.transactionsExportFilename = transactionsExportFilename;
    this.paymentRequestsExportFilename = paymentRequestsExportFilename;
    this.exportWasSuccessful = exportWasSuccessful;
    this.exportFailureReasonKey = sendFailureReasonKey;
    this.sendFailureReasonData = sendFailureReasonData;
  }

  private final String[] sendFailureReasonData;


  public boolean isExportWasSuccessful() {
    return exportWasSuccessful;
  }

  public CoreMessageKey getExportFailureReasonKey() {
    return exportFailureReasonKey;
  }

  public String[] getSendFailureReasonData() {
    return sendFailureReasonData;
  }

  public String getTransactionsExportFilename() {
    return transactionsExportFilename;
  }

  public String getPaymentRequestsExportFilename() {
    return paymentRequestsExportFilename;
  }
}
