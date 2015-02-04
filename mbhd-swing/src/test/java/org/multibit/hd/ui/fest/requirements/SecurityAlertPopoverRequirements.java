package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.security.CloseDebugSecurityPopoverUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Create wallet using welcome wizard</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SecurityAlertPopoverRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    new CloseDebugSecurityPopoverUseCase(window).execute(parameters);

  }
}
