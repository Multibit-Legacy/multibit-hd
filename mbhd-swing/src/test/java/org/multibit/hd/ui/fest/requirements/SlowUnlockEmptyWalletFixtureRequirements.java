package org.multibit.hd.ui.fest.requirements;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.credentials.SlowUnlockWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.environment.CloseDebugEnvironmentPopoverUseCase;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Unlock the empty wallet fixture</li>
 * <li>Include a credentials failure and exercise buttons</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SlowUnlockEmptyWalletFixtureRequirements {

  public static void verifyUsing(FrameFixture window) {

    new CloseDebugEnvironmentPopoverUseCase(window).execute(null);

    new SlowUnlockWalletUseCase(window).execute(null);

    new UnlockReportUseCase(window).execute(null);

  }
}
