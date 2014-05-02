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
 * <li>Verify the "contacts" screen edit Bob contact then cancel changes</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 * <p>Requires the "Bob Cratchit" contact to be present</p>
 *
 * @since 0.0.1
 *  
 */
public class EditThenCancelBobContactUseCase extends AbstractFestUseCase {

  public EditThenCancelBobContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Find Bob's row
    int bobRow = window
      .table(MessageKey.CONTACTS.getKey())
      .cell("Bob Cratchit")
      .row;

    // Get table contents
    String[][] contacts =  window
      .table(MessageKey.CONTACTS.getKey())
      .contents();

    if ("false".equals(contacts[bobRow][ContactTableModel.CHECKBOX_COLUMN_INDEX])) {

      // Click on the row to activate the checkbox
      window
        .table(MessageKey.CONTACTS.getKey())
        .selectRows(bobRow);
    }

    // Click on Edit
    window
      .button(MessageKey.EDIT.getKey())
      .click();

    // Verify the single contact edit wizard appears
    window
      .label(MessageKey.EDIT_CONTACT_TITLE.getKey());

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Update Bob's details
    window
      .textBox(MessageKey.NAME.getKey())
      .setText("Bob Cratchit Jones");

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Expect Yes/No popover
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button("popover."+MessageKey.CLOSE.getKey())
      .requireVisible()
      .requireEnabled();

    // Click Yes
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.ADD.getKey())
      .requireVisible()
      .requireEnabled();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Verify that no new row has been added
    assertThat(rowCount2).isEqualTo(rowCount1);

    // Get updated table contents
    contacts =  window
      .table(MessageKey.CONTACTS.getKey())
      .contents();

    assertThat(contacts[bobRow][ContactTableModel.NAME_COLUMN_INDEX].contains("Jones")).isFalse();

  }

}
