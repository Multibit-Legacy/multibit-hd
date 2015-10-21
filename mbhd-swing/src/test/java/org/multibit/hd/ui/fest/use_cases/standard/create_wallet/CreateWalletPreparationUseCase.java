package org.multibit.hd.ui.fest.use_cases.standard.create_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Pause;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.swing.timing.Timeout.timeout;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "create wallet preparation" panel view</li>
 * </ul>
 *
 * @since 0.0.5
 *
 */
public class CreateWalletPreparationUseCase extends AbstractFestUseCase {

  public CreateWalletPreparationUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Verify that the title appears
    assertLabelText(MessageKey.CREATE_WALLET_PREPARATION_TITLE);

    // Allow time to render the screen
    Pause.pause(3, TimeUnit.SECONDS);

    window
      .label(MessageKey.PREPARATION_NOTE_1.getKey())
      .requireVisible();

    window
      .label(MessageKey.PREPARATION_NOTE_2.getKey())
      .requireVisible();

    window
      .label(MessageKey.PREPARATION_NOTE_3.getKey())
      .requireVisible();

    window
      .label(MessageKey.PREPARATION_NOTE_4.getKey())
      .requireVisible();

    window
      .label(MessageKey.PREPARATION_NOTE_5.getKey())
      .requireVisible();

    window
      .label(MessageKey.PREPARATION_NOTE_6.getKey())
      .requireVisible();

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .requireEnabled(timeout(2, TimeUnit.SECONDS))
      .click();

  }

}
