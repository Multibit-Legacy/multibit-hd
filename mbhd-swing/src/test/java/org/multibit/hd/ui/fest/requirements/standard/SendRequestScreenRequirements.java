package org.multibit.hd.ui.fest.requirements.standard;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.send_request.ShowSendRequestScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.send_request.request.ShowRequestEnterAmountBitcoinUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.send_request.request.ShowRequestEnterAmountWithQRBitcoinUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.send_request.request.ShowRequestThenFinishUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.send_request.send.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "send/request" screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendRequestScreenRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Select the send/request screen
    new ShowSendRequestScreenUseCase(window).execute(parameters);

    // Send
    verifySend_Simple(window, parameters);
    verifySend_PaymentProtocol(window, parameters);

    // Request
    verifyRequest(window, parameters);

  }

  private static void verifySend_Simple(FrameFixture window, Map<String, Object> parameters) {

    // Click Send then immediately Cancel
    new ShowSendThenCancelUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Verify the recipient field then Cancel
    new SendVerifyRecipientAndCancelContactUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Verify the amount fields then Cancel
    new SendVerifyAmountAndCancelContactUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Click Send and fill in the amount panel
    new SendEnterAmountUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Click Next and fill in the confirm panel
    new SendNoFundsReportScreenUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
  }

  private static void verifySend_PaymentProtocol(FrameFixture window, Map<String, Object> parameters) {

    new SendDisplayUntrustedPaymentRequestUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    new SendDisplayTrustedPaymentRequestUseCase(window).execute(parameters);

  }

  private static void verifyRequest(FrameFixture window, Map<String, Object> parameters) {

    // Click Request then immediately Finish
    new ShowRequestThenFinishUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Click Request and enter an amount with QR code then Finish (checks for position 0)
    new ShowRequestEnterAmountWithQRBitcoinUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Click Request and enter an amount then Finish (checks for position 0)
    new ShowRequestEnterAmountBitcoinUseCase(window).execute(parameters);

  }
}
