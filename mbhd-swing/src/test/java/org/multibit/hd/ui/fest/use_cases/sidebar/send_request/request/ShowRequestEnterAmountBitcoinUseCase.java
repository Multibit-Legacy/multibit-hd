package org.multibit.hd.ui.fest.use_cases.sidebar.send_request.request;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "request" wizard with finish</li>
 * <li>Verify the payment request is showing</li>
 * </ul>
 * <p>Requires the "send/request" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class ShowRequestEnterAmountBitcoinUseCase extends AbstractFestUseCase {

  public ShowRequestEnterAmountBitcoinUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Request allowing for network initialisation
    window
      .button(MessageKey.SHOW_REQUEST_WIZARD.getKey())
        // Allow time for the Bitcoin network to initialise
      .requireEnabled(Timeout.timeout(5, TimeUnit.SECONDS))
      .click();

    // Verify the wizard appears
    assertLabelText(MessageKey.REQUEST_BITCOIN_TITLE);

    // Enter an amount
    window
      .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEmpty()
      .enterText("31.23");

    // Enter a QR code label label
    window
      .textBox(MessageKey.QR_CODE_LABEL.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEmpty()
      .setText("Beers and snacks");

    // Enter some private notes
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEmpty()
      .setText("Invoice #43: 6 beers and some snacks");

    // Click Finish
    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled(Timeout.timeout(1, TimeUnit.SECONDS))
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_REQUEST_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify the amount is showing as receiving in the correct position
    String paymentReceiving0 = window
      .label("SEND_REQUEST_REQUESTED.requested.0.primary_balance")
      .text();

    assertThat(paymentReceiving0).contains("31.23");

  }

}
