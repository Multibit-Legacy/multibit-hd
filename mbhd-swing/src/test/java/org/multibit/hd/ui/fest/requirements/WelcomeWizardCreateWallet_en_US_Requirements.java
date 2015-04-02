package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.create_wallet.*;
import org.multibit.hd.ui.fest.use_cases.credentials.QuickUnlockWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.environment.CloseDebugEnvironmentPopoverUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.ShowManageWalletScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet.edit_wallet.ShowThenCancelEditWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.AcceptLicenceUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectCreateWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectLanguage_en_US_UseCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Create wallet using welcome wizard using en_US as the base language</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class WelcomeWizardCreateWallet_en_US_Requirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    new CloseDebugEnvironmentPopoverUseCase(window).execute(parameters);

    new AcceptLicenceUseCase(window).execute(parameters);

    // Use the en_US language
    new WelcomeSelectLanguage_en_US_UseCase(window).execute(parameters);

    new WelcomeSelectCreateWalletUseCase(window).execute(parameters);

    new CreateWalletPreparationUseCase(window).execute(parameters);

    new CreateWalletSelectBackupLocationWalletUseCase(window).execute(parameters);

    new CreateWalletSeedPhraseUseCase(window).execute(parameters);
    new CreateWalletConfirmSeedPhraseUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    new CreateWalletCreatePasswordUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    new CreateWalletReportUseCase(window).execute(parameters);

    // Hand over to the credentials wizard

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    new QuickUnlockWalletUseCase(window).execute(parameters);

    new UnlockReportUseCase(window).execute(parameters);

    // Show the manage wallets screen
    new ShowManageWalletScreenUseCase(window).execute(parameters);

    // Test that the cloud backup was successful
    new ShowThenCancelEditWalletUseCase(window).execute(parameters);

  }
}
