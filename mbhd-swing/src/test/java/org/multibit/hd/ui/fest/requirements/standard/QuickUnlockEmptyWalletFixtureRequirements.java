package org.multibit.hd.ui.fest.requirements.standard;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.QuickUnlockWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.environment.CloseDebugEnvironmentPopoverUseCase;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Unlock the empty wallet fixture</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class QuickUnlockEmptyWalletFixtureRequirements {

  public static void verifyUsing(FrameFixture window) {

    new CloseDebugEnvironmentPopoverUseCase(window).execute(null);

    new QuickUnlockWalletUseCase(window).execute(null);

    new UnlockReportUseCase(window).execute(null);
  }
}
