package org.multibit.hd.ui.fest.use_cases.history;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "history" screen search</li>
 * </ul>
 * <p>Requires the "history" screen to be showing</p>
 * <p>Requires "Opened" and "Password verified" to be present</p>
 *
 * @since 0.0.1
 *  
 */
public class SearchHistoryUseCase extends AbstractFestUseCase {

  public SearchHistoryUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.HISTORY.getKey())
      .rowCount();

    // Verify that rows are present (Password verified, Opened)
    assertThat(rowCount1)
      .describedAs("History from another wallet is being referenced")
      .isEqualTo(2);

    // Enter some search text
    window
      .textBox(MessageKey.SEARCH.getKey())
      .enterText("Opened");

    // Click search
    window
      .button(MessageKey.SEARCH.getKey())
      .click();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.HISTORY.getKey())
      .rowCount();

    // Verify that only a single row matches
    assertThat(rowCount2).isEqualTo(1);

  }

}
