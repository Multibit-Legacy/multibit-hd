package org.multibit.hd.ui.fest.use_cases.sidebar.help;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.awt.*;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "history" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SelectGettingStartedUseCase extends AbstractFestUseCase {

  public SelectGettingStartedUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    String helpContents;

    // Ensure we are on the correct page
    helpContents = window
      .textBox(MessageKey.HELP.getKey() + ".editorPane")
      .text();
    assertThat(helpContents).contains("MultiBit HD help");

    Component editorPane = window
      .textBox(MessageKey.HELP.getKey() + ".editorPane")
      .component();

    // Attempt to click on an internal link
    //
    // This positioning is fragile and relies on the precise
    // location of the "Getting Started" link
    //
    // This should be constant based on the internal help
    // but a change in position can be quickly corrected
    // by offering a series of guesses with pauses in between
    //        window
    //          .robot
    //          .moveMouse(editorPane, 100, 190);
    //        pauseForViewReset();

    // Click the "Getting Started" link
    window
      .robot
      .click(editorPane, new Point(100, 220));

    // Ensure we are on the correct page
    helpContents = window
      .textBox(MessageKey.HELP.getKey() + ".editorPane")
      .text();
    assertThat(helpContents).contains("Step 1 - Download and install");

  }

}
