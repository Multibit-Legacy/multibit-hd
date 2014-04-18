package org.multibit.hd.ui.fest;

import org.junit.Ignore;
import org.multibit.hd.ui.fest.use_cases.welcome.CreateWalletSeedPhraseUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome.CreateWalletSelectBackupLocationWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome.WelcomeSelectLanguageUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome.WelcomeSelectWalletUseCase;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Create wallet using welcome wizard</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
@Ignore
public class WelcomeWizardCreateWalletTest extends AbstractMultiBitHDFestTest {

  @Override
  public void executeUseCases() {

    new WelcomeSelectLanguageUseCase(window).execute();
    new WelcomeSelectWalletUseCase(window).execute();
    new CreateWalletSelectBackupLocationWalletUseCase(window).execute();
    new CreateWalletSeedPhraseUseCase(window).execute();

  }
}
