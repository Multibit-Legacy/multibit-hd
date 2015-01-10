package org.multibit.hd.ui.fest.requirements;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.credentials.QuickUnlockWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.security.CloseDebugSecurityPopoverUseCase;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Unlock the empty wallet fixture</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class QuickUnlockEmptyWalletFixtureRequirements {

  public static void verifyUsing(FrameFixture window) {

    new CloseDebugSecurityPopoverUseCase(window).execute(null);

    new QuickUnlockWalletUseCase(window).execute(null);

    new UnlockReportUseCase(window).execute(null);
  }
}
