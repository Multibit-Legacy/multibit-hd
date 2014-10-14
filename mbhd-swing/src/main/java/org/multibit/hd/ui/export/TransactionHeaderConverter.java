package org.multibit.hd.ui.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

/**
 * Create a CSVEntryConverter for the header values in the CSV
 */
public class TransactionHeaderConverter implements CSVEntryConverter<TransactionData> {

  @Override
  public String[] convertEntry(TransactionData transactionData) {
    String[] columns = new String[14];

    // Date.
    columns[0] = Languages.safeText(MessageKey.DATE);

    // Status
    columns[1] = Languages.safeText(MessageKey.STATUS);

    // Type
    columns[2] = Languages.safeText(MessageKey.TYPE);

   // Description.
    columns[3] = Languages.safeText(MessageKey.DESCRIPTION);

   // Note.
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

    // Miner's fee
    columns[10] = Languages.safeText(MessageKey.TRANSACTION_FEE);

    // Client fee
    columns[11] = Languages.safeText(MessageKey.CLIENT_FEE);

    // coinbase
    columns[12] = Languages.safeText(MessageKey.COINBASE);

    // Transaction hash.
    columns[13] = Languages.safeText(MessageKey.TRANSACTION_HASH);

    return columns;
  }
}

