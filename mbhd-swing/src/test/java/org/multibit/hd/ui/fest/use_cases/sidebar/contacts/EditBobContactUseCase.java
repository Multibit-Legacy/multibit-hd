package org.multibit.hd.ui.fest.use_cases.sidebar.contacts;

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
 * <li>Verify the "contacts" screen edit Bob contact</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 * <p>Requires the "Bob" contact to be present</p>
 *
 * @since 0.0.1
 *  
 */
public class EditBobContactUseCase extends AbstractFestUseCase {

  public EditBobContactUseCase(FrameFixture window) {
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

    // Get the contacts
    String[][] contacts =  window
      .table(MessageKey.CONTACTS.getKey())
      .contents();

    ensureCheckboxIsSelected(MessageKey.CONTACTS, bobRow, ContactTableModel.CHECKBOX_COLUMN_INDEX);

    // Click on Edit
    window
      .button(MessageKey.EDIT.getKey())
      .click();

    // Verify the single contact edit wizard appears
    assertLabelText(MessageKey.EDIT_CONTACT_TITLE);

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify contact image
    window
      .label(MessageKey.CONTACT_IMAGE.getKey())
      .requireVisible();

    // Update Bob's details
    window
      .textBox(MessageKey.NAME.getKey())
      .requireText("Bob")
      .setText("Bob Cratchit");

    window
      .textBox(MessageKey.EMAIL_ADDRESS.getKey())
      .requireText("bob@example.org")
      .setText("bob.cratchit@example.org");



    // Ensure Add button is disabled without tag
    addTag("Scrooge Staff", 1);

    // Private notes
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("Bob is now working for Scrooge");

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
      .rowCount();

    // Verify that no new row has been added
    assertThat(rowCount2).isEqualTo(rowCount1);

    // Verify that "Alice" is unaffected
    window
      .table(MessageKey.CONTACTS.getKey())
      .cell("Alice");

    // Verify that "Bob" is now "Bob Cratchit"
    window
      .table(MessageKey.CONTACTS.getKey())
      .cell("Bob Cratchit");
  }

  /**
   * Verifies that clicking cancel with data present gives a Yes/No popover
   */
  private void verifyCancel() {

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
      .button("popover_"+MessageKey.CLOSE.getKey())
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

}
