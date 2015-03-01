package org.multibit.hd.ui.fest.use_cases.sidebar.send_request.send;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.services.ExternalDataListeningService;

import java.net.URL;
import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "send" view with a payment request</li>
 * </ul>
 *
 * @since 0.0.8
 *
 */
public class SendDisplayPaymentRequestUseCase extends AbstractFestUseCase {


  public SendDisplayPaymentRequestUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Trigger a payment request alert

    final URL signedPaymentRequestUrl = SendDisplayPaymentRequestUseCase.class.getResource("/fixtures/payments/localhost-signed.bitcoinpaymentrequest");

    // Send a file URL to the external data service
    ExternalDataListeningService.writeToSocket("file://" + signedPaymentRequestUrl.getFile());

    // Wait for the alert to appear
    pauseForComponentReset();

    // Check that an alert message is present
    assertLabelContainsValue("alert_message_label", "Please donate to MultiBit");

    // Check the 'Yes' button on the alert is present and click it
    window
      .button(MessageKey.YES.getKey())
      .click();

  }

}
