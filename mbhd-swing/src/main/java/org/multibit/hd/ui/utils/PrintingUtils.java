package org.multibit.hd.ui.utils;

import com.google.common.base.Joiner;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * <p>Utilities to provide the following to application:</p>
 * <ul>
 * <li>Printing information through the Java Printing API</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PrintingUtils {

  private static final Logger log = LoggerFactory.getLogger(PrintingUtils.class);

  /**
   * Utilities have private constructors
   */
  private PrintingUtils() {
  }

  /**
   * <p>Prints a seed phrase after offering up a standard print dialog</p>
   *
   * @param seedPhrase The seed phrase to print
   * @param timestamp  The timestamp
   */
  public static void printSeedPhrase(List<String> seedPhrase, String timestamp) {

    if (!seedPhrase.isEmpty()) {

      String printableSeedPhrase = Joiner.on("\n").join(seedPhrase);

      // Provide some neutral formatting to avoid margins and gutters
      StringBuilder sb = new StringBuilder("\n");
      sb.append("\n\n")
        .append(Languages.safeText(MessageKey.SEED_PRINT_NOTE_1))
        .append("\n")
        .append(Languages.safeText(MessageKey.SEED_PRINT_NOTE_2))
        .append("\n")
        .append(Languages.safeText(MessageKey.SEED_PRINT_NOTE_3))
        .append("\n\n***********************************************")
        .append("\n")
        .append(Languages.safeText(MessageKey.TIMESTAMP))
        .append(": ")
        .append(timestamp)
        .append("\n\n")
        .append(printableSeedPhrase)
        .append("\n\n***********************************************")
        .append("\n\n")
        .append(Languages.safeText(MessageKey.SEED_PRINT_NOTE_4));

      print(new ByteArrayInputStream(sb.toString().getBytes()));

    }
  }

  private static void print(InputStream is) {

    PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();

    DocFlavor flavor = DocFlavor.INPUT_STREAM.PNG;
    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, attributeSet);
    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();

    PrintService service = ServiceUI.printDialog(
      null,
      200, 200,
      printService,
      defaultService,
      flavor,
      attributeSet
    );

    if (service != null) {
      DocPrintJob job = service.createPrintJob();
      DocAttributeSet das = new HashDocAttributeSet();
      Doc doc = new SimpleDoc(is, flavor, das);
      try {
        job.print(doc, attributeSet);
      } catch (PrintException e1) {
        throw new IllegalArgumentException("Printer error:", e1);
      }
    }
  }

}
