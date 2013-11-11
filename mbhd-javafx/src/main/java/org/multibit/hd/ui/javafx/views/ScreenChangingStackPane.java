package org.multibit.hd.ui.javafx.views;

import com.google.common.base.Preconditions;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * <p>Manager to provide the following to UI:</p>
 * <ul>
 * <li>Smooth transitions between views</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ScreenChangingStackPane extends StackPane {

  /**
   * @param stage      The stage that these screens belong to
   * @param anchorPane The anchor pane containing the screens
   * @param parentNode The parent node of the new screen
   */
  public ScreenChangingStackPane(
    Stage stage,
    AnchorPane anchorPane,
    Parent parentNode
  ) {

    Preconditions.checkNotNull(stage, "'stage' must be present");
    Preconditions.checkNotNull(anchorPane, "'anchorPane' must be present");
    Preconditions.checkNotNull(parentNode, "'parentNode' must be present");

    // Wrap the screen group in this stack pane
    getChildren().addAll(parentNode);

    // Add this stack pane under the anchor pane containing these screens
    anchorPane.getChildren().addAll(this);

  }

  /**
   * <p>Provides an animation to transition between screens</p>
   *
   * @param parentNode The parent node of the new screen
   */
  public void transitionTo(final Parent parentNode) {

    // Get the opacity property
    final DoubleProperty opacity = opacityProperty();

    if (getChildren().isEmpty()) {

      // Set to fade in
      setOpacity(0.0);

      // Add the new directly to the top of the stack
      getChildren().add(parentNode);

      // Play the fade in time line
      Transitions.newShortFadeIn(opacity).play();

    } else {

      // There is more than one screen so fade out the first then fade in the second

      // Set up the event handler for the end of the fade out
      EventHandler<ActionEvent> onFinishedHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {

          // End of the fade out

          // Remove the current screen
          getChildren().remove(0);

          // Set to fade in
          setOpacity(0.0);

          // Add the new at the top of the stack
          getChildren().add(0, parentNode);

          // Play the fade in time line
          Transitions.newShortFadeIn(opacity).play();
        }
      };

      // Play the fade out time line
      Transitions.newShortFadeOut(opacity, onFinishedHandler).play();

    }

  }

}