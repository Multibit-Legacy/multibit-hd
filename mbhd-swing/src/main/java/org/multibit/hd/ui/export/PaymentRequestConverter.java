package org.multibit.hd.ui.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentRequestData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Convert PaymentRequestData into single fields for use in a CSV file.
 */
public class PaymentRequestConverter implements CSVEntryConverter<PaymentRequestData> {

  DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Configurations.currentConfiguration.getLocale());


  @Override
  public String[] convertEntry(PaymentRequestData paymentRequestData) {
    String[] columns = new String[12];

    // Date
    columns[0] = paymentRequestData.getDate() == null ? "" : dateFormatter.format(paymentRequestData.getDate().toDate());

    // Type
    columns[1] = paymentRequestData.getType() == null ? "" : paymentRequestData.getType().toString();

    // UUID
    columns[2] = paymentRequestData.getUuid() == null ? "" : paymentRequestData.getUuid().toString();

    // Description (cannot be null)
    columns[3] = paymentRequestData.getDescription();

    // Note
    columns[4] = paymentRequestData.getNote() == null ? "" : paymentRequestData.getNote();

    // Amount in satoshi
    columns[5] = paymentRequestData.getAmountCoin() == null ? "" : paymentRequestData.getAmountCoin().toString();

    // Fiat currency
    columns[6] = "";

    // Fiat amount
    columns[7] = "";
    if (paymentRequestData.getAmountFiat() != null) {
      if (paymentRequestData.getAmountFiat().getCurrency().isPresent()) {
        columns[6] = paymentRequestData.getAmountFiat().getCurrency().get().getCurrencyCode();
      }
      if (paymentRequestData.getAmountFiat().getAmount() != null
        && paymentRequestData.getAmountFiat().getAmount().isPresent()){
          // Ensure we use plain string to avoid "E-05"
          columns[7] = paymentRequestData.getAmountFiat().getAmount().get().stripTrailingZeros().toPlainString();
      }
    }

    // Exchange rate
    columns[8] = "";
    if (paymentRequestData.getAmountFiat() != null && paymentRequestData.getAmountFiat().getRate() != null) {
      columns[8] = paymentRequestData.getAmountFiat().getRate().or("");
    }

    // Exchange rate provider
    columns[9] = "";
    if (paymentRequestData.getAmountFiat() != null && paymentRequestData.getAmountFiat().getExchangeName() != null) {
      columns[9] = paymentRequestData.getAmountFiat().getExchangeName().or("");
    }

    // Matching transaction hash
    columns[10] = "";
    if (paymentRequestData.getTransactionHashOptional().isPresent()) {
      columns[10] = paymentRequestData.getTransactionHashOptional().get().toString();
    }

    // Identity
    columns[11] = paymentRequestData.getIdentityDisplayName();

    return columns;
  }
}

