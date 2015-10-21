package org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ShowToolsScreenUseCase extends AbstractFestUseCase {

  public ShowToolsScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(TOOLS_ROW);

    // Expect the Tools screen to show

    // Row 1
    window
      .button(MessageKey.SHOW_SIGN_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_VERIFY_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    // Row 2
    window
      .button(MessageKey.SHOW_VERIFY_NETWORK_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_ABOUT_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_HARDWARE_TOOLS_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
