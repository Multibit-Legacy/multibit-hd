package org.multibit.hd.ui.javafx.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 * <p>Utility to provide the following to screens:</p>
 * <ul>
 * <li>Various fade transition time lines</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Transitions {

  private static final Duration SHORT = Duration.millis(200);
  private static final Duration LONG = Duration.millis(400);

  /**
   * Utilities have private constructors
   */
  public Transitions() {
  }

  public static Timeline newShortFadeIn(DoubleProperty opacity) {
    return new Timeline(
      new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
      new KeyFrame(SHORT, new KeyValue(opacity, 1.0)));
  }

  /**
   * <p>Provides a smooth fade in using the opacity property (0.0 to 1.0)</p>
   *
   * @param opacity The starting opacity (less than 1.0)
   *
   * @return A time line
   */
  public static Timeline newLongFadeIn(DoubleProperty opacity) {

    return new Timeline(
      new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
      new KeyFrame(LONG, new KeyValue(opacity, 1.0)));
  }

  /**
   * <p>Provides a smooth fade out using the opacity property (1.0 to 0.0)</p>
   *
   * @param opacity                The starting opacity (less than 1.0)
   * @param onFinishedEventHandler The event handler for when the last key frame finishes
   *
   * @return A time line
   */
  public static Timeline newShortFadeOut(
    final DoubleProperty opacity,
    final EventHandler<ActionEvent> onFinishedEventHandler
  ) {
    return new Timeline(
      new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
      new KeyFrame(SHORT, onFinishedEventHandler, new KeyValue(opacity, 0.0)));
  }

  /**
   * <p>Provides a smooth fade out using the opacity property (1.0 to 0.0)</p>
   *
   * @param opacity The starting opacity (less than 1.0)
   *
   * @return A time line
   */
  public static Timeline newLongFadeOut(DoubleProperty opacity) {

    return new Timeline(
      new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
      new KeyFrame(LONG, new KeyValue(opacity, 0.0)));
  }

}
