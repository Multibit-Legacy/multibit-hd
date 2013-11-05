package org.multibit.hd.ui.javafx;

import com.google.common.io.Resources;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.multibit.hd.ui.javafx.screens.Screen;
import org.multibit.hd.ui.javafx.screens.ScreenTransitionManager;

public class MultiBitHD extends Application {

  /**
   * <p>Main entry point to the application</p>
   *
   * @param args None specified
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    Font.loadFont(Resources.getResource("assets/fonts/FontAwesome.otf").toExternalForm(), 10);

    ScreenTransitionManager stm = new ScreenTransitionManager();
    stm.addScreen(Screen.WELCOME_LOGIN, Screen.WELCOME_LOGIN.getFxmlResource());
    stm.addScreen(Screen.WELCOME_PROVIDE_INITIAL_SEED, Screen.WELCOME_PROVIDE_INITIAL_SEED.getFxmlResource());
    stm.addScreen(Screen.MAIN_HOME, Screen.MAIN_HOME.getFxmlResource());
    stm.setInitialScreen(Screen.WELCOME_LOGIN);

    Group root = new Group();
    root.getChildren().addAll(stm);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(Resources.getResource("assets/css/main.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.setTitle("MultiBit HD");
    primaryStage.setScene(scene);
    primaryStage.show();

  }
}
