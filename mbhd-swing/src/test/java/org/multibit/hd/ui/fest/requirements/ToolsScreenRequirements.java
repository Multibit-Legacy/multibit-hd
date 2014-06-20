package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.tools.ShowToolsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.tools.empty_wallet.ShowThenCancelEmptyWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.tools.repair_wallet.ShowThenCancelRepairWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.tools.sign_message.ShowThenFinishSignMessageUseCase;
import org.multibit.hd.ui.fest.use_cases.tools.verify_message.ShowThenFinishVerifyMessageUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "tools" screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ToolsScreenRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Show the tools screen
    new ShowToolsScreenUseCase(window).execute(parameters);

    // Show then cancel the repair wizard
    // Verifying the repair will take too long
    new ShowThenCancelRepairWalletUseCase(window).execute(parameters);

    // Show the verify message wizard
    new ShowThenFinishVerifyMessageUseCase(window).execute(parameters);

    // Show the sign message wizard
    new ShowThenFinishSignMessageUseCase(window).execute(parameters);

    // Show then cancel the "empty wallet" wizard
    new ShowThenCancelEmptyWalletUseCase(window).execute(parameters);

    // Show then cancel the "about" wizard
    // TODO Reinstate this
    //new ShowThenCancelAboutUseCase(window).execute(parameters);

  }
}
