package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.send_request.*;

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

    // Click Send then immediately Cancel
    new ShowSendThenCancelSendUseCase(window).execute(parameters);

    // Verify the recipient field then Cancel
    new VerifyRecipientAndCancelContactUseCase(window).execute(parameters);

    // Verify the amount fields then Cancel
    new VerifyAmountAndCancelContactUseCase(window).execute(parameters);

    // Click Send and fill in the amount panel
    new SendNoFundsAmountScreenUseCase(window).execute(parameters);

    // Click Next and fill in the confirm panel
    new SendNoFundsConfirmScreenUseCase(window).execute(parameters);

  }
}
