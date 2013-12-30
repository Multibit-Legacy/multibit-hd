package org.multibit.hd.ui.models;

import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.api.RAGStatus;
import org.multibit.hd.ui.i18n.Languages;

/**
 * <p>Value object to provide the following to Alert API:</p>
 * <ul>
 * <li>Provision of state for an alert</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class AlertModel implements Model {

  private final RAGStatus severity;
  private final String localisedMessage;

  private int remaining = 0;

  public AlertModel(String localisedMessage, RAGStatus severity) {
    this.severity = severity;
    this.localisedMessage = localisedMessage;
  }

  public int getRemaining() {
    return remaining;
  }

  public void setRemaining(int remaining) {
    this.remaining = remaining;
  }

  public RAGStatus getSeverity() {
    return severity;
  }

  public String getLocalisedMessage() {
    return localisedMessage;
  }

  public String getRemainingText() {
    if (remaining > 0) {
      return Languages.safeText(MessageKey.ALERT_REMAINING,remaining);
    }
    return "";
  }
}
