package org.multibit.hd.ui.fest.use_cases.standard.environment;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "environment unsupported configuration" popover can be closed</li>
 * </ul>
 *
 * @since 0.0.8
 */
public class CloseUnsupportedConfigurationEnvironmentPopoverUseCase extends AbstractFestUseCase {

  public CloseUnsupportedConfigurationEnvironmentPopoverUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Expect "unsupported configuration" popover to be showing
    window
      .panel(CoreMessageKey.UNSUPPORTED_CONFIGURATION_PASSPHRASE.getKey())
      .requireVisible();

    // Dismiss
    window
      .button("environment_alert." + MessageKey.CLOSE.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Allow time for the screen to close
    pauseForComponentReset();
  }

}
