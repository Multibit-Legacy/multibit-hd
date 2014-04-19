package org.multibit.hd.ui.fest.use_cases.sidebar_screens;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "send/request" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendRequestScreenUseCase extends AbstractFestUseCase {

  public SendRequestScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(0);

    // Expect the Send/Request screen to show
    window
      .button(MessageKey.SEND.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.REQUEST.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
