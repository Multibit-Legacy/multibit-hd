package org.multibit.hd.ui.fest;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.testing.FestSwingTestCaseTemplate;
import org.junit.*;
import org.multibit.hd.core.config.Configurations;
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

import java.io.*;
import java.util.Locale;
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
@Ignore
public class MultiBitHDFestTest extends FestSwingTestCaseTemplate {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHDFestTest.class);

  private FrameFixture window;

  private MultiBitHD testObject;

  @BeforeClass
  public static void setUpOnce() throws Exception {

    FailOnThreadViolationRepaintManager.install();

  }

  @Before
  public void setUp() {

    // Reset the configuration
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    // Allow unrestricted operation
    InstallationManager.unrestricted = true;

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
   * </ul>
   */
  @Test
  public void verifyWelcomeWizardCreateWallet_en_US() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh();

    // Create a wallet through the welcome wizard
    WelcomeWizardCreateWallet_en_US_Requirements.verifyUsing(window);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Create a wallet</li>
   * </ul>
   */
  @Test
  public void verifyWelcomeWizardCreateWallet_ro_RO() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh();

    // Create a wallet through the welcome wizard
    WelcomeWizardCreateWallet_ro_RO_Requirements.verifyUsing(window);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Restore a password from a wallet using a known seed phrase</li>
   * </ul>
   */
  @Test
  public void verifyWelcomeWizardRestorePassword_en_US() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh();

    // Restore a password through the welcome wizard
    WelcomeWizardRestorePassword_en_US_Requirements.verifyUsing(window);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Restore a wallet</li>
   * </ul>
   */
  @Test
  public void verifyWelcomeWizardRestoreWallet_en_US() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh();

    // Restore a wallet through the welcome wizard
    WelcomeWizardRestoreWallet_en_US_Requirements.verifyUsing(window);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

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
    SlowUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Explore the sidebar screens
    SidebarTreeScreensRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture</li>
   * <li>Unlock wallet</li>
   * <li>Exercise the Send/Request screen</li>
   * </ul>
   */
  @Test
  public void verifySendRequestScreen() throws Exception {

    // Start with the empty wallet fixture
    arrangeEmpty();

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Verify
    SendRequestScreenRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with restored wallet fixture</li>
   * <li>Unlock wallet</li>
   * <li>Exercise the Payments screen</li>
   * </ul>
   */
  @Test
  public void verifyPaymentsScreen() throws Exception {

    // Start with the standard wallet fixture
    arrangeStandard();

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Verify
    PaymentsScreenRequirements.verifyUsing(window);

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
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Verify
    ContactsScreenRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture</li>
   * <li>Unlock wallet</li>
   * <li>Exercise the History screen</li>
   * </ul>
   */
  @Test
  public void verifyHistoryScreen() throws Exception {

    // Start with the empty wallet fixture
    arrangeEmpty();

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Examine the history after unlocking
    HistoryScreenRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture</li>
   * <li>Unlock wallet</li>
   * <li>Exercise the Settings screen</li>
   * </ul>
   */
  @Test
  public void verifySettingsScreen() throws Exception {

    // Start with the empty wallet fixture
    arrangeEmpty();

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Verify
    SettingsScreenRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture</li>
   * <li>Unlock wallet</li>
   * <li>Exercise the Tools screen</li>
   * </ul>
   */
  @Test
  public void verifyToolsScreen() throws Exception {

    // Start with the empty wallet fixture
    arrangeEmpty();

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Verify
    ToolsScreenRequirements.verifyUsing(window);

  }

  ////////////////////////////////////////////////////////////////

  /**
   * <p>Starts MultiBit HD with an empty application directory</p>
   *
   * @throws Exception If something goes wrong
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
   * @throws Exception If something goes wrong
   */
  private void arrangeEmpty() throws Exception {

    log.info("Arranging empty wallet fixture environment...");

    // Create a random temporary directory to write the wallets
    File temporaryDirectory = makeRandomTemporaryApplicationDirectory();
    InstallationManager.currentApplicationDataDirectory = SecureFiles.verifyOrCreateDirectory(temporaryDirectory);

    // Copy the MBHD cacerts
    InputStream cacerts = MultiBitHDFestTest.class.getResourceAsStream("/fixtures/"+InstallationManager.CA_CERTS_NAME);
    OutputStream target = new FileOutputStream(new File(temporaryDirectory + "/"+InstallationManager.CA_CERTS_NAME));
    ByteStreams.copy(cacerts, target);

    // Initialise the backup manager
    BackupManager.INSTANCE.initialise(temporaryDirectory, null);

    // Add the empty wallet fixture
    WalletFixtures.createEmptyWalletFixture();

    // Continue with the set up
    setUpAfterArrange();

  }

  /**
   * <p>Starts MultiBit HD with an application directory containing a standard wallet fixture containing real transactions</p>
   *
   * @throws Exception If something goes wrong
   */
  private void arrangeStandard() throws Exception {

    log.info("Arranging burned wallet fixture environment...");

    // Create a random temporary directory to write the wallets
    File temporaryDirectory = makeRandomTemporaryApplicationDirectory();
    InstallationManager.currentApplicationDataDirectory = SecureFiles.verifyOrCreateDirectory(temporaryDirectory);

    // Copy the MBHD cacerts
    InputStream cacerts = MultiBitHDFestTest.class.getResourceAsStream("/fixtures/"+InstallationManager.CA_CERTS_NAME);
    OutputStream target = new FileOutputStream(new File(temporaryDirectory + "/"+InstallationManager.CA_CERTS_NAME));
    ByteStreams.copy(cacerts, target);

    // Initialise the backup manager
    BackupManager.INSTANCE.initialise(temporaryDirectory, null);

    // Add the restored wallet fixture
    WalletFixtures.createStandardWalletFixture();

    // Continue with the set up
    setUpAfterArrange();

  }

  /**
   * <p>Handles the ongoing process of application set up</p>
   *
   * @throws Exception If something goes wrong
   */
  private void setUpAfterArrange() throws Exception {

    log.info("Reset locale to en_US");

    // Always reset back to en_US
    Locale.setDefault(Locale.US);

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

    log.info("Creating FEST frame fixture");

    window = new FrameFixture(frame);

    // Show the frame to test at 100,100 (unavoidable)
    window.show();

    log.info("FEST setup complete");

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
