package org.multibit.hd.ui.models;

import com.google.common.base.Optional;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import javax.swing.*;

/**
 * <p>Value object to provide the following to Alert API:</p>
 * <ul>
 * <li>Provision of state for an alert</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class AlertModel implements Model<String> {

  private final RAGStatus severity;
  private String localisedMessage;

  private int remaining = 0;

  private Optional<JButton> button = Optional.absent();

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

  /**
   * @return The RAG severity level
   */
  public RAGStatus getSeverity() {
    return severity;
  }

  /**
   * @return The localised alert message
   */
  public String getLocalisedMessage() {
    return localisedMessage;
  }

  /**
   * @return The localised message indicating how many other alerts there are
   */
  public String getRemainingText() {
    if (remaining > 0) {
      return Languages.safeText(MessageKey.ALERT_REMAINING, remaining);
    }
    return "";
  }

  /**
   * @return The optional button leading to an action
   */
  public Optional<JButton> getButton() {
    return button;
  }

  public void setButton(JButton button) {
    this.button = Optional.fromNullable(button);
  }

  @Override
  public String getValue() {
    return localisedMessage;
  }

  @Override
  public void setValue(String value) {
    this.localisedMessage = value;
  }

  @Override
  public String toString() {
    return "AlertModel{" +
      "button=" + button +
      ", severity=" + severity +
      ", localisedMessage='" + localisedMessage + '\'' +
      ", remaining=" + remaining +
      '}';
  }
}
