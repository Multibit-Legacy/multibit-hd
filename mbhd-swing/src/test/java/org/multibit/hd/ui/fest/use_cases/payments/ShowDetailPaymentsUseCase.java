package org.multibit.hd.ui.fest.use_cases.payments;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.WhitespaceTrimmer;
import org.multibit.hd.ui.views.components.tables.PaymentTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Show details on the Payments screen</li>
 * </ul>
 * <p>Requires the "Payments" screen to be showing</p>
 * <p>Requires at least one payment to be present</p>
 *
 * @since 0.0.1
 *  
 */
public class ShowDetailPaymentsUseCase extends AbstractFestUseCase {

  private static final Logger log = LoggerFactory.getLogger(ShowDetailPaymentsUseCase.class);

  public ShowDetailPaymentsUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
            .table(MessageKey.PAYMENTS.getKey())
            .rowCount();

    // Select the first 7 rows in turn
    for (int i = 0; i < Math.min(rowCount1, 7); i++) {
      // Get the payment data

      String[][] payments = window
              .table(MessageKey.PAYMENTS.getKey())
              .contents();

      // See if it is a payment request or a transaction
      boolean isPaymentRequest = Languages.safeText(CoreMessageKey.PAYMENT_REQUESTED).equals(WhitespaceTrimmer.trim(payments[i][PaymentTableModel.TYPE_COLUMN_INDEX]));

      window
              .table(MessageKey.PAYMENTS.getKey())
              .selectRows(i);

      // Show the details
      window
              .button(MessageKey.DETAILS.getKey())
              .click();

      if (isPaymentRequest) {
        // Verify the payment details wizard appears, showing a payment request
        assertLabelText(MessageKey.PAYMENT_REQUEST);

        window
                .button(MessageKey.CANCEL.getKey())
                .requireVisible()
                .requireEnabled();

        // Verify bitcoin address is shown
        window
                .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
                .requireVisible();

        // Click finish
        window
                .button(MessageKey.FINISH.getKey())
                .click();

        // Verify the underlying screen is back
        window
                .button(MessageKey.DETAILS.getKey())
                .requireVisible()
                .requireEnabled();
      } else {

        // Verify the payment details wizard appears, showing a transaction overview panel
        assertLabelText(MessageKey.TRANSACTION_OVERVIEW);

        window
                .button(MessageKey.NEXT.getKey())
                .requireVisible()
                .requireEnabled();

        // Click next
        window
                .button(MessageKey.NEXT.getKey())
                .click();

        // Verify showing the transaction amount panel
        assertLabelText(MessageKey.TRANSACTION_AMOUNT);

        // Click next
        window
                .button(MessageKey.NEXT.getKey())
                .click();

        // Verify showing the transaction details panel
        assertLabelText(MessageKey.TRANSACTION_DETAIL);

        // Click next
        window
                .button(MessageKey.NEXT.getKey())
                .click();

        // Verify showing the choose payment request
        assertLabelText(MessageKey.CHOOSE_PAYMENT_REQUEST);

        // Click next
        window
                .button(MessageKey.NEXT.getKey())
                .click();

        // Verify showing payment request details
        assertLabelText(MessageKey.PAYMENT_REQUEST);

        // Click finish
        window
                .button(MessageKey.FINISH.getKey())
                .click();

        // Verify the underlying screen is back
        window
                .button(MessageKey.DETAILS.getKey())
                .requireVisible()
                .requireEnabled();
      }
    }
  }
}
