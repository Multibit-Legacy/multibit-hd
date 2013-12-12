package org.multibit.hd.ui.events.view;

/**
 * <p>Event to provide the following to views:</p>
 * <ul>
 * <li>Essential information for a progress change</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ProgressChangedEvent {

  private final String localisedMessage;
  private final int percent;

  /**
   * @param localisedMessage The localised message describing the overall process that is progressing
   * @param percent          The percent
   */
  public ProgressChangedEvent(String localisedMessage, int percent) {

    this.localisedMessage = localisedMessage;
    this.percent = percent;

  }

  /**
   * @return The localised message describing the overall process that is progressing
   */
  public String getLocalisedMessage() {
    return localisedMessage;
  }

  /**
   * @return The percent value to display
   */
  public int getPercent() {
    return percent;
  }

  @Override
  public String toString() {
    return "ProgressChangedEvent{" +
      "localisedMessage='" + localisedMessage + '\'' +
      ", percent=" + percent +
      '}';
  }
}
