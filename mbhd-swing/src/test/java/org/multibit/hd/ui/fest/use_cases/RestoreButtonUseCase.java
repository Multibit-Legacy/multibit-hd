package org.multibit.hd.ui.fest.use_cases;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.languages.MessageKey;

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
  public void execute() {

    window.button(MessageKey.RESTORE.getKey()).click();

  }

}
