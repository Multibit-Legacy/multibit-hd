package org.multibit.hd.ui.fest.use_cases.send_request.request;

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
 * <li>Verify the "request" wizard with a QR code then finish</li>
 * <li>Verify the payment summary is displayed</li>
 * </ul>
 * <p>Requires the "send/request" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class ShowRequestEnterAmountWithQRBitcoinUseCase extends AbstractFestUseCase {

  public ShowRequestEnterAmountWithQRBitcoinUseCase(FrameFixture window) {
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
      .setText("100.0");

    // Enter a transaction label
    window
      .textBox(MessageKey.TRANSACTION_LABEL.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEmpty()
      .setText("Beers");

    // Enter some private notes
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEmpty()
      .setText("Invoice #42: 20 beers");

    // Click the QR code button
    window
      .button(MessageKey.QR_CODE.getKey())
      .click();

    // Verify the QR code popover is showing
    window
      .label(MessageKey.QR_CODE.getKey())
      .requireVisible();

    // Click the close button
    window
      .button("popover_" + MessageKey.CLOSE.getKey())
      .click();

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
    String paymentReceiving0= window
      .label("SEND_REQUEST_REQUESTED_requested_0_primary_balance")
      .text();

    assertThat(paymentReceiving0).contains("100.00");

  }

}
