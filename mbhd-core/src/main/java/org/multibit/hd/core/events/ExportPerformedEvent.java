package org.multibit.hd.core.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

  private final String mbhdPaymentRequestsExportFilename;

  private final String paymentRequestsExportFilename;

  private final boolean exportWasSuccessful;

  private final CoreMessageKey exportFailureReasonKey;

  /**
   * TODO Consider using List<String> instead
   */
  private final String[] exportFailureReasonData;

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP2"})
  public ExportPerformedEvent(
    String transactionsExportFilename,
    String mbhdPaymentRequestsExportFilename,
    String paymentRequestsExportFilename,
    boolean exportWasSuccessful,
    CoreMessageKey exportFailureReasonKey,
    String[] exportFailureReasonData) {

    this.transactionsExportFilename = transactionsExportFilename;
    this.mbhdPaymentRequestsExportFilename = mbhdPaymentRequestsExportFilename;
    this.paymentRequestsExportFilename = paymentRequestsExportFilename;
    this.exportWasSuccessful = exportWasSuccessful;
    this.exportFailureReasonKey = exportFailureReasonKey;
    this.exportFailureReasonData = exportFailureReasonData;

  }

  public boolean isExportWasSuccessful() {
    return exportWasSuccessful;
  }

  public CoreMessageKey getExportFailureReasonKey() {
    return exportFailureReasonKey;
  }

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP"})
  public String[] getExportFailureReasonData() {
    return exportFailureReasonData;
  }

  public String getTransactionsExportFilename() {
    return transactionsExportFilename;
  }

  public String getMBHDPaymentRequestsExportFilename() {
    return mbhdPaymentRequestsExportFilename;
  }

  public String getPaymentRequestsExportFilename() {
    return paymentRequestsExportFilename;
  }

}
