package org.multibit.hd.ui.fest.use_cases.sidebar.tools.change_password;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen change credentials wizard shows</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class ShowThenCancelChangePasswordUseCase extends AbstractFestUseCase {

  public ShowThenCancelChangePasswordUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Change credentials
    window
      .button(MessageKey.SHOW_CHANGE_PASSWORD_WIZARD.getKey())
      .click();

    // Verify the "change credentials" wizard appears
    assertLabelText(MessageKey.CHANGE_PASSWORD_TITLE);

    // Verify buttons are present
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled();

   // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();
  }
}
