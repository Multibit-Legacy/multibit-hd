package org.multibit.hd.ui.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.dto.MBHDPaymentRequestData;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

/**
 * Create a CSVEntryConverter for the header values in the CSV
 */
public class PaymentRequestHeaderConverter implements CSVEntryConverter<MBHDPaymentRequestData> {
  @Override
  public String[] convertEntry(MBHDPaymentRequestData MBHDPaymentRequestData) {
    String[] columns = new String[13];

   // Date
    columns[0] = Languages.safeText(MessageKey.DATE);

    // Type
    columns[1] = Languages.safeText(MessageKey.TYPE);

    // Bitcoin address
    columns[2] = Languages.safeText(MessageKey.BITCOIN_ADDRESS);

    // Description
    columns[3] = Languages.safeText(MessageKey.DESCRIPTION);

    // QR code label
    columns[4] = Languages.safeText(MessageKey.QR_CODE_LABEL);

    // Private notes
    columns[5] = Languages.safeText(MessageKey.PRIVATE_NOTES);

    // Amount in satoshi
    columns[6] = Languages.safeText(MessageKey.LOCAL_AMOUNT) + " "  + BitcoinSymbol.SATOSHI.getTextSymbol();

    // Fiat currency symbol
    columns[7] = Languages.safeText(MessageKey.FIAT_CURRENCY);

    // Fiat currency amount
    columns[8] = Languages.safeText(MessageKey.FIAT_AMOUNT);

    // Exchange rate
    columns[9] = Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL);

    // Exchange rate provider
    columns[10] = Languages.safeText(MessageKey.EXCHANGE_RATE_PROVIDER);

    // Paid amount in satoshi
    columns[11] = Languages.safeText(MessageKey.AMOUNT_PAID) + " " + BitcoinSymbol.SATOSHI.getTextSymbol();

    // Funding transactions
    columns[12] = Languages.safeText(MessageKey.TRANSACTION_HASH);

    return columns;
  }
}

