package org.multibit.hd.ui.fest.use_cases.sidebar.send_request;

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
public class ShowSendRequestScreenUseCase extends AbstractFestUseCase {

  public ShowSendRequestScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Selecting multi-wallet row 0 does not trigger detail view in FEST but works fine in app

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(SEND_REQUEST_ROW);

    // Expect the Send/Request screen to show
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      .requireVisible();

    window
      .button(MessageKey.SHOW_REQUEST_WIZARD.getKey())
      .requireVisible();

    // Change the selection away from Send/Request
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(PAYMENTS_ROW);

    // Expect the Payment screen to show (no Send/Request showing)
    window
      .button(newNotShowingJButtonFixture(MessageKey.SHOW_SEND_WIZARD.getKey()));

    window
      .button(newNotShowingJButtonFixture(MessageKey.SHOW_REQUEST_WIZARD.getKey()));

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(SEND_REQUEST_ROW);

    // Expect the Send/Request screen to show
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      .requireVisible();

    window
      .button(MessageKey.SHOW_REQUEST_WIZARD.getKey())
      .requireVisible();

  }

}
