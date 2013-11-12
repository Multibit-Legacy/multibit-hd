package org.multibit.hd.ui.javafx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import javafx.application.Application;
import javafx.stage.Stage;
import org.multibit.hd.ui.javafx.config.Configuration;
import org.multibit.hd.ui.javafx.controllers.main.GenericEventController;
import org.multibit.hd.ui.javafx.exceptions.UIException;
import org.multibit.hd.ui.javafx.logging.LoggingFactory;
import org.multibit.hd.ui.javafx.platform.GenericApplication;
import org.multibit.hd.ui.javafx.platform.GenericApplicationFactory;
import org.multibit.hd.ui.javafx.platform.GenericApplicationSpecification;
import org.multibit.hd.ui.javafx.utils.Streams;
import org.multibit.hd.ui.javafx.views.StageManager;
import org.multibit.hd.ui.javafx.views.Stages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MultiBitHD extends Application {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHD.class);

  /**
   * The location of the configuration file
   */
  private static final String CONFIGURATION_PATH = "mbhd.yml";

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
      }
    });

    launch(args);
  }

  @Override
  public void init() throws Exception {

    // registerEventListeners();

  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    // Load configuration
    loadConfiguration();

    // Configure logging
    new LoggingFactory(Stages.getConfiguration().getLogging(), "MultiBit HD").configure();

    // Build the stages
    Stages.build();

    // Always start with the welcome stage
    StageManager.WELCOME_STAGE.show();

  }

  /**
   * <p>Load the configuration from the file system</p>
   */
  private void loadConfiguration() {

    // Read the external configuration
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    InputStream fis = null;
    Configuration configuration;
    try {
      fis = new FileInputStream(CONFIGURATION_PATH);
      configuration = mapper.readValue(fis, Configuration.class);
    } catch (IOException e) {
      throw new UIException(e);
    } finally {
      Streams.closeQuietly(fis);
    }

    Preconditions.checkNotNull(configuration, "Configuration must be present");

    Stages.setConfiguration(configuration);

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
