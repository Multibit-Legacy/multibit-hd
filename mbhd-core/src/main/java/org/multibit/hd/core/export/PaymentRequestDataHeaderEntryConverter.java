package org.multibit.hd.core.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.dto.PaymentRequestData;

/**
 * Create a CSVEntryConverter for the header values in the CSV
 */
public class PaymentRequestDataHeaderEntryConverter implements CSVEntryConverter<PaymentRequestData> {
  @Override
  public String[] convertEntry(PaymentRequestData paymentRequestData) {
    String[] columns = new String[4];

    // Date.
    columns[0] = "Date";

    // Description.
    columns[1] = "Description";

    // Amount in BTC.
    columns[2] = "Amount BTC";

    // Amount in fiat
    columns[3] = "Amount fiat";

    return columns;
  }
}

