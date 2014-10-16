package org.multibit.hd.ui.fest.use_cases.sidebar.send_request.send;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.themes.Themes;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "send/receive" screen recipient</li>
 * </ul>
 * <p>Requires the "send/receive" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class SendVerifyRecipientAndCancelContactUseCase extends AbstractFestUseCase {

  public SendVerifyRecipientAndCancelContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Send allowing for network initialisation
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
        // Allow time for the Bitcoin network to initialise
      .requireEnabled(Timeout.timeout(5, TimeUnit.SECONDS))
      .click();

    // Verify the wizard appears
    assertLabelText(MessageKey.SEND_BITCOIN_TITLE);

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
    verifyRecipientField("", false);
    verifyRecipientField(" ", false);
    verifyRecipientField("AhN", false);
    verifyRecipientField("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty", false);
    verifyRecipientField("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXht", false);
    verifyRecipientField("1AhN6rPdrMuKBGFDKR1k9A8SCLYa", false);
    verifyRecipientField("1AhN6rPdrMuKBGFDk9A8SCLYaNgXhty", false);

    // Use a public domain P2SH address
    verifyRecipientField("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU", true);
    verifyRecipientField("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1t", false);

    // Set it to the MultiBit address
    verifyRecipientField("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty", true);

    // Set it to the MultiBit recipient with insufficient clarity
    verifyRecipientField("MultiBit", false);

    // Set it to the MultiBit recipient with sufficient clarity
    verifyRecipientField("MultiBit Donation", true);
    verifyRecipientField("MultiBit Donation 2", true);

    // Cancel from wizard

    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Verify underlying detail screen
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

  /**
   * Verifies that an incorrect recipient (Bitcoin address, xpub, name) is detected on focus loss
   *
   * @param text    The text to use
   * @param isValid True if the validation should pass
   */
  private void verifyRecipientField(String text, boolean isValid) {

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

}
