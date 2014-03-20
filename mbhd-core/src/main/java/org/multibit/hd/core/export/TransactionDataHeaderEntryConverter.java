package org.multibit.hd.core.export;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import org.multibit.hd.core.dto.TransactionData;

/**
 * Create a CSVEntryConverter for the header values in the CSV
 */
public class TransactionDataHeaderEntryConverter implements CSVEntryConverter<TransactionData> {
    @Override
    public String[] convertEntry(TransactionData transactionData) {
        String[] columns = new String[5];

        // Date.
        columns[0] = "Date";

        // Description.
        columns[1] = "Description";

        // Amount in BTC.
        columns[2] = "Amount BTC";

        // Amount in fiat
        columns[3] = "Amount fiat";

        // Transaction hash.
        columns[4] = "Transaction Id";

        return columns;
    }
}

