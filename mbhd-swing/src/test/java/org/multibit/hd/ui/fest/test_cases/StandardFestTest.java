package org.multibit.hd.ui.fest.test_cases;

import com.google.common.base.Optional;
import org.junit.Ignore;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.requirements.standard.*;

/**
 * <p>Standard wallet functional tests</p>
 *
 * @since 0.0.1
 */
public class StandardFestTest extends AbstractFestTest {

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Create a wallet</li>
   * </ul>
   */
  @Test
  public void verifyCreateWallet_en_US_ColdStart() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh(Optional.<HardwareWalletFixture>absent());

    // Create a wallet through the welcome wizard
    WelcomeWizardCreateWallet_en_US_Requirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Create a wallet</li>
   * </ul>
   */
  @Test
  public void verifyCreateWallet_ro_RO_ColdStart() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh(Optional.<HardwareWalletFixture>absent());

    // Create a wallet through the welcome wizard
    WelcomeWizardCreateWallet_ro_RO_Requirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Restore a wallet using the seed phrase and date stamp</li>
   * </ul>
   */
  @Test
  public void verifyRestoreWallet_en_US_ColdStart() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh(Optional.<HardwareWalletFixture>absent());

    // Restore a wallet through the welcome wizard
    WelcomeWizardRestoreWallet_en_US_Requirements.verifyUsing(window);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Restore a wallet using the seed phrase and date stamp</li>
   * </ul>
   */
  @Test
  public void verifyRestoreBeta7Wallet_en_US_ColdStart() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh(Optional.<HardwareWalletFixture>absent());

    // Set the configuration option to show 'Enable restore Beta7 wallets'
    Configurations.currentConfiguration.setShowRestoreBeta7Wallets(true);

    // Restore a wallet through the welcome wizard
    WelcomeWizardRestoreBeta7Wallet_en_US_Requirements.verifyUsing(window);

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

  }

  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with fresh application directory</li>
   * <li>Restore a wallet using the seed phrase and no date stamp</li>
   * </ul>
   */
  @Test
  public void verifyRestoreWalletNoDateStamp_en_US_ColdStart() throws Exception {

    // Start with a completely empty random application directory
    arrangeFresh(Optional.<HardwareWalletFixture>absent());

    // Restore a wallet through the welcome wizard
    WelcomeWizardRestoreWalletNoDateStamp_en_US_Requirements.verifyUsing(window);

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

    // Start with the standard hardware wallet fixture
    arrangeStandard(Optional.<HardwareWalletFixture>absent());

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

    // Start with the standard hardware wallet fixture
    arrangeStandard(Optional.<HardwareWalletFixture>absent());

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

    // Start with the empty hardware wallet fixture
    arrangeEmpty(Optional.<HardwareWalletFixture>absent());

    // Unlock the wallet
    SlowUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Explore the sidebar screens
    SidebarTreeScreensRequirements.verifyUsing(window);

  }


  /**
   * <p>Verify the following:</p>
   * <ul>
   * <li>Start with standard wallet fixture</li>
   * <li>Unlock wallet</li>
   * <li>Exercise the Payments screen</li>
   * </ul>
   */
  @Test
  public void verifyPaymentsScreen() throws Exception {

    // Start with the standard hardware wallet fixture
    arrangeStandard(Optional.<HardwareWalletFixture>absent());

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

    // Start with the empty hardware wallet fixture
    arrangeEmpty(Optional.<HardwareWalletFixture>absent());

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
   * <li>Exercise the Help screen</li>
   * </ul>
   */
  @Test
  public void verifyHelpScreen() throws Exception {

    // Start with the empty hardware wallet fixture
    arrangeEmpty(Optional.<HardwareWalletFixture>absent());

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Verify
    HelpScreenRequirements.verifyUsing(window);

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

    // Start with the empty hardware wallet fixture
    arrangeEmpty(Optional.<HardwareWalletFixture>absent());

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
  @Ignore
  public void verifyManageWalletScreen() throws Exception {

    // Start with the empty hardware wallet fixture
    arrangeEmpty(Optional.<HardwareWalletFixture>absent());

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

    // Start with the empty hardware wallet fixture
    arrangeEmpty(Optional.<HardwareWalletFixture>absent());

    // Unlock the wallet
    QuickUnlockEmptyWalletFixtureRequirements.verifyUsing(window);

    // Verify
    ToolsScreenRequirements.verifyUsing(window);

  }

}
