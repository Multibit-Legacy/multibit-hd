package org.multibit.hd.ui.events.view;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates the progress gauge value has changed</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ProgressChangedEvent implements ViewEvent {

  private final String localisedMessage;
  private final int percent;

  /**
   * <p>Create a progress changed event. Subscribers must interpret progress values as below:</p>
   * <ul>
   * <li>-1 or below: Hide the progress bar</li>
   * <li>0 - 99: Show the bar with "in progress" colouring</li>
   * <li>100 or higher: Show the bar with "success" colouring.</li>
   * </ul>
   *
   * @param localisedMessage The localised message describing the overall process that is progressing
   * @param percent          The percent as defined above. Values greater than 100 will be minimised to 100.
   */
  public ProgressChangedEvent(String localisedMessage, int percent) {

    this.localisedMessage = localisedMessage;
    this.percent = Math.min(100, percent);

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
