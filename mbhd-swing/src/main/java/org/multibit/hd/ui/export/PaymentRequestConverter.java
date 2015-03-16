package org.multibit.hd.ui.export;

import com.google.common.base.Joiner;
import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.MBHDPaymentRequestData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Convert WalletTableData into single fields for use in a CSV file.
 */
public class PaymentRequestConverter implements CSVEntryConverter<MBHDPaymentRequestData> {

  DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Configurations.currentConfiguration.getLocale());


  @Override
  public String[] convertEntry(MBHDPaymentRequestData MBHDPaymentRequestData) {
    String[] columns = new String[12];

    // Date
    columns[0] = MBHDPaymentRequestData.getDate() == null ? "" : dateFormatter.format(MBHDPaymentRequestData.getDate().toDate());

    // Type
    columns[1] = MBHDPaymentRequestData.getType() == null ? "" : MBHDPaymentRequestData.getType().toString();

    // Bitcoin address
    columns[2] = MBHDPaymentRequestData.getAddress() == null ? "" : MBHDPaymentRequestData.getAddress().toString();

    // Description (cannot be null)
    columns[3] = MBHDPaymentRequestData.getDescription();

    // QR code label
    columns[4] = MBHDPaymentRequestData.getLabel() == null ? "" : MBHDPaymentRequestData.getLabel();

    // Note
    columns[5] = MBHDPaymentRequestData.getNote() == null ? "" : MBHDPaymentRequestData.getNote();

    // Amount in satoshi
    columns[6] = MBHDPaymentRequestData.getAmountCoin() == null ? "" : MBHDPaymentRequestData.getAmountCoin().toString();

    // Fiat currency
    columns[7] = "";

    // Fiat amount
    columns[8] = "";
    if (MBHDPaymentRequestData.getAmountFiat() != null) {
      if (MBHDPaymentRequestData.getAmountFiat().getCurrency().isPresent()) {
        columns[7] = MBHDPaymentRequestData.getAmountFiat().getCurrency().get().getCurrencyCode();
      }
      if (MBHDPaymentRequestData.getAmountFiat().getAmount() != null
        && MBHDPaymentRequestData.getAmountFiat().getAmount().isPresent()){
          // Ensure we use plain string to avoid "E-05"
          columns[8] = MBHDPaymentRequestData.getAmountFiat().getAmount().get().stripTrailingZeros().toPlainString();
      }
    }

    // Exchange rate
    columns[8] = "";
    if (MBHDPaymentRequestData.getAmountFiat() != null && MBHDPaymentRequestData.getAmountFiat().getRate() != null) {
      columns[8] = MBHDPaymentRequestData.getAmountFiat().getRate().or("");
    }

    // Exchange rate provider
    columns[9] = "";
    if (MBHDPaymentRequestData.getAmountFiat() != null && MBHDPaymentRequestData.getAmountFiat().getExchangeName() != null) {
      columns[9] = MBHDPaymentRequestData.getAmountFiat().getExchangeName().or("");
    }

    // Paid amount in satoshi
    columns[10] = MBHDPaymentRequestData.getPaidAmountCoin() == null ? "" : MBHDPaymentRequestData.getPaidAmountCoin().toString();

    // Funding transactions
    columns[11] = MBHDPaymentRequestData.getPayingTransactionHashes() == null ? "" : Joiner.on(" ").join(MBHDPaymentRequestData.getPayingTransactionHashes());

    return columns;
  }
}

