package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.ShowManageWalletScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.change_password.VerifyChangePasswordUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.edit_wallet.ShowThenCancelEditWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.empty_wallet.ShowThenCancelEmptyWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.history.*;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.repair_wallet.ShowThenCancelRepairWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.wallet_details.ShowThenCancelWalletDetailsUseCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "manage wallets" screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ManageWalletScreenRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Show the manage wallets screen
    new ShowManageWalletScreenUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Show then cancel the "edit wallet" wizard
    new ShowThenCancelEditWalletUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Show then cancel the "wallet details" wizard
    new ShowThenCancelWalletDetailsUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    if (CoreServices.getOrCreateBitcoinNetworkService().isStartedOk()) {
      // Show then cancel the "empty wallet" wizard
      new ShowThenCancelEmptyWalletUseCase(window).execute(parameters);

      Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
    }

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Show then cancel the "repair wallet" wizard
    // Verifying the repair will take too long
    new ShowThenCancelRepairWalletUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Verify change password
    new VerifyChangePasswordUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Verify the "history" screen change
    verifyHistoryScreen(window, parameters);

  }

  /**
   * The history screen is a special case of a screen initiated from a button
   *
   * @param window     The frame fixture window
   * @param parameters Any parameters
   */
  private static void verifyHistoryScreen(FrameFixture window, Map<String, Object> parameters) {

    new ShowHistoryScreenUseCase(window).execute(parameters);

    // Click Edit and update credentials verified
    new EditPasswordEntryUseCase(window).execute(parameters);

    // Click Edit and fill in some extra info on credentials but then Cancel
    new EditThenCancelPasswordEntryUseCase(window).execute(parameters);

    // Select wallet created and credentials then use multi-edit
    new EditOpenedAndPasswordEntryUseCase(window).execute(parameters);

    // Search for the first entry
    new SearchHistoryUseCase(window).execute(parameters);



  }

}
