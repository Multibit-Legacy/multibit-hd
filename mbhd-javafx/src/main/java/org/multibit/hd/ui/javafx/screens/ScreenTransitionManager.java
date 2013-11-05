package org.multibit.hd.ui.javafx.screens;

import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.multibit.hd.ui.javafx.exceptions.Exceptions;

import java.io.IOException;
import java.util.HashMap;

/**
 * <p>Manager to provide the following to UI:</p>
 * <ul>
 * <li>Handles smooth transitions between screens</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ScreenTransitionManager extends StackPane {

  private HashMap<Screen, Node> screens = new HashMap<>();

  /**
   * <p>Add a screen</p>
   *
   * @param screen   The screen
   * @param rootNode The root node for the screen
   *
   * @return True if the screen was added without an overwrite
   */
  public boolean addScreen(Screen screen, Node rootNode) {
    return screens.put(screen, rootNode) == null;
  }

  /**
   * <p>Add a screen from FXML</p>
   *
   * @param screen   The screen
   * @param resource The resource containing the FXML describing the root node
   *
   * @return True if the screen was added
   */
  public boolean addScreen(Screen screen, String resource) {

    FXMLLoader fxmlLoader = new FXMLLoader(Resources.getResource(resource));
    Parent rootNode = null;

    try {
      rootNode = (Parent) fxmlLoader.load();
    } catch (IOException e) {
      Exceptions.rethrow(e);
    }

    TransitionAware controller = fxmlLoader.getController();
    controller.setScreenTransitionManager(this);

    return addScreen(screen, rootNode);

  }

  /**
   * <p>Remove a screen</p>
   *
   * @param screen The screen
   */
  public void removeScreen(Screen screen) {

    Preconditions.checkState(screens.containsKey(screen), "'" + screen + "' has not been loaded");

    screens.remove(screen);
  }

  /**
   * <p>Sets the initial screen to start with</p>
   *
   * @param screen The starting screen
   */
  public void setInitialScreen(final Screen screen) {

    getChildren().add(0, screens.get(screen));

  }

  /**
   * <p>Provides an animation to transition between screens</p>
   *
   * @param screen The screen name to switch to
   */
  public void transitionTo(final Screen screen) {

    Preconditions.checkState(screens.containsKey(screen), "'" + screen + "' has not been loaded");

    // Get the opacity property
    final DoubleProperty opacity = opacityProperty();

    if (getChildren().isEmpty()) {

      // Set to fade in
      setOpacity(0.0);

      // Add the new directly to the top of the stack
      getChildren().add(screens.get(screen));

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
          getChildren().add(0, screens.get(screen));

          // Play the fade in time line
          Transitions.newShortFadeIn(opacity).play();
        }
      };

      // Play the fade out time line
      Transitions.newShortFadeOut(opacity, onFinishedHandler).play();

    }
  }

}