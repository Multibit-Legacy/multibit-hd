package org.multibit.hd.core.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.dto.PaymentRequestData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Convert WalletTableData into single fields for use in a CSV file.
 */
public class PaymentRequestDataEntryConverter implements CSVEntryConverter<PaymentRequestData> {

    DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.UK);


    @Override
    public String[] convertEntry(PaymentRequestData paymentRequestData) {
        String[] columns = new String[4];

        // Date.
        String formattedDate = "";
        if (paymentRequestData.getDate() != null) {
            if (paymentRequestData.getDate().getMillis() != 0) {
                try {
                    formattedDate = dateFormatter.format(paymentRequestData.getDate());
                } catch (IllegalArgumentException iae) {
                    // ok
                }
            }
        }
        columns[0] = formattedDate;

        // Description.
        columns[1] = paymentRequestData.getDescription() == null ? "" : paymentRequestData.getDescription();

        // Amount in BTC.
        columns[2] = paymentRequestData.getAmountBTC() == null ? "" : paymentRequestData.getAmountBTC().toString();

        // Amount in fiat
        columns[3] = "";
        if (paymentRequestData.getAmountFiat() != null && paymentRequestData.getAmountFiat().getAmount() != null) {
          columns[3] =  paymentRequestData.getAmountFiat().getAmount().toString();
        }

        return columns;
    }
}

