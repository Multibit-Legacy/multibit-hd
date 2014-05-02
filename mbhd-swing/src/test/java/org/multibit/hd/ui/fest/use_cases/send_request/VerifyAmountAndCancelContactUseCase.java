package org.multibit.hd.ui.fest.use_cases.send_request;

import com.google.common.base.Strings;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.themes.Themes;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "send/receive" screen amount fields</li>
 * </ul>
 * <p>Requires the "send/receive" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class VerifyAmountAndCancelContactUseCase extends AbstractFestUseCase {

  public VerifyAmountAndCancelContactUseCase(FrameFixture window) {
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

    // Set the recipient editor text box to the MultiBit address
    window
      .textBox(MessageKey.RECIPIENT.getKey())
      .setText("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    // Change focus to trigger validation
    window
      .button(MessageKey.PASTE.getKey())
      .focus();

    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    verifyBitcoinAmountField("", false);
    verifyBitcoinAmountField(" ", false);
    verifyBitcoinAmountField("abc", false);
    verifyBitcoinAmountField("'", false);

    verifyBitcoinAmountField("0", true);
    verifyBitcoinAmountField("0.0", true);

    verifyBitcoinAmountField("-1", false);
    verifyBitcoinAmountField("-0.1", false);

    verifyBitcoinAmountField("0.1", true);
    verifyBitcoinAmountField("3.33333", true);
    verifyBitcoinAmountField("9.99999", true);
    verifyBitcoinAmountField("0.00001", true); // 1 sat in mBTC

    verifyBitcoinAmountField("1", true); // 1 BTC
    verifyBitcoinAmountField("10", true);
    verifyBitcoinAmountField("100", true);
    verifyBitcoinAmountField("100.0", true);
    verifyBitcoinAmountField("100.00001", true);
    verifyBitcoinAmountField("1000", true); // 1 BTC

    verifyBitcoinAmountField("1,000", true);
    verifyBitcoinAmountField("1,000.00001", true);

    verifyBitcoinAmountField("1 000", false); // 1,000 BTC
    verifyBitcoinAmountField("1 000 000 000", false); // 1,000,000 BTC

    verifyBitcoinAmountField("21000000000", true); // 21,000,000 BTC
    verifyBitcoinAmountField("20000000000.12345", true); // 20,000,000,000.12345 mBTC

    verifyBitcoinAmountField("21,000,000,000", true); // 21,000,000 BTC
    verifyBitcoinAmountField("20,000,000,000.12345", true); // 20,000,000,000.12345 mBTC

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
   * Verifies that an incorrect Bitcoin amount format is detected on focus loss
   *
   * @param text    The text to use as a Bitcoin address
   * @param isValid True if the validation should pass
   */
  private void verifyBitcoinAmountField(String text, boolean isValid) {

    // Enter the text into the amount field
    window
      .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
        // Must clear first then "type" the values to ensure the local amount updates
      .setText("")
      .enterText(text);

    // Lose focus to trigger validation
    window
      .button(MessageKey.PASTE.getKey())
      .focus();

    // Verify the focus change and background color of the editor
    if (isValid) {
      window
        .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
        .background()
        .requireEqualTo(Themes.currentTheme.dataEntryBackground());

      // Verify the local amount has updated
      String localAmount = window
        .textBox(MessageKey.LOCAL_AMOUNT.getKey())
        .text();

      assertThat(Strings.isNullOrEmpty(localAmount)).isFalse();

      window
        .textBox(MessageKey.LOCAL_AMOUNT.getKey())
        .background()
        .requireEqualTo(Themes.currentTheme.dataEntryBackground());

    } else {
      window
        .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
        .background()
        .requireEqualTo(Themes.currentTheme.invalidDataEntryBackground());
    }


  }

}
