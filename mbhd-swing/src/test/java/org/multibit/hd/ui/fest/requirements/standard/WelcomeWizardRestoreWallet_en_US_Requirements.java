package org.multibit.hd.ui.fest.requirements.standard;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.testing.WalletSummaryFixtures;
import org.multibit.hd.ui.fest.use_cases.standard.environment.CloseDebugEnvironmentPopoverUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.restore_wallet.RestoreWalletDateStampUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.restore_wallet.RestoreWalletEnterSeedPhraseUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.restore_wallet.RestoreWalletReportUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.restore_wallet.RestoreWalletSelectBackupLocationWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.AttachHardwareWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.WelcomeSelectLanguage_en_US_UseCase;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.WelcomeSelectRestoreWalletUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Restore wallet using welcome wizard using en_US as the base language</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class WelcomeWizardRestoreWallet_en_US_Requirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    new CloseDebugEnvironmentPopoverUseCase(window).execute(parameters);

    // Use the en_US language
    new WelcomeSelectLanguage_en_US_UseCase(window).execute(parameters);

    new AttachHardwareWalletUseCase(window).execute(parameters);

    new WelcomeSelectRestoreWalletUseCase(window).execute(parameters);

    // Use the empty seed phrase so we can put in a recent timestamp
    parameters.put(
      MessageKey.SEED_PHRASE.getKey(),
      WalletSummaryFixtures.EMPTY_WALLET_SEED_PHRASE
    );
    parameters.put(
      MessageKey.TIMESTAMP.getKey(),
      Dates.newSeedTimestamp()
    );

    new RestoreWalletEnterSeedPhraseUseCase(window).execute(parameters);

    new RestoreWalletSelectBackupLocationWalletUseCase(window).execute(parameters);

    new RestoreWalletDateStampUseCase(window).execute(parameters);

    new RestoreWalletReportUseCase(window).execute(parameters);

  }
}
