package org.multibit.hd.ui.fest.use_cases.contacts;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;

import java.awt.event.KeyEvent;
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
public class EditThenCancelBobContactKeyboardUseCase extends AbstractFestUseCase {

  public EditThenCancelBobContactKeyboardUseCase(FrameFixture window) {
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
      .cell("Bob")
      .row;

    ensureCheckboxIsSelected(MessageKey.CONTACTS, bobRow, ContactTableModel.CHECKBOX_COLUMN_INDEX);

    // Click Enter
    window
      .table(MessageKey.CONTACTS.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_ENTER));

    // Verify the single contact edit wizard appears
    window
      .label(MessageKey.EDIT_CONTACT_TITLE.getKey());

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

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

  }

}
