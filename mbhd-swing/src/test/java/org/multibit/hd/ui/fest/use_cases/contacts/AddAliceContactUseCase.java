package org.multibit.hd.ui.fest.use_cases.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactState;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen add Alice contact</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class AddAliceContactUseCase extends AbstractFestUseCase {

  public AddAliceContactUseCase(FrameFixture window) {
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

    // Fill in Alice's details
    window
      .textBox(MessageKey.NAME.getKey())
      .setText("Alice");

    window
      .textBox(MessageKey.EMAIL_ADDRESS.getKey())
      .setText("alice@example.org");

    // Use a public domain standard address
    verifyBitcoinAddressField("", true);
    verifyBitcoinAddressField(" ", true);
    verifyBitcoinAddressField("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty", false);
    verifyBitcoinAddressField("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXht", false);
    verifyBitcoinAddressField("1AhN6rPdrMuKBGFDKR1k9A8SCLYa", false);
    verifyBitcoinAddressField("1AhN6rPdrMuKBGFDk9A8SCLYaNgXhty", false);

    // Use a public domain P2SH address
    verifyBitcoinAddressField("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU", true);
    verifyBitcoinAddressField("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1t", false);

    // Set it to the MultiBit address
    verifyBitcoinAddressField("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty", true);

    window
      .textBox(MessageKey.EXTENDED_PUBLIC_KEY.getKey())
      .setText("");

    // Ensure Add button is disabled without tag
    addTag("Poet", 0);
    addTag("Traveller", 1);
    removeTag(2);

    // Private notes
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("Alice's private notes");

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
   * Verifies that an incorrect Bitcoin format is detected on focus loss
   *
   * @param text    The text to use as a Bitcoin address
   * @param isValid True if the validation should pass
   */
  private void verifyBitcoinAddressField(String text, boolean isValid) {

    // Set the text
    window
      .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
      .setText(text);

    // Lose focus to trigger validation
    window
      .textBox(MessageKey.EXTENDED_PUBLIC_KEY.getKey())
      .focus();

    // Verify the focus change and background color
    if (isValid) {
      window
        .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
        .background()
        .requireEqualTo(Themes.currentTheme.dataEntryBackground());
    } else {
      window
        .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
        .background()
        .requireEqualTo(Themes.currentTheme.invalidDataEntryBackground());
    }

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
      .button("popover."+MessageKey.CLOSE.getKey())
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
