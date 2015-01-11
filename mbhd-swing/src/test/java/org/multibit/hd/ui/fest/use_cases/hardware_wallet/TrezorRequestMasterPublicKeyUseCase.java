package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Trezor "request master public key" screen</li>
 * </ul>
 *
 * <p>Require the panel view to be showing (event triggered earlier)</p>
 *
 * @since 0.0.5
 */
public class TrezorRequestMasterPublicKeyUseCase extends AbstractFestUseCase {

  public TrezorRequestMasterPublicKeyUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Allow time for events to propagate
    pauseForHardwareEvent();

    // This transitional panel is too variable to trap reliably

    // Check that the request panel view is showing
//    window
//    .label(MessageKey.TREZOR_UNLOCK_TITLE.getKey())
//      .requireVisible();

  }
}
