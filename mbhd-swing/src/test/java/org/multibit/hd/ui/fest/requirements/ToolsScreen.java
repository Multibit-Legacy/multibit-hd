package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.tools.ShowThenFinishSignMessageUseCase;
import org.multibit.hd.ui.fest.use_cases.tools.ShowThenFinishVerifyMessageUseCase;
import org.multibit.hd.ui.fest.use_cases.tools.ShowToolsScreenUseCase;

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
public class ToolsScreen {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Show the tools screen
    new ShowToolsScreenUseCase(window).execute(parameters);

    // Perform a simple edit and finish operation
    new ShowThenFinishSignMessageUseCase(window).execute(parameters);

    // Perform a simple edit and finish operation
    new ShowThenFinishVerifyMessageUseCase(window).execute(parameters);
  }
}
