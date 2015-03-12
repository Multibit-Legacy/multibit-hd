package org.multibit.hd.core.managers;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.writer.CSVEntryConverter;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.MBHDPaymentRequestData;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ExportPerformedEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * <p>Manager to provide the following to other core classes:</p>
 * <ul>
 * <li>Exporting of transactions and payment requests to CSV files </li>
 * </ul>
 * </p>
 *
 */
public class ExportManager {

  /**
   * The suffix for the CSV files
   */
  private static final String CSV_SUFFIX = ".csv";

  private static final String OPEN_BRACKET = "(";
  private static final String CLOSE_BRACKET = ")";

  public ExportManager() {

  }

  public static void export(final List<PaymentData> paymentDataList, final List<MBHDPaymentRequestData> MBHDPaymentRequestDataList, final File exportDirectory, final String transactionFileStem, final String paymentRequestFileStem,
                            final CSVEntryConverter<MBHDPaymentRequestData> paymentRequestHeaderConverter, final CSVEntryConverter<MBHDPaymentRequestData> paymentRequestConverter,
                            final CSVEntryConverter<TransactionData> transactionHeaderConverter, final CSVEntryConverter<TransactionData> transactionConverter) {
    ExecutorService executorService = SafeExecutors.newSingleThreadExecutor("export");
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        ExportManager.exportInternal(
          paymentDataList,
                MBHDPaymentRequestDataList,
          exportDirectory,
          transactionFileStem,
          paymentRequestFileStem,
          paymentRequestHeaderConverter, paymentRequestConverter,
          transactionHeaderConverter, transactionConverter
        );
      }
    });

  }

  public static void exportInternal(final List<PaymentData> paymentDataList, final List<MBHDPaymentRequestData> MBHDPaymentRequestDataList, final File exportDirectory, final String transactionFileStem, final String paymentRequestFileStem,
                                    final CSVEntryConverter<MBHDPaymentRequestData> paymentRequestHeaderConverter, final CSVEntryConverter<MBHDPaymentRequestData> paymentRequestConverter,
                                    final CSVEntryConverter<TransactionData> transactionHeaderConverter, final CSVEntryConverter<TransactionData> transactionConverter) {
    // Perform the export.
    // On completion this fires an ExportPerformedEvent that you subscribe to to find out what happened
    String[] exportFilenames = calculateExportFilenames(exportDirectory, transactionFileStem, paymentRequestFileStem);

    String transactionsExportFilename = exportDirectory.getAbsolutePath() + File.separator + exportFilenames[0];
    String paymentRequestsExportFilename = exportDirectory.getAbsolutePath() + File.separator + exportFilenames[1];

    // Sort payments and payment requests by date descending.
    Comparator<PaymentData> comparator = new Comparator<PaymentData>() {
      @Override
      public int compare(PaymentData o1, PaymentData o2) {
        if (o1 == null) {
          if (o2 == null) {
            return 0;
          } else {
            return 1;
          }
        } else {
          if (o2 == null) {
            return -1;
          }
        }
        Date d1 = o1.getDate().toDate();
        Date d2 = o2.getDate().toDate();
        if (d1 == null) {
          // Object 1 has missing date.
          return 1;
        }
        if (d2 == null) {
          // Object 2 has missing date.
          return -1;
        }
        long n1 = d1.getTime();
        long n2 = d2.getTime();
        if (n1 == 0) {
          // Object 1 has missing date.
          return 1;
        }
        if (n2 == 0) {
          // Object 2 has missing date.
          return -1;
        }
        if (n1 < n2) {
          return -1;
        } else if (n1 > n2) {
          return 1;
        } else {
          return 0;
        }
      }
    };

    Collections.sort(paymentDataList, Collections.reverseOrder(comparator));
    Collections.sort(MBHDPaymentRequestDataList, Collections.reverseOrder(comparator));

    List<TransactionData> transactionDataList = Lists.newArrayList();
    for (PaymentData paymentData : paymentDataList) {
      if (paymentData instanceof TransactionData) {
        transactionDataList.add((TransactionData) paymentData);
      }
    }

    boolean exportWasSuccessful = true;
    String errorMessage = null;

    // Output transactions
    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(transactionsExportFilename, true), Charsets.UTF_8)) {
      // Write the header row.
      CSVWriter<TransactionData> csvHeaderWriter = new CSVWriterBuilder<TransactionData>(outputStreamWriter).strategy(CSVStrategy.UK_DEFAULT)
        .entryConverter(transactionHeaderConverter).build();

      csvHeaderWriter.write(new TransactionData(null, null, null, null, null, null, null, null, null, null, false, null, null, 0, true));

      // Write the body of the CSV file.
      CSVWriter<TransactionData> csvWriter = new CSVWriterBuilder<TransactionData>(outputStreamWriter).strategy(CSVStrategy.UK_DEFAULT)
        .entryConverter(transactionConverter).build();

      csvWriter.writeAll(transactionDataList);

      // Success
    } catch (RuntimeException | IOException e) {
      exportWasSuccessful = false;
      errorMessage = e.getClass().getCanonicalName() + " " + e.getMessage();
    }

    // Output payment requests
    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(paymentRequestsExportFilename, true), Charsets.UTF_8)) {
      // Write the header row.
      CSVWriter<MBHDPaymentRequestData> csvHeaderWriter = new CSVWriterBuilder<MBHDPaymentRequestData>(outputStreamWriter).strategy(CSVStrategy.UK_DEFAULT)
        .entryConverter(paymentRequestHeaderConverter).build();

      csvHeaderWriter.write(new MBHDPaymentRequestData());

      // Write the body of the CSV file.
      CSVWriter<MBHDPaymentRequestData> csvWriter = new CSVWriterBuilder<MBHDPaymentRequestData>(outputStreamWriter).strategy(CSVStrategy.UK_DEFAULT)
        .entryConverter(paymentRequestConverter).build();

      csvWriter.writeAll(MBHDPaymentRequestDataList);

      // Success
    } catch (RuntimeException | IOException e ) {
      exportWasSuccessful = false;
      errorMessage = e.getClass().getCanonicalName() + " " + e.getMessage();
    }

    if (exportWasSuccessful) {
      CoreEvents.fireExportPerformedEvent(new ExportPerformedEvent(transactionsExportFilename,
        paymentRequestsExportFilename, true, null, null));
    } else {
      CoreEvents.fireExportPerformedEvent(new ExportPerformedEvent(transactionsExportFilename, paymentRequestsExportFilename, false, CoreMessageKey.THE_ERROR_WAS, new String[]{errorMessage}));
    }
  }

  public static String[] calculateExportFilenames(File exportPaymentsLocationFile, String transactionFileStem, String paymentRequestFileStem) {
    String candidate0 = transactionFileStem + CSV_SUFFIX;
    String candidate1 = paymentRequestFileStem + CSV_SUFFIX;

    // If these files don't exist we are done
    if (!((new File(exportPaymentsLocationFile.getAbsolutePath() + File.separator + candidate0)).exists()) &&
      !((new File(exportPaymentsLocationFile.getAbsolutePath() + File.separator + candidate0)).exists())) {
      return new String[]{candidate0, candidate1};
    } else {
      int count = 2;
      // Arbitrary limit just to stop infinite loops
      while (count < 1000) {
        candidate0 = transactionFileStem + OPEN_BRACKET + count + CLOSE_BRACKET + CSV_SUFFIX;
        candidate1 = paymentRequestFileStem + OPEN_BRACKET + count + CLOSE_BRACKET + CSV_SUFFIX;
        if (!((new File(exportPaymentsLocationFile.getAbsolutePath() + File.separator + candidate0)).exists()) &&
          !((new File(exportPaymentsLocationFile.getAbsolutePath() + File.separator + candidate0)).exists())) {
          return new String[]{candidate0, candidate1};
        }
        count++;
      }
    }
    // Should not happen except when arbitrary limit hit
    // TODO throw exception
    return null;
  }

}
