package org.multibit.hd.core.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exceptions.HistoryLoadException;
import org.multibit.hd.core.protobuf.MBHDHistoryProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <p>
 * Serialize and de-serialize history entries to a byte stream containing a
 * <a href="http://code.google.com/apis/protocolbuffers/docs/overview.html">protocol buffer</a>.</p>
 *
 * <p>Protocol buffers are a data interchange format developed by Google with an efficient binary representation, a type safe specification
 * language and compilers that generate code to work with those data structures for many languages. Protocol buffers
 * can have their format evolved over time: conceptually they represent data using (tag, length, value) tuples.</p>
 *
 * <p>The format is defined by the <tt>history.proto</tt> file in the MBHD source distribution.</p>
 *
 * <p>This class is used through its static methods. The most common operations are <code>writeHistory</code> and <code>readHistory</code>, which do
 * the obvious operations on Output/InputStreams. You can use a {@link java.io.ByteArrayInputStream} and equivalent
 * {@link java.io.ByteArrayOutputStream} if byte arrays are preferred. The protocol buffer can also be manipulated
 * in its object form if you'd like to modify the flattened data structure before serialization to binary.</p>
 *
 * <p>Based on the original work by Miron Cuperman for the Bitcoinj project</p>
 */
public class HistoryProtobufSerializer {

  private static final Logger log = LoggerFactory.getLogger(HistoryProtobufSerializer.class);

  /**
   * Formats the given history entries to the given output stream in protocol buffer format.<p>
   */
  public void writeHistoryEntries(Set<HistoryEntry> historyEntries, OutputStream output) throws IOException {

    MBHDHistoryProtos.History historyProto = historyEntriesToProto(historyEntries);
    historyProto.writeTo(output);

  }

  /**
   * Converts the given history entries to the object representation of the protocol buffers. This can be modified, or
   * additional data fields set, before serialization takes place.
   */
  public MBHDHistoryProtos.History historyEntriesToProto(Set<HistoryEntry> historyEntries) {

    MBHDHistoryProtos.History.Builder historyBuilder = MBHDHistoryProtos.History.newBuilder();

    Preconditions.checkNotNull(historyEntries, "HistoryEntry must be specified");

    for (HistoryEntry history : historyEntries) {

      MBHDHistoryProtos.HistoryEntry historyProto = makeHistoryEntryProto(history);
      historyBuilder.addHistoryEntry(historyProto);

    }

    return historyBuilder.build();
  }

  /**
   * <p>Parses a HistoryEntry from the given stream, using the provided HistoryEntry instance to loadHistoryEntry data into.
   * <p>A HistoryEntry db can be unreadable for various reasons, such as inability to open the file, corrupt data, internally
   * inconsistent data, You should always
   * handle {@link org.multibit.hd.core.exceptions.HistoryLoadException} and communicate failure to the user in an appropriate manner.</p>
   *
   * @throws org.multibit.hd.core.exceptions.HistoryLoadException thrown in various error conditions (see description).
   */
  public Set<HistoryEntry> readHistoryEntries(InputStream input) {

    Set<HistoryEntry> historyEntries = Sets.newHashSet();
    try {

      MBHDHistoryProtos.History historyProto = parseToProto(input);

      readHistoryEntry(historyProto, historyEntries);

      return historyEntries;

    } catch (IOException e) {
      ExceptionHandler.handleThrowable(new HistoryLoadException("Could not parse input stream to protobuf", e));
    }

    return historyEntries;
  }

  /**
   * Returns the loaded protocol buffer from the given byte stream. This method is designed for low level work involving the
   * wallet file format itself.
   */
  public static MBHDHistoryProtos.History parseToProto(InputStream input) throws IOException {
    return MBHDHistoryProtos.History.parseFrom(input);
  }

  /**
   * <p>Loads history data from the given protocol buffer and inserts it into the given Set of history entries.</p>
   *
   * <p>A history db can be unreadable for various reasons, such as inability to open the file, corrupt data, internally
   * inconsistent data, an extension marked as mandatory that cannot be handled and so on.</p>
   *
   * <p>You should always handle {@link org.multibit.hd.core.exceptions.HistoryLoadException} and communicate failure to the user in an appropriate manner.</p>
   *
   */
  private void readHistoryEntry(MBHDHistoryProtos.History historyProto, Set<HistoryEntry> historyEntries) {

    Set<HistoryEntry> readHistoryEntry = Sets.newHashSet();

    List<MBHDHistoryProtos.HistoryEntry> historyProtos = historyProto.getHistoryEntryList();

    if (historyProtos != null) {
      for (MBHDHistoryProtos.HistoryEntry historyEntryProto : historyProtos) {

        String idAsString = historyEntryProto.getId();

        UUID id = UUID.fromString(idAsString);
        String description = historyEntryProto.getDescription();
        DateTime created = new DateTime(historyEntryProto.getCreated(), DateTimeZone.UTC);

        HistoryEntry history = new HistoryEntry(id, description, created);
        history.setNotes(historyEntryProto.getNotes());

        readHistoryEntry.add(history);
      }

    }

    // Everything read OK - put the new history entries into the passed in history entry collection
    historyEntries.clear();
    historyEntries.addAll(readHistoryEntry);
  }

  private static MBHDHistoryProtos.HistoryEntry makeHistoryEntryProto(HistoryEntry historyEntry) {

    MBHDHistoryProtos.HistoryEntry.Builder historyBuilder = MBHDHistoryProtos.HistoryEntry.newBuilder();
    historyBuilder.setId(historyEntry.getId().toString());
    historyBuilder.setDescription(historyEntry.getDescription());
    historyBuilder.setCreated(historyEntry.getCreated().getMillis());
    historyBuilder.setNotes(historyEntry.getNotes().or(""));

    return historyBuilder.build();
  }
}
