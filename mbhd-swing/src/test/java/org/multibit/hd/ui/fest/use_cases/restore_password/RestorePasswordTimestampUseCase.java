package org.multibit.hd.ui.fest.use_cases.restore_password;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.awt.event.KeyEvent;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "restore wallet timestamp and password" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RestorePasswordTimestampUseCase extends AbstractFestUseCase {

  public RestorePasswordTimestampUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Verify that the title appears
    assertLabelText(MessageKey.RESTORE_WALLET_TIMESTAMP_TITLE);

    // Verify that the notes are present
    assertLabelText(MessageKey.RESTORE_TIMESTAMP_NOTE_1);

    String timestamp = (String) parameters.get(MessageKey.TIMESTAMP.getKey());
    window
      .textBox(MessageKey.TIMESTAMP.getKey())
      .setText(timestamp)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .label(MessageKey.ENTER_PASSWORD.getKey())
      .requireVisible();

    window
      .button(MessageKey.SHOW.getKey())
      .requireVisible();

    // Matching password
    window
      .textBox(MessageKey.ENTER_PASSWORD.getKey())
      .setText("abc123")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

}
