package org.multibit.hd.ui.javafx;

import com.google.common.io.Resources;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.multibit.hd.ui.javafx.controllers.main.GenericEventController;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.platform.GenericApplication;
import org.multibit.hd.ui.javafx.platform.GenericApplicationFactory;
import org.multibit.hd.ui.javafx.platform.GenericApplicationSpecification;
import org.multibit.hd.ui.javafx.screens.Screen;
import org.multibit.hd.ui.javafx.screens.ScreenTransitionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class MultiBitHD extends Application {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHD.class);
  private static GenericApplication genericApplication;

  /**
   * <p>Main entry point to the application</p>
   *
   * @param args None specified
   */
  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void init() throws Exception {

    Toolkit.getToolkit();
    registerEventListeners();

  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    // Load preferences
    loadPreferences();

    // Load fonts
    loadFonts();

    // TODO Get the preferred locale
    Locale preferredLocale = Locale.UK;

    // The screen transition manager handles i18n and transitions
    ScreenTransitionManager stm = new ScreenTransitionManager(primaryStage, Screen.WELCOME_LOGIN);
    stm.onLocaleChanged(preferredLocale);

  }

  private void registerEventListeners() {

    // TODO Get this working

    /*

    Notes:

    GenericEvents = Working menu in MacOS
    GenericEvents then JavaFx = Good menu but no quit event
    JavaFx then GenericEvents = Nasty error messages and dead app

    Selective removal does not affect this
    Change of launching thread does not affect this

    EAWT might be deprecated in MacOS (no mention after Java6)

    Java8 might have full integration of MacOS menu handling

    */

    log.info("Configuring native event handling");
    GenericApplicationSpecification specification = new GenericApplicationSpecification();

    GenericEventController coreController = new GenericEventController();

    specification.getOpenURIEventListeners().add(coreController);
    specification.getPreferencesEventListeners().add(coreController);
    specification.getAboutEventListeners().add(coreController);
    specification.getQuitEventListeners().add(coreController);

    genericApplication = GenericApplicationFactory.INSTANCE.buildGenericApplication(specification);


  }

  private void loadPreferences() {
    //To change body of created methods use File | Settings | File Templates.
  }

  /**
   * <p>Load all the supporting fonts from the classpath</p>
   * <p>This must be done before any screens are loaded</p>
   */
  private void loadFonts() {
    Font.loadFont(Resources.getResource(AwesomeDecorator.FONT_AWESOME_TTF_PATH).toExternalForm(), 10.0);
  }

}
