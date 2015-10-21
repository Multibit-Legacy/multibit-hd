package org.multibit.hd.ui.fest.requirements.standard;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.ShowToolsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.about.ShowThenCancelAboutUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.sign_message.ShowThenFinishSignMessageUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.trezor_tools.ShowThenCancelTrezorToolsUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.verify_message.ShowThenFinishVerifyMessageUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.verify_network.ShowThenFinishVerifyNetworkUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "tools" screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ToolsScreenRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Show the tools screen
    new ShowToolsScreenUseCase(window).execute(parameters);

    // Show the "sign message" wizard
    new ShowThenFinishSignMessageUseCase(window).execute(parameters);

    // Show the "verify message" wizard
    new ShowThenFinishVerifyMessageUseCase(window).execute(parameters);

    // Show the "verify network" wizard
    new ShowThenFinishVerifyNetworkUseCase(window).execute(parameters);

    // Show then cancel the "about" wizard
    new ShowThenCancelAboutUseCase(window).execute(parameters);

    // Show the "Trezor tools" wizard
    new ShowThenCancelTrezorToolsUseCase(window).execute(parameters);


  }
}
