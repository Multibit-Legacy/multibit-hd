package org.multibit.hd.ui.fest.use_cases.welcome_select;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "select wallet" panel view</li>
 * <li>Proceed with "create Trezor wallet" selection</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class WelcomeSelectCreateTrezorWalletUseCase extends AbstractFestUseCase {

  public WelcomeSelectCreateTrezorWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Allow time for events to propagate
    pauseForHardwareEvent();

    // Verify that the title appears
    assertLabelText(MessageKey.SELECT_WALLET_TITLE);

    window
      .radioButton(MessageKey.TREZOR_CREATE_WALLET.getKey())
      .requireSelected()
      .requireEnabled()
      .requireVisible();

    window
      .radioButton(MessageKey.USE_EXISTING_WALLET.getKey())
      .requireNotSelected()
      .requireVisible();

    window
      .radioButton(MessageKey.RESTORE_WALLET.getKey())
      .requireNotSelected()
      .requireVisible();

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

}
