package org.multibit.hd.ui.fest;

import com.google.common.collect.Maps;
import org.junit.Ignore;
import org.multibit.hd.ui.fest.use_cases.welcome.*;

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
@Ignore
public class WelcomeWizardCreateWalletTest extends AbstractMultiBitHDFestTest {

  @Override
  public void executeUseCases() {

    Map<String,Object> parameters = Maps.newHashMap();

    new WelcomeSelectLanguageUseCase(window).execute(parameters);
    new WelcomeSelectWalletUseCase(window).execute(parameters);
    new CreateWalletSelectBackupLocationWalletUseCase(window).execute(parameters);
    new CreateWalletSeedPhraseUseCase(window).execute(parameters);
    new CreateWalletConfirmSeedPhraseUseCase(window).execute(parameters);

  }
}
