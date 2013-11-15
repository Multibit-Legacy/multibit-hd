package org.multibit.hd.ui.javafx.views;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.multibit.hd.ui.javafx.config.Configurations;
import org.multibit.hd.ui.javafx.i18n.Languages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * <p>Enum to provide the following to UI:</p>
 * <ul>
 * <li>Access to stages and transitions between scenes</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum StageManager {

  WELCOME_STAGE,
  MAIN_STAGE

  // End of enum
  ;

  /**
   * Provide logging for this class
   */
  private static final Logger log = LoggerFactory.getLogger(StageManager.class);

  private Stage stage;

  private EnumSet<Screen> screens;

  /**
   * The group that acts as the root of the overall scene (e.g. an anchor pane with title, split pane etc)
   */
  private Group sceneGroup = new Group();

  /**
   * The stage screens (e.g. WELCOME_LOGIN, WELCOME_PROVIDE_INITIAL_SEED etc)
   */
  private Map<Screen, View> screenViewMap = Maps.newHashMap();

  /**
   * The current screen
   */
  private Screen currentScreen;

  /**
   * The screen changer (manages transition animations within the screen view map)
   */
  private ScreenChangingStackPane screenChanger;

  /**
   * The anchor pane for the screen changer to use as its basis
   */
  private AnchorPane anchorPane;

  /**
   * @param latchedStage If the stage is already set this will be ignored to ensure a smooth language transition
   *
   * @return The stage manager
   */
  public StageManager withStage(Stage latchedStage) {

    // Cannot set a new stage since locale change will look awkward
    if (stage != null) {
      return this;
    }

    this.stage = latchedStage;
    return this;
  }

  /**
   * @param screens The set of screens associated with this stage
   *
   * @return The stage manager
   */
  public StageManager withScreens(EnumSet<Screen> screens) {
    this.screens = screens;
    return this;
  }

  /**
   * @param sceneGroup The group that acts as the root of the overall scene
   *
   * @return The stage manager
   */
  public StageManager withSceneGroup(Group sceneGroup) {
    this.sceneGroup = sceneGroup;
    return this;
  }

  /**
   * @param anchorPane The anchor pane acting as the root of the screens
   *
   * @return The stage manager
   */
  public StageManager withAnchorPane(AnchorPane anchorPane) {
    this.anchorPane = anchorPane;
    return this;
  }

  /**
   * @param screen The initial screen to display
   *
   * @return The stage manager
   */
  public StageManager withCurrentScreen(Screen screen) {
    this.currentScreen = screen;
    return this;
  }

  /**
   * <p>Build the stage by loading the views for the screens</p>
   */
  public void build() {

    log.debug("Building {}", this.name());

    Preconditions.checkNotNull(screens, "'screens' must be present");
    Preconditions.checkNotNull(screenViewMap, "'screenViewMap' must be present");

    ResourceBundle resourceBundle = Languages.newResourceBundle(Configurations.currentConfiguration.getLocale());

    // Clear any existing views
    screenViewMap.clear();

    // Load the screens
    for (Screen screen : screens) {
      View view = Views.loadView(screen, resourceBundle);
      screenViewMap.put(screen, view);
    }

    Preconditions.checkState(screenViewMap.containsKey(currentScreen), "Screen view map does not contain " + currentScreen);

    screenChanger = new ScreenChangingStackPane(
      stage,
      anchorPane,
      screenViewMap.get(currentScreen).getScreenParentNode()
    );

    // Ensure the scene uses the same stylesheet
    Scene scene = new Scene(sceneGroup);
    scene.getStylesheets().add(Resources.getResource("assets/css/main.css").toExternalForm());

    stage.setScene(scene);

    changeScreen(currentScreen);
  }

  /**
   * <p>Reset the stage in preparation for a language change</p>
   *
   * @return The stage manager
   */
  public synchronized StageManager reset() {

    screens = null;
    sceneGroup = null;
    screenViewMap.clear();
    currentScreen = null;
    screenChanger = null;
    anchorPane = null;

    return this;
  }

  /**
   * <p>Show this stage</p>
   */
  public void show() {
    log.debug("Showing {}.{}", this.name(), currentScreen);
    stage.show();
  }

  /**
   * <p>Hide this stage</p>
   */
  public void hide() {
    log.debug("Hiding {}.{}", this.name(), currentScreen);
    stage.hide();
  }

  /**
   * <p>Change to another screen present on the current stage</p>
   *
   * @param screen The destination screen
   */
  public void changeScreen(Screen screen) {

    Preconditions.checkState(screenViewMap.containsKey(screen), "'" + screen + "' is not present on this stage");

    log.debug("Transition to {}", screen);
    screenChanger.transitionTo(screenViewMap.get(screen).getScreenParentNode());
  }

  /**
   * <p>Hand over control to a different stage manager</p>
   *
   * @param stageManager The replacement stage manager
   * @param screen       The initial screen
   */
  public void handOver(StageManager stageManager, Screen screen) {

    log.debug("Hand over to {}.{}", stageManager, screen);
    this.hide();
    stageManager.changeScreen(screen);
    stageManager.show();
  }
}
