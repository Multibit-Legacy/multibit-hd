package org.multibit.hd.ui.fest.use_cases.sidebar.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contact" screen search</li>
 * </ul>
 * <p>Requires the "contact" screen to be showing</p>
 * <p>Requires "Alice", "Bob Cratchit" and "Uriah" to be present</p>
 *
 * @since 0.0.1
 *  
 */
public class SearchContactUseCase extends AbstractFestUseCase {

  public SearchContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Verify that 2 rows are present (Alice, Bob and Uriah)
    assertThat(rowCount1).isEqualTo(3);

    // Enter some search text
    window
      .textBox(MessageKey.SEARCH.getKey())
      .enterText("Alice");

    // Click search
    window
      .button(MessageKey.SEARCH.getKey())
      .click();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Expect only "Alice" and "Bob Cratchit"
    assertThat(rowCount2).isEqualTo(2);

    // Search for "Alice" and "Bob Cratchit"
    window
      .table(MessageKey.CONTACTS.getKey())
      .cell("Alice");

    window
      .table(MessageKey.CONTACTS.getKey())
      .cell("Bob Cratchit");
  }

}
