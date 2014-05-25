package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.create_wallet.*;
import org.multibit.hd.ui.fest.use_cases.security.CloseDebugSecurityPopoverUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Create wallet using welcome wizard</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WelcomeWizardCreateWalletRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String,Object> parameters = Maps.newHashMap();

    new CloseDebugSecurityPopoverUseCase(window).execute(parameters);

    new WelcomeSelectLanguageUseCase(window).execute(parameters);

    new WelcomeSelectWalletUseCase(window).execute(parameters);

    new CreateWalletSelectBackupLocationWalletUseCase(window).execute(parameters);

    new CreateWalletSeedPhraseUseCase(window).execute(parameters);
    new CreateWalletConfirmSeedPhraseUseCase(window).execute(parameters);

    new CreateWalletCreatePasswordUseCase(window).execute(parameters);

    new CreateWalletReportUseCase(window).execute(parameters);

  }
}
