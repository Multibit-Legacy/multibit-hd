package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.credentials.RestoreButtonTrezorUseCase;
import org.multibit.hd.ui.fest.use_cases.credentials.UnlockReportUseCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of
 * unlocking a Trezor wallet under warm start followed by a restore</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class RestoreTrezorRestoreWithLocalBackupRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Verify wallet unlocked and start the restore process
    new RestoreButtonTrezorUseCase(window).execute(parameters);

    // Verify wallet loads
    new UnlockReportUseCase(window).execute(null);

    Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
  }
}
