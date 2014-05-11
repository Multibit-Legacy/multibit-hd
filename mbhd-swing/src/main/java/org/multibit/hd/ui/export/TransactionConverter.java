package org.multibit.hd.ui.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.TransactionData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Convert WalletTableData into single fields for use in a CSV file.
 */
public class TransactionConverter implements CSVEntryConverter<TransactionData> {

  DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Configurations.currentConfiguration.getLocale());

  @Override
  public String[] convertEntry(TransactionData transactionData) {
    String[] columns = new String[12];

    // Date.
    columns[0] = transactionData.getDate() == null ? "" : dateFormatter.format(transactionData.getDate().toDate());

    // Status
    columns[1] = transactionData.getStatus() == null ? "" : transactionData.getStatus().getStatus().toString();

    // Type
    columns[2] = transactionData.getType() == null ? "" : transactionData.getType().toString();

    // Description.
    columns[3] = transactionData.getDescription() == null ? "" : transactionData.getDescription();

    // Note
    columns[4] = transactionData.getNote() == null ? "" : transactionData.getNote();

    // Amount in BTC.
    columns[5] = transactionData.getAmountBTC() == null ? "" : transactionData.getAmountBTC().toString();

    // Amount in fiat
    columns[6] = "";
    if (transactionData.getAmountFiat() != null && transactionData.getAmountFiat().getAmount() != null) {
      columns[6] = transactionData.getAmountFiat().getAmount().toString();
    }

    // Exchange rate
    columns[7] = "";
    if (transactionData.getAmountFiat() != null && transactionData.getAmountFiat().getRate() != null) {
      columns[7] = transactionData.getAmountFiat().getRate().or("");
    }

    // Exchange rate provider
    columns[8] = "";
    if (transactionData.getAmountFiat() != null && transactionData.getAmountFiat().getExchangeName() != null) {
      columns[8] = transactionData.getAmountFiat().getExchangeName().or("");
    }

    // Miner's fee
    columns[9] = transactionData.getFeeOnSendBTC() == null || !(transactionData.getFeeOnSendBTC().isPresent())
            ? "" : transactionData.getFeeOnSendBTC().get().toString();

    // Coinbase
    columns[10] = Boolean.toString(transactionData.isCoinBase());

    // Transaction hash.
    columns[11] = transactionData.getTransactionId();
    return columns;
  }
}

