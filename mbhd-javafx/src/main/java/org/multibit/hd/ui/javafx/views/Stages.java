package org.multibit.hd.ui.javafx.views;

import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.multibit.hd.ui.javafx.controllers.main.MainController;
import org.multibit.hd.ui.javafx.controllers.welcome.WelcomeController;

import java.util.EnumSet;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>Factory to provide the following to UI startup:</p>
 * <ul>
 * <li>Stage builders</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Stages {

  /**
   * Utilities have private constructor
   */
  private Stages() {
  }

  /**
   * <p>Build the welcome stage</p>
   *
   * @param locale         The locale
   * @param resourceBundle The resource bundle
   */
  public static void buildWelcomeStage(Locale locale, ResourceBundle resourceBundle) {
    Group welcomeStageSceneGroup = new Group();
    View welcomeStageView = Views.loadView(Screen.WELCOME_STAGE, resourceBundle);
    welcomeStageSceneGroup.getChildren().addAll(welcomeStageView.getScreenParentNode());

    // Locate the detail anchor pane to use as the screen group
    AnchorPane welcomeStageAnchorPane = ((WelcomeController) welcomeStageView.getController()).getDetailAnchorPane();

    // Build the welcome stage
    StageManager
      .WELCOME_STAGE
      .withStage(new Stage())
      .withSceneGroup(welcomeStageSceneGroup)
      .withAnchorPane(welcomeStageAnchorPane)
      .withScreens(EnumSet.of(
        Screen.WELCOME_LOGIN,
        Screen.WELCOME_PROVIDE_INITIAL_SEED
      ))
      .withLocale(locale)
      .withCurrentScreen(Screen.WELCOME_LOGIN)
      .build();
  }

  /**
   * <p>Build the main stage</p>
   *
   * @param locale         The locale
   * @param resourceBundle The resource bundle
   */
  public static void buildMainStage(Locale locale, ResourceBundle resourceBundle) {

    Group mainStageSceneGroup = new Group();
    View mainStageView = Views.loadView(Screen.MAIN_STAGE, resourceBundle);
    mainStageSceneGroup.getChildren().addAll(mainStageView.getScreenParentNode());

    // Locate the detail anchor pane in the main stage to use as the screen group
    AnchorPane mainStageAnchorPane = ((MainController) mainStageView.getController()).getDetailAnchorPane();

    // Build the main stage
    StageManager
      .MAIN_STAGE
      .withStage(new Stage())
      .withSceneGroup(mainStageSceneGroup)
      .withAnchorPane(mainStageAnchorPane)
      .withScreens(EnumSet.of(
        Screen.MAIN_HOME,
        Screen.MAIN_CONTACTS
      ))
      .withLocale(locale)
      .withCurrentScreen(Screen.MAIN_HOME)
      .build();
  }
}
