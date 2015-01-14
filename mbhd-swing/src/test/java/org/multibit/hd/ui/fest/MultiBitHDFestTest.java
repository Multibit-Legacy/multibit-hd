package org.multibit.hd.ui.fest;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.testing.FestSwingTestCaseTemplate;
import org.junit.*;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.Yaml;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.trezor.clients.AbstractTrezorHardwareWalletClient;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
import org.multibit.hd.testing.HardwareWalletFixtureType;
import org.multibit.hd.testing.TrezorHardwareWalletClientFixtures;
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

    // Allow unrestricted operation
    // This will force the use of a temporary directory for application configuration
    // ensuring that existing configurations and wallets are untouched
    InstallationManager.unrestricted = true;

    // Reset the configuration
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @After
  public void tearDown() {

    // Make this message stand out
    log.warn("FEST: Test complete. Firing 'SOFT' shutdown.");

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    // Allow time for the app to terminate and be garbage collected
    pause(3, TimeUnit.SECONDS);

    log.debug("FEST: Application cleanup should have finished. Performing final cleanup.");

    testObject.stop();

    window.cleanUp();

    // Reset the installation manager
    InstallationManager.shutdownNow(ShutdownEvent.ShutdownType.SOFT);

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
    arrangeFresh(HardwareWalletFixtureType.NONE);

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
    arrangeFresh(HardwareWalletFixtureType.NONE);

    // Create a wallet through the welcome wizard
    WelcomeWizardCreateWallet_ro_RO_Requirements.verifyUsing(window);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Restore a wallet using the seed phrase and timestamp</li>
   * </ul>
   */
  @Test
  public void verifyWelcomeWizardRestoreWallet_en_US() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh(HardwareWalletFixtureType.NONE);

    // Restore a wallet through the welcome wizard
    WelcomeWizardRestoreWallet_en_US_Requirements.verifyUsing(window);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with standard application directory</li>
   * <li>Show the credentials unlock screen and restore from there</li>
   * </ul>
   */
  @Test
  public void verifyRestorePassword() throws Exception {

    // Start with the standard empty wallet in a random application directory
    arrangeStandard(HardwareWalletFixtureType.NONE);

    // Restore a password through the welcome wizard
    WelcomeWizardRestorePasswordRequirements.verifyUsing(window);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with standard application directory</li>
   * <li>Show the credentials unlock screen and click restore</li>
   * <li>Back out of the restore by selecting an existing wallet</li>
   * </ul>
   */
  @Test
  public void verifyUseExistingWallet() throws Exception {

    // Start with the standard empty wallet in a random application directory
    arrangeStandard(HardwareWalletFixtureType.NONE);

    // Use  the welcome wizard
    WelcomeWizardUseExistingWalletRequirements.verifyUsing(window);

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
    arrangeEmpty(HardwareWalletFixtureType.NONE);

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

    // Start with the standard wallet fixture (require contacts)
    arrangeStandard(HardwareWalletFixtureType.NONE);

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
    arrangeStandard(HardwareWalletFixtureType.NONE);

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
    arrangeEmpty(HardwareWalletFixtureType.NONE);

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
   * <li>Exercise the Settings screen</li>
   * </ul>
   */
  @Test
  public void verifySettingsScreen() throws Exception {

    // Start with the empty wallet fixture
    arrangeEmpty(HardwareWalletFixtureType.NONE);

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
   * <li>Exercise the Manage Wallet screen</li>
   * </ul>
   */
  @Test
  public void verifyManageWalletScreen() throws Exception {

    // Start with the empty wallet fixture
    arrangeEmpty(HardwareWalletFixtureType.NONE);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Verify
    ManageWalletScreenRequirements.verifyUsing(window);

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

    // Start with the standard wallet fixture
    arrangeEmpty(HardwareWalletFixtureType.NONE);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Verify
    ToolsScreenRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh wallet fixture (cold)</li>
   * <li>Create a new Trezor hardware wallet</li>
   * <li>Unlock the wallet</li>
   * </ul>
   */
  @Test
  public void verifyCreateTrezorHardwareWallet_ColdStart() throws Exception {

    // Prepare an empty and attached Trezor device that will be initialised
    HardwareWalletEventFixtures.prepareCreateTrezorWalletUseCaseEvents();

    // Start with a fresh environment
    arrangeFresh(HardwareWalletFixtureType.TREZOR_WIPED);

    // Verify
    CreateTrezorHardwareWalletColdStartRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture (warm)</li>
   * <li>Create a new Trezor hardware wallet</li>
   * <li>Unlock the wallet</li>
   * </ul>
   */
  @Test
  public void verifyCreateTrezorHardwareWallet_WarmStart() throws Exception {

    // Prepare an empty and attached Trezor device that will be initialised
    HardwareWalletEventFixtures.prepareCreateTrezorWalletUseCaseEvents();

    // Start with an empty environment
    arrangeEmpty(HardwareWalletFixtureType.TREZOR_WIPED);

    // Verify
    CreateTrezorHardwareWalletWarmStartRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture (warm)</li>
   * <li>Unlock wallet</li>
   * </ul>
   */
  @Test
  public void verifyUnlockTrezorHardwareWallet_WarmStart() throws Exception {

    // Prepare an initialised and attached Trezor device that will be unlocked
    HardwareWalletEventFixtures.prepareUnlockTrezorWalletUseCaseEvents();

    // Start with the empty hardware wallet fixture
    arrangeEmpty(HardwareWalletFixtureType.TREZOR_INITIALISED);

    // Verify
    UnlockTrezorHardwareWalletWarmStartRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh wallet fixture (cold)</li>
   * <li>Unlock wallet</li>
   * </ul>
   *
   * TODO Need to add cold start functionality into WelcomeWizard
   */
  @Ignore
  public void verifyUnlockTrezorHardwareWallet_ColdStart() throws Exception {

    // Prepare an initialised and attached Trezor device that will be unlocked
    HardwareWalletEventFixtures.prepareUnlockTrezorWalletUseCaseEvents();

    // Start with the fresh hardware wallet fixture
    arrangeFresh(HardwareWalletFixtureType.TREZOR_INITIALISED);

    // Verify
    UnlockTrezorHardwareWalletColdStartRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with empty wallet fixture (warm)</li>
   * <li>Plug trezor out, see password screen, plug in, see PIN matrix</li>
   * </ul>
   */
  @Test
  public void verifyPlugInAndPullOutTrezorHardwareWallet() throws Exception {

    // Prepare an initialised and attached Trezor device that will be unlocked
    HardwareWalletEventFixtures.preparePlugInAndPullOutTrezorWalletUseCaseEvents();

    // Start with the empty hardware wallet fixture
    arrangeEmpty(HardwareWalletFixtureType.TREZOR_INITIALISED);

    // Verify
    PlugInAndPullOutTrezorHardwareWalletRequirements.verifyUsing(window);

  }

  ////////////////////////////////////////////////////////////////

  /**
   * <p>Cold start</p>
   * <p>Starts MultiBit HD with an empty application directory</p>
   *
   * @param hardwareWalletFixtureType The hardware wallet fixture type
   *
   * @throws Exception If something goes wrong
   */
  private void arrangeFresh(HardwareWalletFixtureType hardwareWalletFixtureType) throws Exception {

    log.info("Arranging fresh environment...");

    // Continue with the set up
    setUpAfterArrange(false, hardwareWalletFixtureType);

  }

  /**
   * <p>Warm start</p>
   * <p>Starts MultiBit HD with an application directory containing the empty wallet fixture and an accepted licence</p>
   *
   * @throws Exception If something goes wrong
   */
  private void arrangeEmpty(HardwareWalletFixtureType hardwareWalletFixtureType) throws Exception {

    log.info("Arranging empty wallet fixture environment...");

    // Get the temporary application directory
    File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Copy the MBHD cacerts
    InputStream cacerts = MultiBitHDFestTest.class.getResourceAsStream("/fixtures/" + InstallationManager.CA_CERTS_NAME);
    OutputStream target = new FileOutputStream(new File(applicationDirectory + "/" + InstallationManager.CA_CERTS_NAME));
    ByteStreams.copy(cacerts, target);

    // Initialise the backup manager
    BackupManager.INSTANCE.initialise(applicationDirectory, Optional.<File>absent());

    // Add the empty wallet fixture
    WalletFixtures.createEmptyMBHDSoftWalletFixture();

    // Continue with the set up
    setUpAfterArrange(true, hardwareWalletFixtureType);

  }

  /**
   * <p>Warm start</p>
   * <p>Starts MultiBit HD with an application directory containing a standard wallet fixture containing real transactions and contacts</p>
   *
   * @param hardwareWalletFixtureType The hardware wallet fixture type
   *
   * @throws Exception If something goes wrong
   */
  private void arrangeStandard(HardwareWalletFixtureType hardwareWalletFixtureType) throws Exception {

    log.info("Arranging standard wallet fixture environment...");

    // Get the temporary application directory
    File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Copy the MBHD cacerts
    InputStream cacerts = MultiBitHDFestTest.class.getResourceAsStream("/fixtures/" + InstallationManager.CA_CERTS_NAME);
    OutputStream target = new FileOutputStream(new File(applicationDirectory + "/" + InstallationManager.CA_CERTS_NAME));
    ByteStreams.copy(cacerts, target);

    // Initialise the backup manager to use the temporary directory
    BackupManager.INSTANCE.initialise(applicationDirectory, Optional.<File>absent());

    // Add the restored wallet fixture
    WalletFixtures.createStandardWalletFixture();

    // Continue with the set up
    setUpAfterArrange(true, hardwareWalletFixtureType);

  }

  /**
   * <p>Handles the ongoing process of application set up</p>
   *
   * @throws Exception If something goes wrong
   */
  private void setUpAfterArrange(
    boolean licenceAccepted,
    HardwareWalletFixtureType hardwareWalletFixtureType
  ) throws Exception {

    log.debug("Setup after arrange with hardware wallet environment: {}", hardwareWalletFixtureType);

    HardwareWalletService hardwareWalletService;
    AbstractTrezorHardwareWalletClient mockClient;
    switch (hardwareWalletFixtureType) {

      case NONE:
        // Do nothing
        break;
      case TREZOR_WIPED:
        // Arrange the hardware client responses
        mockClient = TrezorHardwareWalletClientFixtures.newClient_Wiped();

        // Setup the mock hardware wallet service
        hardwareWalletService = new HardwareWalletService(mockClient);
        CoreServices.setHardwareWalletService(hardwareWalletService);
        break;
      case TREZOR_INITIALISED:
        // Arrange the hardware client responses
        mockClient = TrezorHardwareWalletClientFixtures.newClient_Initialised();

        // Setup the mock hardware wallet service
        hardwareWalletService = new HardwareWalletService(mockClient);
        CoreServices.setHardwareWalletService(hardwareWalletService);
        break;
    }

    log.debug("Reset locale to en_US");

    // Always reset back to en_US
    Locale.setDefault(Locale.US);

    log.debug("Create standard configuration (no exchange)");

    // Always start without an exchange
    Configuration configuration = Configurations.newDefaultConfiguration();
    configuration.getBitcoin().setCurrentExchange(ExchangeKey.NONE.name());
    configuration.setLicenceAccepted(licenceAccepted);

    // Persist the new configuration ready for reading later
    try (FileOutputStream fos = new FileOutputStream(InstallationManager.getConfigurationFile())) {
      Yaml.writeYaml(fos, configuration);
    } catch (IOException e) {
      ExceptionHandler.handleThrowable(e);
    }

    log.info("Starting MultiBit HD...");

    // Start MultiBit HD within FEST (mimic main method but split sequence)
    testObject = new MultiBitHD();
    testObject.start(null);

    MainView frame = GuiActionRunner.execute(
      new GuiQuery<MainView>() {
        protected MainView executeInEDT() {

          InstallationManager.getOrCreateApplicationDataDirectory();

          log.info("FEST initialising UI...");
          return testObject.initialiseUIViews();
        }
      });

    log.info("Creating FEST frame fixture");

    window = new FrameFixture(frame);

    // Show the frame to test at 100,100 (unavoidable)
    window.show();

    log.info("FEST setup complete");

  }

}
