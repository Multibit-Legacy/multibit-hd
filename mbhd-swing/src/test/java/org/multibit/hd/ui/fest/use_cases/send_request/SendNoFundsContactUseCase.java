package org.multibit.hd.ui.fest.use_cases.send_request;

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
public class SendNoFundsContactUseCase extends AbstractFestUseCase {

  public SendNoFundsContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Send
    window
      .button(MessageKey.SEND.getKey())
      .click();

    // Verify the wizard appears
    window
      .label(MessageKey.SEND_BITCOIN_TITLE.getKey());

    // Verify buttons
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Use a public domain standard address
    verifyBitcoinAddressField("", false);
    verifyBitcoinAddressField(" ", false);
    verifyBitcoinAddressField("AhN", false);
    verifyBitcoinAddressField("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty", false);
    verifyBitcoinAddressField("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXht", false);
    verifyBitcoinAddressField("1AhN6rPdrMuKBGFDKR1k9A8SCLYa", false);
    verifyBitcoinAddressField("1AhN6rPdrMuKBGFDk9A8SCLYaNgXhty", false);

    // Use a public domain P2SH address
    verifyBitcoinAddressField("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU", true);
    verifyBitcoinAddressField("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1t", false);

    // Set it to the MultiBit address
    verifyBitcoinAddressField("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty", true);

  }

  /**
   * Verifies that an incorrect Bitcoin format is detected on focus loss
   *
   * @param text    The text to use as a Bitcoin address
   * @param isValid True if the validation should pass
   */
  private void verifyBitcoinAddressField(String text, boolean isValid) {

    // Set the text directly on the combo box editor
    window
      .textBox(MessageKey.RECIPIENT.getKey())
      .setText(text);

    // Lose focus to trigger validation
    window
      .button(MessageKey.PASTE.getKey())
      .focus();

    // Verify the focus change and background color of the editor
    if (isValid) {
      window
        .textBox(MessageKey.RECIPIENT.getKey())
        .background()
        .requireEqualTo(Themes.currentTheme.dataEntryBackground());
    } else {
      window
        .textBox(MessageKey.RECIPIENT.getKey())
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

  private void removeTag(String tag, int startCount) {

    // Count the tags
    final int tagCount1 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount1).isEqualTo(startCount);

    // Click on tag to remove
    window
      .list(MessageKey.TAGS.getKey())
      .clickItem(tag);

    // Count the tags
    final int tagCount2 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount2).isEqualTo(startCount - 1);

  }

}
