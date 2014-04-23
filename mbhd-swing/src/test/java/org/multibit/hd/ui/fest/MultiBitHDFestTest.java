package org.multibit.hd.ui.fest;

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
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.fest.requirements.ContactsScreen;
import org.multibit.hd.ui.fest.requirements.SidebarTreeScreens;
import org.multibit.hd.ui.fest.requirements.WelcomeWizardCreateWallet;
import org.multibit.hd.ui.views.MainView;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

/**
 * <p>Abstract base class to provide the following to functional tests:</p>
 * <ul>
 * <li>Access to standard startup</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class MultiBitHDFestTest {

  protected FrameFixture window;

  @BeforeClass
  public static void setUpOnce() throws Exception {
    FailOnThreadViolationRepaintManager.install();

    // Ensure we start with an empty and disposable application data directory

    // Create a random temporary directory to writeContacts the wallets
    File temporaryDirectory = makeRandomTemporaryApplicationDirectory();
    InstallationManager.currentApplicationDataDirectory = SecureFiles.verifyOrCreateDirectory(temporaryDirectory);

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

  /**
   * @return A random temporary directory suitable for use as an application directory
   *
   * @throws java.io.IOException If something goes wrong
   */
  public static File makeRandomTemporaryApplicationDirectory() throws IOException {

    File temporaryFile = File.createTempFile("nothing", "nothing");
    temporaryFile.deleteOnExit();

    File parentDirectory = temporaryFile.getParentFile();

    File temporaryDirectory = new File(parentDirectory.getAbsolutePath() + File.separator + ("" + (new Random()).nextInt(1000000)));
    assertThat(temporaryDirectory.mkdir()).isTrue();

    temporaryDirectory.deleteOnExit();

    return temporaryDirectory;
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

  /**
   * The single overall test script to avoid multiple application restarts and ensure
   * a particular order of requirements testing
   */
  @Test
  public void verifyRequirements() {

    // Start by creating a wallet through the welcome wizard
    WelcomeWizardCreateWallet.verifyUsing(window);

    // Explore the sidebar screens
    SidebarTreeScreens.verifyUsing(window);

    // Create some contacts for use with send/receive later
    ContactsScreen.verifyUsing(window);

  }

}
