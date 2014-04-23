package org.multibit.hd.ui.fest.use_cases.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen delete Uriah contact</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 * <p>Requires the "Uriah Heep" contact to be present</p>
 *
 * @since 0.0.1
 *  
 */
public class DeleteUriahContactUseCase extends AbstractFestUseCase {

  public DeleteUriahContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the contacts
    String[][] contacts = window
      .table(MessageKey.CONTACTS.getKey())
      .contents();

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Find the Uriah Heep row
    int uriahRow = window
        .table(MessageKey.CONTACTS.getKey())
        .cell("Uriah Heep").row;

    // Check if it is selected
    if ("false".equals(contacts[uriahRow][ContactTableModel.CHECKBOX_COLUMN_INDEX])) {

      // Click on the row to activate the checkbox
      window
        .table(MessageKey.CONTACTS.getKey())
        .selectRows(uriahRow);
    }

    // Click on Delete
    window
      .button(MessageKey.DELETE.getKey())
      .click();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Verify the row has been deleted
    assertThat(rowCount2).isEqualTo(rowCount1 - 1);

  }

}
