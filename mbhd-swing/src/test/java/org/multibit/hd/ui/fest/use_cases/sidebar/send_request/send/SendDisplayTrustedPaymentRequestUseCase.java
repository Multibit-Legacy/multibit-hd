package org.multibit.hd.ui.fest.use_cases.sidebar.send_request.send;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.services.ExternalDataListeningService;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState;

import java.net.URL;
import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "send" view with a trusted payment request</li>
 * </ul>
 *
 * @since 0.0.8
 *
 */
public class SendDisplayTrustedPaymentRequestUseCase extends AbstractFestUseCase {


  public SendDisplayTrustedPaymentRequestUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Trigger a payment request alert

    final URL signedPaymentRequestUrl = SendDisplayTrustedPaymentRequestUseCase.class.getResource("/fixtures/payments/dblsha-signed.bitcoinpaymentrequest");

    // Send a file URL to the external data service
    ExternalDataListeningService.writeToSocket("file://" + signedPaymentRequestUrl.getFile());

    // Wait for the alert to appear
    pauseForComponentReset();

    // Check that an alert message is present
    assertLabelContainsValue("alert_message_label", "Trusted payment request \"Donate!\" for \"mB 10.00000\". Continue ?");

    // Check the 'Yes' button on the alert is present and click it
    window
      .button(MessageKey.YES.getKey())
      .click();

    // Wait for the "send bitcoin" wizard to appear
    pauseForComponentReset();

    // Verify the wizard appears
    assertLabelText(MessageKey.DISPLAY_PAYMENT_REQUEST_TITLE);

    // Verify buttons
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    // Verify buttons
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    // Verify content
    window
      .label("trust_status")
      .requireVisible()
      .requireText("This payment request provided identity information");

    window
      .label(MessageKey.NOTES.getKey()+".value")
      .requireVisible()
      .requireText("Donate!");

    window
      .label(MessageKey.NAME.getKey()+".value")
      .requireVisible()
      .requireText("n/a");

    window
      .label(MessageKey.DATE.getKey()+".value")
      .requireVisible()
      .requireText("01 Mar 2015 17:16");

    window
      .label(MessageKey.EXPIRES.getKey()+".value")
      .requireVisible()
      .requireText("n/a");

    window
      .label(SendBitcoinState.SEND_DISPLAY_PAYMENT_REQUEST.name() + ".amount.primary_balance")
      .requireVisible()
      .requireText("\u00a010.00");
    window
      .label(SendBitcoinState.SEND_DISPLAY_PAYMENT_REQUEST.name() + ".amount.secondary_balance")
      .requireVisible()
      .requireText("000");

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      .requireVisible();

  }

}
