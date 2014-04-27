package org.multibit.hd.ui.fest;

import com.google.common.io.Files;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.testing.FestSwingTestCaseTemplate;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.testing.WalletFixtures;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.fest.requirements.*;
import org.multibit.hd.ui.views.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.fest.swing.timing.Pause.pause;

/**
 * <p>Abstract base class to provide the following to functional tests:</p>
 * <ul>
 * <li>Access to standard startup</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class MultiBitHDFestTest extends FestSwingTestCaseTemplate {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHDFestTest.class);

  private FrameFixture window;

  private MultiBitHD testObject;

  @BeforeClass
  public static void setUpOnce() throws Exception {

    FailOnThreadViolationRepaintManager.install();

  }

  @After
  public void tearDown() {

    log.debug("FEST: Test complete. Firing 'SOFT' shutdown.");

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    // Allow time for the app to terminate and be garbage collected
    pause(3, TimeUnit.SECONDS);

    log.debug("FEST: Application cleanup should have finished. Performing final cleanup.");

    window.cleanUp();

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Create a wallet</li>
   * <li>Restore a wallet</li>
   * <li>Switch wallets</li>
   * </ul>
   */
  @Test
  public void verifyWelcomeWizard() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh();

    // Create a wallet through the welcome wizard
    WelcomeWizardCreateWallet.verifyUsing(window);

    // Unlock the wallet
    UnlockEmptyWalletFixture.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture</li>
   * <li>Unlock wallet</li>
   * <li>Scan through sidebar screens</li>
   * </ul>
   */
  @Test
  public void verifySidebarScreens() throws Exception {

    // Start with the empty wallet fixture
    arrangeEmpty();

    // Unlock the wallet
    UnlockEmptyWalletFixture.verifyUsing(window);

    // Explore the sidebar screens
    SidebarTreeScreens.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture</li>
   * <li>Unlock wallet</li>
   * <li>Exercise the Contacts screen</li>
   * </ul>
   */
  @Test
  public void verifyContactsScreen() throws Exception {

    // Start with the empty wallet fixture
    arrangeEmpty();

    // Unlock the wallet
    UnlockEmptyWalletFixture.verifyUsing(window);

    // Create some contacts for use with send/receive later
    ContactsScreen.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture</li>
   * <li>Unlock wallet</li>
   * <li>Exercise the Contacts screen</li>
   * </ul>
   */
  @Test
  public void verifyHistoryScreen() throws Exception {

    // Start with the empty wallet fixture
    arrangeEmpty();

    // Unlock the wallet
    UnlockEmptyWalletFixture.verifyUsing(window);

    // Examine the history after unlocking
    HistoryScreen.verifyUsing(window);

  }

  ////////////////////////////////////////////////////////////////

  /**
   * <p>Starts MultiBit HD with an empty application directory</p>
   *
   * @throws Exception
   */
  private void arrangeFresh() throws Exception {

    log.info("Arranging fresh environment...");

    // Create a random temporary directory to write the wallets
    File temporaryDirectory = makeRandomTemporaryApplicationDirectory();
    InstallationManager.currentApplicationDataDirectory = SecureFiles.verifyOrCreateDirectory(temporaryDirectory);

    // Continue with the set up
    setUpAfterArrange();

  }

  /**
   * <p>Starts MultiBit HD with an application directory containing the empty wallet fixture</p>
   *
   * @throws Exception
   */
  private void arrangeEmpty() throws Exception {

    log.info("Arranging empty wallet fixture environment...");

    // Create a random temporary directory to write the wallets
    File temporaryDirectory = makeRandomTemporaryApplicationDirectory();
    InstallationManager.currentApplicationDataDirectory = SecureFiles.verifyOrCreateDirectory(temporaryDirectory);

    // Initialise the backup manager
    BackupManager.INSTANCE.initialise(temporaryDirectory, null);

    // Add the empty wallet fixture
    WalletFixtures.createEmptyWalletFixture();

    // Continue with the set up
    setUpAfterArrange();

  }

  /**
   * <p>Handles the ongoing process of application set up</p>
   *
   * @throws Exception
   */
  private void setUpAfterArrange() throws Exception {

    log.info("Starting MultiBit HD...");

    // Start MultiBit HD within FEST
    testObject = new MultiBitHD();
    testObject.start(null);

    MainView frame = GuiActionRunner.execute(new GuiQuery<MainView>() {
      protected MainView executeInEDT() {

        InstallationManager.getOrCreateApplicationDataDirectory();

        return testObject.initialiseUIViews();
      }
    });

    window = new FrameFixture(frame);
    window.show(); // shows the frame to test
    window.show();
  }

  /**
   * @return A random temporary directory suitable for use as an application directory
   *
   * @throws java.io.IOException If something goes wrong
   */
  private File makeRandomTemporaryApplicationDirectory() throws IOException {

    File temporaryDirectory = Files.createTempDir();
    temporaryDirectory.deleteOnExit();

    return temporaryDirectory;

  }

}
