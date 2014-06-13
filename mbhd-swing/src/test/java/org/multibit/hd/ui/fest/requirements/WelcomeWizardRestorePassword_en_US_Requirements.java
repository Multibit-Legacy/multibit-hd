package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.WalletFixtures;
import org.multibit.hd.ui.fest.use_cases.restore_wallet.RestoreWalletEnterSeedPhraseUseCase;
import org.multibit.hd.ui.fest.use_cases.restore_wallet.RestoreWalletReportUseCase;
import org.multibit.hd.ui.fest.use_cases.restore_wallet.RestoreWalletSelectBackupLocationWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.restore_wallet.RestoreWalletTimestampUseCase;
import org.multibit.hd.ui.fest.use_cases.security.CloseDebugSecurityPopoverUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectLanguage_en_US_UseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectRestorePasswordUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Restore password using welcome wizard using en_US as the base language</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WelcomeWizardRestorePassword_en_US_Requirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    new CloseDebugSecurityPopoverUseCase(window).execute(parameters);

    // Use the en_US language
    new WelcomeSelectLanguage_en_US_UseCase(window).execute(parameters);

    new WelcomeSelectRestorePasswordUseCase(window).execute(parameters);

    // Use the standard seed phrase so we can put in a recent timestamp
    parameters.put(
      MessageKey.SEED_PHRASE.getKey(),
      WalletFixtures.STANDARD_WALLET_SEED_PHRASE
    );
    parameters.put(
      MessageKey.TIMESTAMP.getKey(),
      WalletFixtures.STANDARD_TIMESTAMP
    );

    new RestoreWalletEnterSeedPhraseUseCase(window).execute(parameters);

    new RestoreWalletSelectBackupLocationWalletUseCase(window).execute(parameters);

    new RestoreWalletTimestampUseCase(window).execute(parameters);

    new RestoreWalletReportUseCase(window).execute(parameters);

  }
}
