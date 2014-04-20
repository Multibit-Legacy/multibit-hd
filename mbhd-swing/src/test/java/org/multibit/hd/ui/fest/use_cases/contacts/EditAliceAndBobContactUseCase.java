package org.multibit.hd.ui.fest.use_cases.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactState;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen edit Alice and Bob contacts</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 * <p>Requires Alice and Bob to be created</p>
 *
 * @since 0.0.1
 *  
 */
public class EditAliceAndBobContactUseCase extends AbstractFestUseCase {

  public EditAliceAndBobContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.CONTACTS.getKey())
      .contents().length;

    // Click on Bob's table row
    String[][] contacts = window
      .table(MessageKey.CONTACTS.getKey())
      .contents();

    if ("false".equals(contacts[0][ContactTableModel.CHECKBOX_COLUMN_INDEX])) {

      // Click on the row to activate the checkbox
      window
        .table(MessageKey.CONTACTS.getKey())
        .selectRows(0);
    }

    if ("false".equals(contacts[1][ContactTableModel.CHECKBOX_COLUMN_INDEX])) {

      // Click on the row to activate the checkbox
      window
        .table(MessageKey.CONTACTS.getKey())
        .selectRows(1);
    }

    // Click on Edit
    window
      .button(MessageKey.EDIT.getKey())
      .click();

    // Verify the multiple contact edit wizard appears
    window
      .label(MessageKey.EDIT_CONTACTS_TITLE.getKey());

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Update Alice and Bob's details

    // Add new common "Friends" tag
    addTag("Friends", 0);

    // Private notes
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("Alice and Bob are my friends");

    verifyCancel();

    // Click Apply
    window
      .button(MessageKey.APPLY.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.ADD.getKey())
      .requireVisible()
      .requireEnabled();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.CONTACTS.getKey())
      .contents().length;

    // Verify that no new row has been added
    assertThat(rowCount2).isEqualTo(rowCount1);

    // Update the contents
    contacts = window
      .table(MessageKey.CONTACTS.getKey())
      .contents();

    assertThat(contacts[0][ContactTableModel.TAG_COLUMN_INDEX]).contains("Friends");
    assertThat(contacts[1][ContactTableModel.TAG_COLUMN_INDEX]).contains("Friends");

  }

  /**
   * Verifies that clicking cancel with data present gives a Yes/No popover
   */
  private void verifyCancel() {

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Expect Yes/No popup)
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.CLOSE.getKey())
      .requireVisible()
      .requireEnabled();

    // Click No
    window
      .button(MessageKey.NO.getKey())
      .requireVisible()
      .requireEnabled()
      .click();
  }

  private void addTag(String tag, int startCount) {

    window
      .button(EditContactState.EDIT_CONTACT_ENTER_DETAILS + "." + MessageKey.ADD.getKey())
      .requireVisible()
      .requireDisabled();

    // Add a tag
    window
      .textBox(MessageKey.TAGS.getKey())
      .setText(tag);

    // Count the tags
    final int tagCount1 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount1).isEqualTo(startCount);

    // Click Add tag
    window
      .button(EditContactState.EDIT_CONTACT_ENTER_DETAILS + "." + MessageKey.ADD.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Count the tags
    final int tagCount2 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount2).isEqualTo(tagCount1 + 1);

  }

  private void removeTag(int tagIndex, int startCount) {

    // Count the tags
    final int tagCount1 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount1).isEqualTo(startCount);

    // Click Remove on "tag"
    window
      .list(MessageKey.TAGS.getKey())
      .selectItem(tagIndex);

    // Count the tags
    final int tagCount2 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount2).isEqualTo(startCount - 1);

  }

}
