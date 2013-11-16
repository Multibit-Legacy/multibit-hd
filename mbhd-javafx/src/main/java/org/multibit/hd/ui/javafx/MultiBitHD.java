package org.multibit.hd.ui.javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.multibit.hd.ui.javafx.config.Configurations;
import org.multibit.hd.ui.javafx.controllers.main.GenericEventController;
import org.multibit.hd.ui.javafx.logging.LoggingFactory;
import org.multibit.hd.ui.javafx.platform.GenericApplication;
import org.multibit.hd.ui.javafx.platform.GenericApplicationFactory;
import org.multibit.hd.ui.javafx.platform.GenericApplicationSpecification;
import org.multibit.hd.ui.javafx.views.Screen;
import org.multibit.hd.ui.javafx.views.StageManager;
import org.multibit.hd.ui.javafx.views.Stages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiBitHD extends Application {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHD.class);

  // TODO Implement this
  private static GenericApplication genericApplication = null;

  /**
   * <p>Main entry point to the application</p>
   *
   * @param args None specified
   */
  public static void main(final String[] args) {

    // Start the logging factory
    LoggingFactory.bootstrap();

    // Provide a default uncaught exception handler
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        log.error(e.getMessage(), e);
        StageManager.MAIN_STAGE.handOver(StageManager.MAIN_STAGE, Screen.MAIN_ERROR);
      }
    });

    launch(args);
  }

  @Override
  public void init() throws Exception {

    registerEventListeners();

  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    // Load configuration
    loadConfiguration();

    // Configure logging
    new LoggingFactory(Configurations.currentConfiguration.getLoggingConfiguration(), "MultiBit HD").configure();

    // Build the stages
    Stages.build();

    // Always start with the welcome stage
    StageManager.MAIN_STAGE.show();

  }

  /**
   * <p>Load the configuration from the application data directory</p>
   */
  private void loadConfiguration() {

    Configurations.currentConfiguration = Configurations.readConfiguration();

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
}
