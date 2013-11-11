package org.multibit.hd.ui.javafx.views;

import com.google.common.base.Optional;
import com.google.common.io.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.multibit.hd.ui.javafx.exceptions.UIException;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Load screen from FXML applying the given locale</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Views {

  /**
   * Utilities have private constructors
   */
  private Views() {
  }

  /**
   * <p>Loads the screen FXML and binds the screen transition managers to its controller</p>
   *
   * @param screen         The screen to load
   * @param resourceBundle The resource bundle to apply during load
   *
   * @return The transition aware controller with parent node set
   */
  public static View loadView(Screen screen, ResourceBundle resourceBundle) {

    FXMLLoader fxmlLoader = new FXMLLoader(Resources.getResource(screen.getFxmlResource()));
    fxmlLoader.setResources(resourceBundle);

    final Parent screenParentNode;
    try {
      screenParentNode = (Parent) fxmlLoader.load();
    } catch (IOException e) {
      throw new UIException(e);
    }

    // Get the controller reference
    ViewAware controller = fxmlLoader.getController();

    // Bind the view to the controller
    View view = new View(
      Optional.<View>absent(),
      screen,
      screenParentNode,
      controller
    );

    // Bind the controller to the view
    controller.setView(view);

    return view;

  }

}
