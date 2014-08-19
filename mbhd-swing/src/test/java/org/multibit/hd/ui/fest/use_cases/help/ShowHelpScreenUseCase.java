package org.multibit.hd.ui.fest.use_cases.help;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "history" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ShowHelpScreenUseCase extends AbstractFestUseCase {

  public ShowHelpScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(HELP_ROW);

    // Expect the Help screen to show
    window
      .button(MessageKey.BACK.getKey())
      .requireVisible()
      .requireDisabled();

    window
      .button(MessageKey.FORWARD.getKey())
      .requireVisible()
      .requireDisabled();

    window
      .button(MessageKey.VIEW_IN_EXTERNAL_BROWSER.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .scrollPane(MessageKey.HELP.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
