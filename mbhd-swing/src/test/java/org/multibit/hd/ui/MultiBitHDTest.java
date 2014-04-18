package org.multibit.hd.ui;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.ui.fest.use_cases.welcome.CreateWalletSelectBackupLocationWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome.WelcomeSelectWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome.WelcomeSelectLanguageUseCase;
import org.multibit.hd.ui.views.MainView;

import java.io.File;

import static org.fest.assertions.Fail.fail;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *  
 */
public class MultiBitHDTest {

  private FrameFixture window;

  @BeforeClass
  public static void setUpOnce() throws Exception {
    FailOnThreadViolationRepaintManager.install();

    // Ensure we start with an empty and disposable application data directory
    File applicationDirectory = new File("target");
    InstallationManager.currentApplicationDataDirectory = SecureFiles.verifyOrCreateDirectory(applicationDirectory);

    // Prepare the JVM (Nimbus, system properties etc)
    MultiBitHD.initialiseJVM();

    // Create controllers so that the generic app can access listeners
    if (!MultiBitHD.initialiseUIControllers(null)) {

      fail();

    }

    // Prepare platform-specific integration (protocol handlers, quit events etc)
    MultiBitHD.initialiseGenericApp();

    // Start core services (logging, security alerts, configuration, Bitcoin URI handling etc)
    MultiBitHD.initialiseCore(null);

  }

  @Before
  public void setUp() {

    MainView frame = GuiActionRunner.execute(new GuiQuery<MainView>() {
      protected MainView executeInEDT() {

        InstallationManager.getOrCreateApplicationDataDirectory();

        return MultiBitHD.initialiseUIViews();
      }
    });

    window = new FrameFixture(frame);
    window.show(); // shows the frame to test
  }

  @After
  public void tearDown() {
    window.cleanUp();
  }

  @Test
  public void welcomeWizard_createWallet() {

    new WelcomeSelectLanguageUseCase(window).execute();
    new WelcomeSelectWalletUseCase(window).execute();
    new CreateWalletSelectBackupLocationWalletUseCase(window).execute();

  }

}
