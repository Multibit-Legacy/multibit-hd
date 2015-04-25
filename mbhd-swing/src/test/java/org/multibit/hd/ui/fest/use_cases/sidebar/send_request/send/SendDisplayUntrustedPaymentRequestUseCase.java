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
 * <li>Verify the "send" view with an untrusted payment request</li>
 * </ul>
 *
 * @since 0.0.8
 *
 */
public class SendDisplayUntrustedPaymentRequestUseCase extends AbstractFestUseCase {


  public SendDisplayUntrustedPaymentRequestUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Trigger a payment request alert

    final URL signedPaymentRequestUrl = SendDisplayUntrustedPaymentRequestUseCase.class.getResource("/fixtures/payments/localhost-signed.bitcoinpaymentrequest");

    // Send a file URL to the external data service
    ExternalDataListeningService.writeToSocket("file://" + signedPaymentRequestUrl.getFile());

    // Wait for the alert to appear
    pauseForComponentReset();
    pauseForComponentReset();

    // Check that an alert message is present
    assertLabelContainsValue("alert_message_label", "Please donate to MultiBit");

    // Check the 'Yes' button on the alert is present and click it
    window
      .button(MessageKey.YES.getKey())
      .click();

    // Wait for the "send bitcoin" wizard to appear
    pauseForComponentReset();

    // Verify the wizard appears
    assertLabelText(MessageKey.DISPLAY_PAYMENT_REQUEST_TITLE);

    // Verify 'Pay this payment request' button
    window
      .button(MessageKey.PAY_THIS_PAYMENT_REQUEST.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify the Finish button
    window
       .button(MessageKey.FINISH.getKey())
       .requireVisible()
       .requireEnabled();

     // Verify content
    window
      .label("trust_status")
      .requireVisible()
      .requireText("This payment request did not provide identity information");

    window
      .label(MessageKey.NOTES.getKey()+".value")
      .requireVisible()
      .requireText("Please donate to MultiBit");

    window
      .label(MessageKey.NAME.getKey()+".value")
      .requireVisible()
      .requireText("");

    window
      .label(MessageKey.DATE.getKey()+".value")
      .requireVisible()
      .requireText("26 Feb 2015 11:29");

// TODO - expires seems to be filled in with now
//    window
//      .label(MessageKey.EXPIRES.getKey()+".value")
//      .requireVisible()
//      .requireText("n/a");

    window
      .label(SendBitcoinState.SEND_DISPLAY_PAYMENT_REQUEST.name() + ".amount.primary_balance")
      .requireVisible()
      .requireText("\u00a010.00");
    window
      .label(SendBitcoinState.SEND_DISPLAY_PAYMENT_REQUEST.name() + ".amount.secondary_balance")
      .requireVisible()
      .requireText("000");

    // Click the 'Pay the Payment request' button
    window
      .button(MessageKey.PAY_THIS_PAYMENT_REQUEST.getKey())
      .click();

    // Verify the 'Send progress' screen appears
    // There won't be any money to pay the payment request
    assertLabelText(MessageKey.SEND_PROGRESS_TITLE);

    // There should be an enabled Cancel button and a disabled Next button
    window
       .button(MessageKey.CANCEL.getKey())
       .requireVisible()
       .requireEnabled();

    window
        .button(MessageKey.NEXT.getKey())
        .requireVisible()
        .requireDisabled();

     // Click the 'Cancel' button to dismiss the 'Send progress' screen
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();
  }
}
