package org.multibit.hd.ui.fest.test_cases;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.Uninterruptibles;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.testing.FestSwingTestCaseTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.Yaml;
import org.multibit.hd.core.dto.WalletMode;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletClient;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.testing.WalletSummaryFixtures;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.views.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.fest.swing.timing.Pause.pause;

/**
 * <p>Test suite for Swing UI functional tests:</p>
 * <ul>
 * <li>Handles startup sequence</li>
 * <li>Handles soft shutdown</li>
 * </ul>
 *
 * @since 0.0.1
 */
public abstract class AbstractFestTest extends FestSwingTestCaseTemplate {

  private static final Logger log = LoggerFactory.getLogger(AbstractFestTest.class);

  protected FrameFixture window;

  protected MultiBitHD testObject;

  @BeforeClass
  public static void setUpOnce() throws Exception {

    FailOnThreadViolationRepaintManager.install();

  }

  /**
   * Create a local wallet backup
   *
   * @param walletSummary The wallet summary
   *
   * @throws IOException If something goes wrong
   */
  public static void createLocalBackup(WalletSummary walletSummary) throws IOException {
    // Create a local backup of the empty wallet so that there is one to load
    BackupManager.INSTANCE.createLocalBackup(walletSummary.getWalletId(), WalletSummaryFixtures.ABANDON_TREZOR_PASSWORD);
  }

  @SuppressFBWarnings({"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD"})
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
   * <p>Cold start</p>
   * <p>Starts MultiBit HD with an empty application directory</p>
   *
   * @param hardwareWalletFixture The hardware wallet fixture
   *
   * @throws Exception If something goes wrong
   */
  protected void arrangeFresh(Optional<HardwareWalletFixture> hardwareWalletFixture) throws Exception {

    log.info("Arranging fresh environment...");

    // Continue with the set up
    setUpAfterArrange(false, hardwareWalletFixture, WalletMode.TREZOR);

  }

  /**
   * <p>Warm start</p>
   * <p>Starts MultiBit HD with an application directory containing the empty wallet fixture and an accepted licence</p>
   *
   * @param hardwareWalletFixture The hardware wallet fixture
   *
   * @throws Exception If something goes wrong
   */
  @SuppressFBWarnings({"OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE"})
  protected WalletSummary arrangeEmpty(Optional<HardwareWalletFixture> hardwareWalletFixture) throws Exception {
    log.info("Arranging empty wallet fixture environment with hardwareWalletFixtureType: {} ...", hardwareWalletFixture);

    // Get the temporary application directory
    File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Copy the standard MBHD cacerts
    InputStream cacerts = AbstractFestTest.class.getResourceAsStream("/mbhd-cacerts");
    OutputStream target = new FileOutputStream(new File(applicationDirectory + "/" + InstallationManager.CA_CERTS_NAME));
    ByteStreams.copy(cacerts, target);

    // Initialise the backup manager
    BackupManager.INSTANCE.initialise(applicationDirectory, Optional.<File>absent());

    // Add the empty wallet fixture
    WalletSummary walletSummary;
    if (hardwareWalletFixture.isPresent()) {
      walletSummary = WalletSummaryFixtures.createEmptyTrezorHardWalletFixture();
    } else {
      walletSummary = WalletSummaryFixtures.createEmptyMBHDSoftWalletFixture();
    }

    // Continue with the set up
    setUpAfterArrange(true, hardwareWalletFixture, WalletMode.TREZOR);

    return walletSummary;
  }

  /**
   * <p>Warm start</p>
   * <p>Starts MultiBit HD with an application directory containing a standard wallet fixture containing real transactions and contacts</p>
   *
   * @param hardwareWalletFixture The hardware wallet fixture
   *
   * @throws Exception If something goes wrong
   */
  @SuppressFBWarnings({"OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE"})
  protected void arrangeStandard(Optional<HardwareWalletFixture> hardwareWalletFixture) throws Exception {

    log.info("Arranging standard wallet fixture environment...");

    // Get the temporary application directory
    File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Copy the standard MBHD cacerts
    InputStream cacerts = AbstractFestTest.class.getResourceAsStream("/mbhd-cacerts");
    OutputStream target = new FileOutputStream(new File(applicationDirectory + "/" + InstallationManager.CA_CERTS_NAME));
    ByteStreams.copy(cacerts, target);

    // Initialise the backup manager to use the temporary directory
    BackupManager.INSTANCE.initialise(applicationDirectory, Optional.<File>absent());

    // Add the restored wallet fixture
    WalletSummaryFixtures.createStandardMBHDSoftWalletFixture();

    // Continue with the set up
    setUpAfterArrange(true, hardwareWalletFixture, WalletMode.TREZOR);

  }

  /**
   * <p>Handles the ongoing process of application set up</p>
   *
   * @throws Exception If something goes wrong
   */
  protected void setUpAfterArrange(
    boolean licenceAccepted,
    Optional<HardwareWalletFixture> hardwareWalletFixture,
    WalletMode walletMode) throws Exception {

    if (hardwareWalletFixture.isPresent()) {

      log.debug("Setup after arrange with hardware wallet environment");

      // Get the hardware wallet client for the test case
      HardwareWalletClient mockClient = hardwareWalletFixture.get().getClient();

      // Inject it into the hardware wallet service
      HardwareWalletService hardwareWalletService = new HardwareWalletService(mockClient);

      // Create a suitable ordering based on the wallet mode
      List<Optional<HardwareWalletService>> hardwareWalletServices = Lists.newArrayList();
      switch (walletMode) {
        case TREZOR:
          hardwareWalletServices.add(Optional.of(hardwareWalletService));
          hardwareWalletServices.add(Optional.<HardwareWalletService>absent());
          break;
        case KEEP_KEY:
          hardwareWalletServices.add(Optional.<HardwareWalletService>absent());
          hardwareWalletServices.add(Optional.of(hardwareWalletService));
          break;
      }
      // Ensure CoreServices uses it instead of creating one
      CoreServices.setHardwareWalletServices(hardwareWalletServices);

    }

    log.debug("Reset locale to en_US");

    // Always reset back to en_US
    Locale.setDefault(Locale.US);

    log.debug("Create standard configuration (no exchange)");

    // Always start without an exchange and with cloud backups
    Configuration configuration = Configurations.newDefaultConfiguration();
    configuration.getBitcoin().setCurrentExchange(ExchangeKey.NONE.name());
    configuration.setLicenceAccepted(licenceAccepted);

    String festCloudBackupDirectory = InstallationManager.getOrCreateApplicationDataDirectory().getAbsolutePath() + "/fest-cloud-backups";
    configuration.getAppearance().setCloudBackupLocation(festCloudBackupDirectory);

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
          log.info("FEST initialising UI...");
          return testObject.initialiseUIViews();
        }
      });

    log.info("Creating FEST frame fixture");

    window = new FrameFixture(frame);

    // Show the frame to test at 100,100 (unavoidable)
    window.show();

    log.info("FEST setup complete");

    // Allow time for UI to render
    Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);

  }

}
