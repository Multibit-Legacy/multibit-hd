package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.sidebar.send_request.ShowSendRequestScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.send_request.request.ShowRequestEnterAmountBitcoinUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.send_request.request.ShowRequestEnterAmountWithQRBitcoinUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.send_request.request.ShowRequestThenFinishUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.send_request.send.*;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "send/request" screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SendRequestScreenRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String,Object> parameters = Maps.newHashMap();

    // Select the send/request screen
    new ShowSendRequestScreenUseCase(window).execute(parameters);

    // Send
    verifySend(window, parameters);

    // Request
    verifyRequest(window, parameters);

  }

  private static void verifySend(FrameFixture window, Map<String, Object> parameters) {

    // Click Send then immediately Cancel
    new ShowSendThenCancelUseCase(window).execute(parameters);

    // Verify the recipient field then Cancel
    new SendVerifyRecipientAndCancelContactUseCase(window).execute(parameters);

    // Verify the amount fields then Cancel
    new SendVerifyAmountAndCancelContactUseCase(window).execute(parameters);

    // Click Send and fill in the amount panel
    new SendNoFundsAmountScreenUseCase(window).execute(parameters);

    // Click Next and fill in the confirm panel
    new SendNoFundsConfirmScreenUseCase(window).execute(parameters);
  }

  private static void verifyRequest(FrameFixture window, Map<String, Object> parameters) {

    // Click Request then immediately Finish
    new ShowRequestThenFinishUseCase(window).execute(parameters);

    // Click Request and enter an amount with QR code then Finish (checks for position 0)
    new ShowRequestEnterAmountWithQRBitcoinUseCase(window).execute(parameters);

    // Click Request and enter an amount then Finish (checks for position 0)
    new ShowRequestEnterAmountBitcoinUseCase(window).execute(parameters);

  }
}
