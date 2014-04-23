package org.multibit.hd.ui.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

/**
 * Create a CSVEntryConverter for the header values in the CSV
 */
public class PaymentRequestHeaderConverter implements CSVEntryConverter<PaymentRequestData> {
  @Override
  public String[] convertEntry(PaymentRequestData paymentRequestData) {
    String[] columns = new String[12];

   // Date.
    columns[0] = Languages.safeText(MessageKey.DATE);

    // Type
    columns[1] = Languages.safeText(MessageKey.TYPE);

    // Bitcoin address.
    columns[2] = Languages.safeText(MessageKey.BITCOIN_ADDRESS);

    // Description.
    columns[3] = Languages.safeText(MessageKey.DESCRIPTION);

    // QR code label.
    columns[4] = Languages.safeText(MessageKey.QR_CODE_LABEL_LABEL);

    // Private notes.
    columns[5] = Languages.safeText(MessageKey.PRIVATE_NOTES);

    // Amount in BTC.
    columns[6] = Languages.safeText(MessageKey.LOCAL_AMOUNT) + " "  + Configurations.currentConfiguration.getBitcoin().getBitcoinSymbol();

    // Amount in fiat
    columns[7] = Languages.safeText(MessageKey.LOCAL_AMOUNT) + " " + Configurations.currentConfiguration.getBitcoin().getLocalCurrencySymbol();

    // Exchange rate
    columns[8] = Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL);

    // Exchange rate provider
    columns[9] = Languages.safeText(MessageKey.EXCHANGE_RATE_PROVIDER);

    // Paid amount in BTC.
    columns[10] = Languages.safeText(MessageKey.AMOUNT_PAID) + " "  + Configurations.currentConfiguration.getBitcoin().getBitcoinSymbol();

    // Funding transactions.
    columns[11] = Languages.safeText(MessageKey.TRANSACTION_HASH);

    return columns;
  }
}

