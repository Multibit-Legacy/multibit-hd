package org.multibit.hd.ui.fest.use_cases.password;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Click "restore" button</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RestoreButtonUseCase extends AbstractFestUseCase {

  public RestoreButtonUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Expect the restore button
    window
      .button(MessageKey.RESTORE.getKey())
      .click();

    // Allow time for the wizard hand over to take place
    pauseForViewReset();

  }

}
