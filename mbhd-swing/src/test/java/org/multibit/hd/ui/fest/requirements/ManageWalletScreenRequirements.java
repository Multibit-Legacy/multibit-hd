package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.ShowManageWalletScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.change_password.VerifyChangePasswordUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.edit_wallet.ShowThenCancelEditWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.empty_wallet.ShowThenCancelEmptyWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.payment_settings.ShowThenCancelPaymentSettingsUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.payment_settings.VerifyPaymentSettingsBlockExplorerUseCase;
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

    // Exercise the basic settings by showing and cancelling
    new ShowThenCancelEditWalletUseCase(window).execute(parameters);
    new ShowThenCancelWalletDetailsUseCase(window).execute(parameters);

    if (CoreServices.getOrCreateBitcoinNetworkService().isStartedOk()) {
      // Show then cancel the "empty wallet" wizard
      new ShowThenCancelEmptyWalletUseCase(window).execute(parameters);

    }

    // Show then cancel the "repair wallet" wizard
    // Verifying the repair will take too long
    new ShowThenCancelRepairWalletUseCase(window).execute(parameters);
    new ShowThenCancelPaymentSettingsUseCase(window).execute(parameters);

    // Payment settings
    new VerifyPaymentSettingsBlockExplorerUseCase(window).execute(parameters);

    // Change password
    new VerifyChangePasswordUseCase(window).execute(parameters);

  }

}
