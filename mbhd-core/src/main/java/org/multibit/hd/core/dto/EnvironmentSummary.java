package org.multibit.hd.core.dto;

import com.google.common.base.Optional;

import java.net.URI;

/**
 * <p>Value object to provide the following to Core API:</p>
 * <ul>
 * <li>Information about the operating environment</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class EnvironmentSummary {

  public enum AlertType {

    /**
     * A debugger has been detected attached to this JVM
     */
    DEBUGGER_ATTACHED,
    /**
     * The system time has drifted from time standards
     */
    SYSTEM_TIME_DRIFT,
    /**
     * A new article is present on the MultiBit.org Atom feed
     */
    ATOM_FEED_CHECK,
    /**
     * A backup operation failed
     */
    BACKUP_FAILED,
    /**
     * A certificate failed for an external site (e.g. an exchange)
     */
    CERTIFICATE_FAILED,
    /**
     * Unsupported firmware has been attached - user must upgrade to continue
     */
    UNSUPPORTED_FIRMWARE_ATTACHED,
    /**
     * Deprecated firmware has been attached - user may continue but UI may be different
     */
    DEPRECATED_FIRMWARE_ATTACHED,
    /**
     * Unsupported configuration has been detected (e.g. passphrase in Trezor)
     */
    UNSUPPORTED_CONFIGURATION_ATTACHED,

    // End of enum
    ;

  }

  private final AlertType alertType;
  private final RAGStatus severity;

  private final Optional<CoreMessageKey> messageKey;
  private final Optional<Object[]> messageData;
  private final Optional<URI> uri;

  /**
   * <p>A debugger is attached - could be an attack in progress</p>
   *
   * @return A new "debugger attached" summary
   */
  public static EnvironmentSummary newDebuggerAttached() {
    return new EnvironmentSummary(
      RAGStatus.RED,
      Optional.of(CoreMessageKey.DEBUGGER_ATTACHED),
      Optional.<Object[]>absent(),
      Optional.<URI>absent(),
      AlertType.DEBUGGER_ATTACHED
    );
  }

  /**
   * <p>The system time has drifted - could cause transaction problems</p>
   *
   * @return A new "system time drift" summary
   */
  public static EnvironmentSummary newSystemTimeDrift() {
    return new EnvironmentSummary(
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.SYSTEM_TIME_DRIFT),
      Optional.<Object[]>absent(),
      Optional.<URI>absent(),
      AlertType.SYSTEM_TIME_DRIFT
    );
  }

  /**
   * <p>A new Atom feed article is present - could be useful info for the user</p>
   *
   * @param truncatedTitle The truncated title string
   * @param atomEntryUri The Atom entry URI
   *
   * @return A new "Atom feed" summary
   */
  public static EnvironmentSummary newAtomFeedCheck(String truncatedTitle, URI atomEntryUri) {
    return new EnvironmentSummary(
      RAGStatus.GREEN,
      Optional.of(CoreMessageKey.ATOM_FEED_CHECK),
      Optional.of(new Object[]{truncatedTitle}),
      Optional.of(atomEntryUri),
      AlertType.ATOM_FEED_CHECK
    );
  }

  /**
   * <p>A ZIP backup has failed to write correctly</p>
   *
   * @return A new "backup failed" summary
   */
  public static EnvironmentSummary newBackupFailed() {
    return new EnvironmentSummary(
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.BACKUP_FAILED),
      Optional.<Object[]>absent(),
      Optional.<URI>absent(),
      AlertType.BACKUP_FAILED
    );
  }

  /**
   * <p>A HTTPS connection has failed due to incorrect certificate</p>
   *
   * @return A new "certificate failure" summary
   */
  public static EnvironmentSummary newCertificateFailed() {
    return new EnvironmentSummary(
      RAGStatus.RED,
      Optional.of(CoreMessageKey.CERTIFICATE_FAILED),
      Optional.<Object[]>absent(),
      Optional.<URI>absent(),
      AlertType.CERTIFICATE_FAILED
    );
  }

  /**
   * <p>A hardware wallet with unsupported firmware is attached - could expose user to risk</p>
   *
   * @return A new "unsupported firmware" summary
   */
  public static EnvironmentSummary newUnsupportedFirmware() {
    return new EnvironmentSummary(
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.UNSUPPORTED_FIRMWARE_ATTACHED),
      Optional.<Object[]>absent(),
      Optional.<URI>absent(),
      AlertType.UNSUPPORTED_FIRMWARE_ATTACHED
    );
  }

  /**
   * <p>A hardware wallet with unsupported firmware is attached - could expose user to risk</p>
   *
   * @return A new "unsupported firmware" summary
   */
  public static EnvironmentSummary newDeprecatedFirmware() {
    return new EnvironmentSummary(
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.DEPRECATED_FIRMWARE_ATTACHED),
      Optional.<Object[]>absent(),
      Optional.<URI>absent(),
      AlertType.DEPRECATED_FIRMWARE_ATTACHED
    );
  }

  /**
   * <p>A hardware wallet with unsupported configuration is attached - unsupported operations are present</p>
   *
   * @return A new "unsupported configuration" summary
   */
  public static EnvironmentSummary newUnsupportedConfigurationPassphrase() {
    return new EnvironmentSummary(
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.UNSUPPORTED_CONFIGURATION_PASSPHRASE),
      Optional.<Object[]>absent(),
      Optional.<URI>absent(),
      AlertType.UNSUPPORTED_CONFIGURATION_ATTACHED
    );
  }

  /**
   * <p>A hardware wallet with unsupported configuration is attached - unsupported operations are present</p>
   *
   * @return A new "unsupported configuration" summary
   */
  public static EnvironmentSummary newUnsupportedConfigurationFirmware() {
    return new EnvironmentSummary(
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.UNSUPPORTED_CONFIGURATION_PASSPHRASE),
      Optional.<Object[]>absent(),
      Optional.<URI>absent(),
      AlertType.UNSUPPORTED_CONFIGURATION_ATTACHED
    );
  }

  /**
   * @param severity    The severity (Red, Amber, Green)
   * @param messageKey  The error key to allow localisation
   * @param messageData The error data for insertion into the error message
   * @param uri
   */
  public EnvironmentSummary(
    RAGStatus severity,
    Optional<CoreMessageKey> messageKey,
    Optional<Object[]> messageData,
    Optional<URI> uri,
    AlertType alertType
  ) {
    this.uri = uri;

    this.alertType = alertType;
    this.severity = severity;

    this.messageKey = messageKey;
    this.messageData = messageData;
  }

  /**
   * @return The alert type to assist creation of alert models
   */
  public AlertType getAlertType() {
    return alertType;
  }

  /**
   * @return The severity (e.g. AMBER)
   */
  public RAGStatus getSeverity() {
    return severity;
  }

  /**
   * @return The message key
   */
  public Optional<CoreMessageKey> getMessageKey() {
    return messageKey;
  }

  /**
   * @return An optional array of arbitrary objects, often for insertion into a resource bundle string
   */
  public Optional<Object[]> getMessageData() {
    return messageData;
  }

  /**
   * @return An optional URI (such as for an Atom feed article)
   */
  public Optional<URI> getUri() {
    return uri;
  }

  @Override
  public String toString() {
    return "EnvironmentSummary{" +
      "alertType=" + alertType +
      ", severity=" + severity +
      ", messageKey=" + messageKey +
      ", messageData=" + messageData +
      '}';
  }
}
