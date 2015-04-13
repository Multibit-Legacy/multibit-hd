package org.multibit.hd.ui.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

/**
 * Create a CSVEntryConverter for the header values in the CSV
 */
public class PaymentRequestHeaderConverter implements CSVEntryConverter<PaymentRequestData> {
  @Override
  public String[] convertEntry(PaymentRequestData PaymentRequestData) {
    String[] columns = new String[12];

   // Date
    columns[0] = Languages.safeText(MessageKey.DATE);

    // Type
    columns[1] = Languages.safeText(MessageKey.TYPE);

    // UUID
    columns[2] = Languages.safeText(MessageKey.UUID);

    // Description
    columns[3] = Languages.safeText(MessageKey.DESCRIPTION);

    // Private notes
    columns[4] = Languages.safeText(MessageKey.PRIVATE_NOTES);

    // Amount in satoshi
    columns[5] = Languages.safeText(MessageKey.LOCAL_AMOUNT) + " "  + BitcoinSymbol.SATOSHI.getTextSymbol();

    // Fiat currency symbol
    columns[6] = Languages.safeText(MessageKey.FIAT_CURRENCY);

    // Fiat currency amount
    columns[7] = Languages.safeText(MessageKey.FIAT_AMOUNT);

    // Exchange rate
    columns[8] = Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL);

    // Exchange rate provider
    columns[9] = Languages.safeText(MessageKey.EXCHANGE_RATE_PROVIDER);

    // Matching transaction hash
    columns[10] = Languages.safeText(MessageKey.TRANSACTION_HASH);

    // Identity
    columns[11] = Languages.safeText(MessageKey.IDENTITY);

    return columns;
  }
}

