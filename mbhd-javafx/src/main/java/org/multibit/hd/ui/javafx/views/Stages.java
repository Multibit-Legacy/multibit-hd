package org.multibit.hd.ui.javafx.views;

import com.google.common.base.Preconditions;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.multibit.hd.ui.javafx.config.Configuration;
import org.multibit.hd.ui.javafx.controllers.main.MainController;
import org.multibit.hd.ui.javafx.controllers.welcome.WelcomeController;
import org.multibit.hd.ui.javafx.i18n.Languages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.ResourceBundle;

/**
 * <p>Factory to provide the following to UI startup:</p>
 * <ul>
 * <li>Stage builders</li>
 * <li>Global resource bundle</li>
 * <li>Global configuration</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Stages {

  private static final Logger log = LoggerFactory.getLogger(Stages.class);

  private static ResourceBundle resourceBundle;
  private static Configuration configuration;

  /**
   * Utilities have private constructor
   */
  private Stages() {
  }

  /**
   * @param configuration The runtime configuration
   */
  public static void setConfiguration(Configuration configuration) {
    Stages.configuration = configuration;
  }

  /**
   * @return The runtime configuration
   */
  public static Configuration getConfiguration() {
    return configuration;
  }

  /**
   * <p>Build all the stages</p>
   */
  public static void build() {

    Preconditions.checkNotNull(configuration, "'confiugration' must be present");

    log.debug("Configuration change: {}", configuration);

    Stages.resourceBundle = Languages.newResourceBundle(configuration.getLocale());

    Preconditions.checkNotNull(resourceBundle, "'resourceBundle' must be present");

    Stages.buildWelcomeStage();
    Stages.buildMainStage();

  }

  /**
   * <p>Build the welcome stage</p>
   */
  private static void buildWelcomeStage() {

    log.debug("Building welcome stage");

    Group welcomeStageSceneGroup = new Group();
    View welcomeStageView = Views.loadView(Screen.WELCOME_STAGE, resourceBundle);
    welcomeStageSceneGroup.getChildren().addAll(welcomeStageView.getScreenParentNode());

    // Locate the detail anchor pane to use as the screen group
    AnchorPane welcomeStageAnchorPane = ((WelcomeController) welcomeStageView.getController()).getDetailAnchorPane();

    // Build the welcome stage
    Stage welcomeStage = new Stage();
    welcomeStage.setHeight(600);
    welcomeStage.setWidth(800);

    StageManager
      .WELCOME_STAGE
      .reset()
      .withStage(welcomeStage)
      .withSceneGroup(welcomeStageSceneGroup)
      .withAnchorPane(welcomeStageAnchorPane)
      .withScreens(EnumSet.of(
        Screen.WELCOME_LOGIN,
        Screen.WELCOME_PROVIDE_INITIAL_SEED
      ))
      .withCurrentScreen(Screen.WELCOME_LOGIN)
      .build();
  }

  /**
   * <p>Build the main stage</p>
   */
  private static void buildMainStage() {

    log.debug("Building main stage");

    Group mainStageSceneGroup = new Group();
    View mainStageView = Views.loadView(Screen.MAIN_STAGE, resourceBundle);
    mainStageSceneGroup.getChildren().addAll(mainStageView.getScreenParentNode());

    // Locate the detail anchor pane in the main stage to use as the screen group
    AnchorPane mainStageAnchorPane = ((MainController) mainStageView.getController()).getDetailAnchorPane();

    // Build the main stage
    Stage mainStage = new Stage();
    mainStage.setHeight(1024);
    mainStage.setWidth(1280);

    StageManager
      .MAIN_STAGE
      .reset()
      .withStage(mainStage)
      .withSceneGroup(mainStageSceneGroup)
      .withAnchorPane(mainStageAnchorPane)
      .withScreens(EnumSet.of(
        Screen.MAIN_HOME,
        Screen.MAIN_CONTACTS,
        Screen.MAIN_SETTINGS,
        Screen.MAIN_HELP,
        Screen.MAIN_WALLET
      ))
      .withCurrentScreen(Screen.MAIN_HOME)
      .build();
  }
}
