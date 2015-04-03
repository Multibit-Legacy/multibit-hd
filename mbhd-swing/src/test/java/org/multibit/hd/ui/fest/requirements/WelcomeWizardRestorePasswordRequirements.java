package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.WalletFixtures;
import org.multibit.hd.ui.fest.use_cases.credentials.RestoreButtonUseCase;
import org.multibit.hd.ui.fest.use_cases.restore_password.RestorePasswordEnterSeedPhraseUseCase;
import org.multibit.hd.ui.fest.use_cases.restore_password.RestorePasswordReportUseCase;
import org.multibit.hd.ui.fest.use_cases.environment.CloseDebugEnvironmentPopoverUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectRestorePasswordUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Restore credentials starting from the unlock screen</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class WelcomeWizardRestorePasswordRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    new CloseDebugEnvironmentPopoverUseCase(window).execute(parameters);

    // Start the restore process
    new RestoreButtonUseCase(window).execute(parameters);

    // Select the restore option
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

    // Enter a suitable seed phrase
    new RestorePasswordEnterSeedPhraseUseCase(window).execute(parameters);

    // Verify the report
    new RestorePasswordReportUseCase(window).execute(parameters);

  }
}
