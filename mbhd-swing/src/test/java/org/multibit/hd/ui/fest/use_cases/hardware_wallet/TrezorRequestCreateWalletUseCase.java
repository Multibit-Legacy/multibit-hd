package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Trezor "press confirm wipe device" screen</li>
 * </ul>
 *
 * <p>Require the panel view to be showing (event triggered earlier)</p>
 *
 * @since 0.0.5
 */
public class TrezorRequestCreateWalletUseCase extends AbstractFestUseCase {

  public TrezorRequestCreateWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Check that the request panel view is showing
    window
    .label(MessageKey.CREATE_TREZOR_WALLET_REQUEST_CREATE_WALLET_TITLE.getKey())
      .requireVisible();

  }
}
