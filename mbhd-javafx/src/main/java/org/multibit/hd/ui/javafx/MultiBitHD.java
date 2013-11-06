package org.multibit.hd.ui.javafx;

import com.google.common.io.Resources;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.screens.ScreenTransitionManager;

import java.util.Locale;

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

    Font.loadFont(Resources.getResource(AwesomeDecorator.FONT_AWESOME_TTF_PATH).toExternalForm(), 10.0);

    // TODO Get the preferred locale
    Locale preferredLocale = Locale.UK;

    // TODO Get the preferred starting position
    primaryStage.centerOnScreen();

    // The screen manager handles the stage for different languages
    ScreenTransitionManager stm = new ScreenTransitionManager(primaryStage);
    stm.onLocaleChanged(preferredLocale);

  }

}
