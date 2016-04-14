package org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.about;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen about wizard shows</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class ShowThenCancelAboutUseCase extends AbstractFestUseCase {

  public ShowThenCancelAboutUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on "about"
    window
      .button(MessageKey.SHOW_ABOUT_WIZARD.getKey())
      .click();

    // Verify the "about" wizard appears
    assertLabelText(MessageKey.ABOUT_TITLE);

    // Verify the note appears
    assertLabelText(MessageKey.ABOUT_NOTE_1);

    // Verify "visit website" is present
    window
      .button(MessageKey.VISIT_WEBSITE.getKey())
      .requireVisible()
      .requireEnabled();

   // Verify "donate now" is present
    window
      .button(MessageKey.DONATE_NOW.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify Finish is present
    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled();

    // TODO click donate button
    // Click on the donate now button
//    window
//      .button(MessageKey.DONATE_NOW.getKey())
//      .click();
//
//    pauseForViewReset();
//
//    // Verify the send screen is shown
//    assertLabelText(MessageKey.SEND_BITCOIN_TITLE);
//
//    // Click on the cancel button on the send screen
//    window
//      .button(MessageKey.CANCEL.getKey())
//      .click();
//
//    // Check that the Settings screen is shown
//    window
//      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
//      .requireVisible()
//      .requireEnabled();

    // Click Finish
    window
      .button(MessageKey.FINISH.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_SIGN_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
