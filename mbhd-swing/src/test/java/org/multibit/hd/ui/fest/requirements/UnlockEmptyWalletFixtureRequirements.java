package org.multibit.hd.ui.fest.requirements;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.password.UnlockWalletUseCase;
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
public class UnlockEmptyWalletFixtureRequirements {

  public static void verifyUsing(FrameFixture window) {

    new CloseDebugSecurityPopoverUseCase(window).execute(null);

    new UnlockWalletUseCase(window).execute(null);


  }
}
