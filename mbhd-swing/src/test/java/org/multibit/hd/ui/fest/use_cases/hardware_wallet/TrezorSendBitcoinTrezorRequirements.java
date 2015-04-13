package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.sidebar.send_request.ShowSendRequestScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.send_request.send.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "send" screen to verify its wizards show correctly for Trezor devices with simple transaction</li>
 * </ul>
 *
 * <p>Requires a Trezor wallet to be unlocked</p>
 *
 * @since 0.0.8
 */
public class TrezorSendBitcoinTrezorRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Select the send/request screen
    new ShowSendRequestScreenUseCase(window).execute(parameters);

    // Select Send and fill in the amount panel then click Next
    new SendEnterAmountUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    new SendNoFundsReportScreenUseCase(window);
  }
}
