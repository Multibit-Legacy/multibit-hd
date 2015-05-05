package org.multibit.hd.common.error_reporting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.net.URI;

/**
 * <p>Value object to provide the following to error reporting code:</p>
 * <ul>
 * <li>Outcome of error report upload</li>
 * </ul>
 *
 * @since 0.1.0
 *  
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "error_report_status",
  "id",
  "uri"
})
public class ErrorReportResult {

  private ErrorReportStatus errorReportStatus;

  private String id;

  private URI uri;

  public ErrorReportResult() {
  }

  public ErrorReportResult(ErrorReportStatus errorReportStatus) {
    this.errorReportStatus = errorReportStatus;
  }

  /**
   * @return The error report status
   */
  public ErrorReportStatus getErrorReportStatus() {
    return errorReportStatus;
  }

  public void setErrorReportStatus(ErrorReportStatus errorReportStatus) {
    this.errorReportStatus = errorReportStatus;
  }

  /**
   * @return An ID for the user if the error report was uploaded OK
   */
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return A URI pointing to additional reference material for this error
   */
  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }
}
