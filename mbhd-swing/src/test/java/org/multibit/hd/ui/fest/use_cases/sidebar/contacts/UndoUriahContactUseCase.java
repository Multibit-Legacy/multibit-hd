package org.multibit.hd.ui.fest.use_cases.sidebar.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen can undo the Uriah contact delete operation</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 * <p>Requires the "Uriah Heep" contact to have ben deleted</p>
 *
 * @since 0.0.1
 *
 */
public class UndoUriahContactUseCase extends AbstractFestUseCase {

  public UndoUriahContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Click on Undo
    window
      .button(MessageKey.UNDO.getKey())
      .click();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.CONTACTS.getKey())
      .contents().length;

    // Verify the row has been restored
    assertThat(rowCount2).isEqualTo(rowCount1 + 1);

    // Locate Uriah Heep
    window
      .table(MessageKey.CONTACTS.getKey())
      .cell("Uriah Heep");

  }

}
