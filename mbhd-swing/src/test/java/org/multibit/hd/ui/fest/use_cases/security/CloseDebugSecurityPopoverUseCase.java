package org.multibit.hd.ui.fest.use_cases.security;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "security debugger attached" popover can be closed</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CloseDebugSecurityPopoverUseCase extends AbstractFestUseCase {

  public CloseDebugSecurityPopoverUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    if (OSUtils.isDebuggerAttached()) {

      // Expect debug popover to be showing
      window
        .panel(CoreMessageKey.DEBUGGER_ATTACHED.getKey())
        .requireVisible();

      // Dismiss
      window
        .button("security_alert." + MessageKey.CLOSE.getKey())
        .requireVisible()
        .requireEnabled()
        .click();
    }

  }

}
