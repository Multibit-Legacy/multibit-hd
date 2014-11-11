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
    String[] columns = new String[14];

    // Date
    columns[0] = transactionData.getDate() == null ? "" : dateFormatter.format(transactionData.getDate().toDate());

    // Status
    columns[1] = transactionData.getStatus() == null ? "" : transactionData.getStatus().getStatus().toString();

    // Type
    columns[2] = transactionData.getType() == null ? "" : transactionData.getType().toString();

    // Description
    columns[3] = transactionData.getDescription() == null ? "" : transactionData.getDescription();

    // Note
    columns[4] = transactionData.getNote() == null ? "" : transactionData.getNote();

    // Amount in BTC
    columns[5] = transactionData.getAmountCoin() == null ? "" : transactionData.getAmountCoin().toString();

    // Fiat currency
    columns[6] = "";

    // Fiat amount
    columns[7] = "";
    if (transactionData.getAmountFiat() != null) {
      if (transactionData.getAmountFiat().getCurrency().isPresent()) {
        columns[6] = transactionData.getAmountFiat().getCurrency().get().getCurrencyCode();
      }
      if (transactionData.getAmountFiat().getAmount() != null
        && transactionData.getAmountFiat().getAmount().isPresent()){
          // Ensure we use plain string to avoid "E-05"
          columns[7] = transactionData.getAmountFiat().getAmount().get().stripTrailingZeros().toPlainString();
      }
    }

    // Exchange rate
    columns[8] = "";
    if (transactionData.getAmountFiat() != null
      && transactionData.getAmountFiat().getRate() != null
      && transactionData.getAmountFiat().getRate().isPresent()) {
      columns[8] = transactionData.getAmountFiat().getRate().get();
    }

    // Exchange rate provider
    columns[9] = "";
    if (transactionData.getAmountFiat() != null
      && transactionData.getAmountFiat().getExchangeName() != null
      && transactionData.getAmountFiat().getExchangeName().isPresent()) {
      columns[9] = transactionData.getAmountFiat().getExchangeName().get();
    }

    // Miner's fee
    columns[10] = "";
    if (transactionData.getMiningFee() != null
      && transactionData.getMiningFee().isPresent()) {
      columns[10] = transactionData.getMiningFee().get().toString();
    }

    // Client fee
    columns[11] = "";
    if (transactionData.getClientFee() != null
      && transactionData.getClientFee().isPresent()) {
      columns[11] = transactionData.getClientFee().get().toString();
    }

    // Coinbase
    columns[12] = Boolean.toString(transactionData.isCoinBase());

    // Transaction hash
    columns[13] = transactionData.getTransactionId();
    return columns;
  }
}

