package org.multibit.hd.ui.fest.use_cases.settings;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "settings" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ShowSettingsScreenUseCase extends AbstractFestUseCase {

  public ShowSettingsScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(SETTINGS_ROW);

    // Expect the Settings screen to show
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_BITCOIN_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_EXCHANGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_APPLICATION_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_SOUND_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
