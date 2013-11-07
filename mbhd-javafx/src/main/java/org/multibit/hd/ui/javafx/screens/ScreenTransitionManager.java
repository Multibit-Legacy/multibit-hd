package org.multibit.hd.ui.javafx.screens;

import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.multibit.hd.ui.javafx.exceptions.UIException;
import org.multibit.hd.ui.javafx.i18n.Languages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>Manager to provide the following to UI:</p>
 * <ul>
 * <li>Loading resource bundles for FXML</li>
 * <li>Selecting </li>
 * <li>Smooth transitions between screens</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ScreenTransitionManager extends StackPane {

  private static final Logger log = LoggerFactory.getLogger(ScreenTransitionManager.class);

  private final HashMap<Screen, Node> screens = new HashMap<>();

  private ResourceBundle resourceBundle;

  private Screen currentScreen;
  private final Stage primaryStage;
  //private final GenericApplication genericApplication;

  public ScreenTransitionManager(Stage primaryStage, Screen initialScreen) {

    Preconditions.checkNotNull(primaryStage, "'primaryStage' must be present");

    this.primaryStage = primaryStage;

    this.currentScreen = initialScreen;

    log.info("Configuring native event handling");

    // TODO Get this working
    //GenericApplicationSpecification specification = new GenericApplicationSpecification();
    //GenericEventController controller = new GenericEventController();
    //specification.getOpenURIEventListeners().add(controller);
    //genericApplication = GenericApplicationFactory.INSTANCE.buildGenericApplication(specification);

    // Configure the system menu

    MenuBar mb = new MenuBar();
    mb.setUseSystemMenuBar(true);

    final Menu menu1 = new Menu("File");
    final Menu menu2 = new Menu("Options");
    final Menu menu3 = new Menu("Help");

    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(menu1, menu2, menu3);

  }

  /**
   * <p>Reset all scenes based on the new locale</p>
   *
   * @param locale The new locale
   */
  public void onLocaleChanged(Locale locale) {

    Preconditions.checkNotNull(locale, "'locale' must be present");

    log.info("Set locale to {}", locale);

    // Set the resource bundle to use for all new controllers
    resourceBundle = Languages.newResourceBundle(locale);

    // Reload all the available screens
    loadAllScreens();

    // Keep the current screen
    getChildren().add(0, screens.get(currentScreen));

    // Treat this as a single group
    Group root = new Group();
    root.getChildren().addAll(this);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(Resources.getResource("assets/css/main.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.setTitle(resourceBundle.getString("multiBitFrame.title"));

    primaryStage.show();

  }

  /**
   * <p>Add a screen from FXML</p>
   *
   * @param screen The screen
   *
   * @return True if the screen was added
   */
  public boolean addScreen(Screen screen) {

    FXMLLoader fxmlLoader = new FXMLLoader(Resources.getResource(screen.getFxmlResource()));
    fxmlLoader.setResources(resourceBundle);
    Parent rootNode;

    try {
      rootNode = (Parent) fxmlLoader.load();
    } catch (IOException e) {
      throw new UIException(e);
    }

    // Bind the transition manager to the controller
    TransitionAware controller = fxmlLoader.getController();
    controller.setScreenTransitionManager(this);

    return screens.put(screen, rootNode) == null;

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

    currentScreen = screen;
  }

  /**
   * Handles the process of loading all the screens with the current resource bundle
   */
  private void loadAllScreens() {
    this.getChildren().clear();
    screens.clear();

    for (Screen screen : Screen.values()) {
      addScreen(screen);
    }
  }

}