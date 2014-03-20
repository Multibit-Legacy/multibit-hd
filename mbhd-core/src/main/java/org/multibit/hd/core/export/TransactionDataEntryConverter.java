package org.multibit.hd.core.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.dto.TransactionData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Convert WalletTableData into single fields for use in a CSV file.
 */
public class TransactionDataEntryConverter implements CSVEntryConverter<TransactionData> {

    DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.UK);


    @Override
    public String[] convertEntry(TransactionData transactionData) {
        String[] columns = new String[5];

        // Date.
        String formattedDate = "";
        if (transactionData.getDate() != null) {
            if (transactionData.getDate().getMillis() != 0) {
                try {
                    formattedDate = dateFormatter.format(transactionData.getDate());
                } catch (IllegalArgumentException iae) {
                    // ok
                }
            }
        }
        columns[0] = formattedDate;

        // Description.
        columns[1] = transactionData.getDescription() == null ? "" : transactionData.getDescription();

        // Amount in BTC.
        columns[2] = transactionData.getAmountBTC() == null ? "" : transactionData.getAmountBTC().toString();

        // Amount in fiat
        columns[3] = "";
        if (transactionData.getAmountFiat() != null && transactionData.getAmountFiat().getAmount() != null) {
          columns[3] =  transactionData.getAmountFiat().getAmount().toString();
        }

        // Transaction hash.
        columns[4] = transactionData.getTransactionId();
        return columns;
    }
}

