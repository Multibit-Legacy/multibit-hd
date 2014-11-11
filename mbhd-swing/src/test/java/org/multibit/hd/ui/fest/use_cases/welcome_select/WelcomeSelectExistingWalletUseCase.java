package org.multibit.hd.ui.fest.use_cases.welcome_select;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "select credentials" panel view</li>
 * <li>Proceed with "use existing wallet" selection</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WelcomeSelectExistingWalletUseCase extends AbstractFestUseCase {

  public WelcomeSelectExistingWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Verify that the title appears
    assertLabelText(MessageKey.SELECT_WALLET_TITLE);

    window
      .radioButton(MessageKey.CREATE_WALLET.getKey())
      .requireSelected()
      .requireEnabled()
      .requireVisible();

    window
      .radioButton(MessageKey.RESTORE_PASSWORD.getKey())
      .requireNotSelected()
      .requireEnabled()
      .requireVisible();

    window
      .radioButton(MessageKey.RESTORE_WALLET.getKey())
      .requireNotSelected()
      .requireEnabled()
      .requireVisible();

    window
      .radioButton(MessageKey.USE_HARDWARE_WALLET.getKey())
      .requireNotSelected()
      .requireDisabled()
      .requireVisible();

    window
      .radioButton(MessageKey.USE_EXISTING_WALLET.getKey())
      .requireNotSelected()
      .requireVisible();

    // Select "use existing wallet"
    window
      .radioButton(MessageKey.USE_EXISTING_WALLET.getKey())
      .click();

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .click();

    // Expect a handover to the unlock screen
    pauseForViewReset();

  }

}
