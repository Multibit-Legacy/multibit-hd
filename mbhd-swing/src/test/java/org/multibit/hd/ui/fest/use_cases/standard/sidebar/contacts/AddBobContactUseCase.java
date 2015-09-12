package org.multibit.hd.ui.fest.use_cases.standard.sidebar.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactState;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen add Bob contact</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class AddBobContactUseCase extends AbstractFestUseCase {

  public AddBobContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Click on Add
    window
      .button(MessageKey.ADD.getKey())
      .click();

    // Verify the wizard appears
    assertLabelText(MessageKey.NEW_CONTACT_TITLE);

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify contact image
    window
      .label(MessageKey.CONTACT_IMAGE.getKey())
      .requireVisible();

    // Fill in Bob's details
    window
      .textBox(MessageKey.NAME.getKey())
      .setText("Bob");

    window
      .textBox(MessageKey.EMAIL_ADDRESS.getKey())
      .setText("bob@example.org");

    window
      .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
      .setText("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    window
      .textBox(MessageKey.TAGS.getKey())
      .setText("");

    // Add some tags (testing empty and duplicates)
    addTag("Bob", 0, true);

    // Blocked tags
    addTag("Bob", 1, false);
    addTag("", 1, false);
    addTag(" ", 1, false);

    // Accepted tags
    addTag("VIP", 1, true);
    addTag("Family", 2, true);
    addTag("Entrepreneur", 3, true);
    addTag("Programmer", 4, true);
    addTag("❤ Artist ❤", 5, true);
    addTag("Poet", 6, true);
    addTag("Traveller", 7, true);

    // Blocked tags
    addTag("Author", 8,false);

    // Remove the tags from positions 0 or 1 due to FEST limitations
    removeTag(8);
    removeTag(7);
    removeTag(6);
    removeTag(5);
    removeTag(4);
    removeTag(3);
    removeTag(2);
    removeTag(1);

    // Add a previously deleted tag
    addTag("Bob", 0, true);

    // Private notes
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("Bob's private notes");

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

    // Verify a new row has been added
    assertThat(rowCount2).isEqualTo(rowCount1 + 1);

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

  /**
   * @param tag            The tag text
   * @param startCount     The start count (1-based)
   * @param expectAccepted True if the tag should be accepted
   */
  private void addTag(String tag, int startCount, boolean expectAccepted) {

    if (expectAccepted) {
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

    } else {

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

      // Expect no Add button
      window
        .button(EditContactState.EDIT_CONTACT_ENTER_DETAILS + "." + MessageKey.ADD.getKey())
        .requireVisible()
        .requireDisabled();

    }

  }

}
