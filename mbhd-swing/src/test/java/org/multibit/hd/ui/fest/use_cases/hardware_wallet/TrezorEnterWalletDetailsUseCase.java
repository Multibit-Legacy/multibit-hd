package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Trezor "enter wallet details" panel view</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class TrezorEnterWalletDetailsUseCase extends AbstractFestUseCase {

  public TrezorEnterWalletDetailsUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Check that the Trezor enter wallet details screen is showing
    window
      .label(MessageKey.CREATE_TREZOR_WALLET_ENTER_DETAILS_TITLE.getKey())
      .requireVisible();

    window
      .label(MessageKey.ENTER_TREZOR_LABEL.getKey())
      .requireVisible();

    window
      .label(MessageKey.SEED_SIZE.getKey())
      .requireVisible();

    window
      .textBox(MessageKey.ENTER_TREZOR_LABEL.getKey())
      .requireVisible();

    window
      .comboBox(MessageKey.SEED_SIZE.getKey())
      .requireVisible();

    // Check the 'Next' button is present and click it
    window
      .button(MessageKey.NEXT.getKey())
      .requireEnabled(Timeout.timeout(3, TimeUnit.SECONDS))
      .click();

  }
}
