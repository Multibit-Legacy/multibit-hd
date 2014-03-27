package org.multibit.hd.ui.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

/**
 * Create a CSVEntryConverter for the header values in the CSV
 */
public class TransactionHeaderConverter implements CSVEntryConverter<TransactionData> {

  @Override
  public String[] convertEntry(TransactionData transactionData) {
    String[] columns = new String[12];

    // Date.
    columns[0] = Languages.safeText(MessageKey.DATE);

    // Status
    columns[1] = Languages.safeText(MessageKey.STATUS);

    // Type
    columns[2] = Languages.safeText(MessageKey.TYPE);

   // Description.
    columns[3] = Languages.safeText(MessageKey.DESCRIPTION);

   // Note.
    columns[4] = Languages.safeText(MessageKey.NOTES);

    // Amount in BTC.
    columns[5] = Languages.safeText(MessageKey.AMOUNT) + " "  + Configurations.currentConfiguration.getBitcoinConfiguration().getBitcoinSymbol();

    // Amount in fiat
    columns[6] = Languages.safeText(MessageKey.AMOUNT) + " " + Configurations.currentConfiguration.getBitcoinConfiguration().getLocalCurrencySymbol();

    // Exchange rate
    columns[7] = Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL);

    // Exchange rate provider
    columns[8] = Languages.safeText(MessageKey.EXCHANGE_RATE_PROVIDER);

    // Miner's fee
    columns[9] = Languages.safeText(MessageKey.TRANSACTION_FEE);

    // coinbase
    columns[10] = Languages.safeText(MessageKey.COINBASE);

    // Transaction hash.
    columns[11] = Languages.safeText(MessageKey.TRANSACTION_HASH);

    return columns;
  }
}

