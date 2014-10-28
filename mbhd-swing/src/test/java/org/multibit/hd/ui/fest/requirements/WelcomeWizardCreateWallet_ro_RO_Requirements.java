package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.create_wallet.*;
import org.multibit.hd.ui.fest.use_cases.security.CloseDebugSecurityPopoverUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.AcceptLicenceUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectCreateWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectLanguage_ro_RO_UseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Create wallet using welcome wizard using ro_RO as the base language</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class WelcomeWizardCreateWallet_ro_RO_Requirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String,Object> parameters = Maps.newHashMap();

    new CloseDebugSecurityPopoverUseCase(window).execute(parameters);

    new AcceptLicenceUseCase(window).execute(parameters);

    // Use the ro_RO language
    new WelcomeSelectLanguage_ro_RO_UseCase(window).execute(parameters);

    new WelcomeSelectCreateWalletUseCase(window).execute(parameters);

    new CreateWalletPreparationUseCase(window).execute(parameters);

    new CreateWalletSelectBackupLocationWalletUseCase(window).execute(parameters);

    new CreateWalletSeedPhraseUseCase(window).execute(parameters);
    new CreateWalletConfirmSeedPhraseUseCase(window).execute(parameters);

    new CreateWalletCreatePasswordUseCase(window).execute(parameters);

    new CreateWalletReportUseCase(window).execute(parameters);

  }
}
