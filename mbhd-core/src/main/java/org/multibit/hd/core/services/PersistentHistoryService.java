package org.multibit.hd.core.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.dto.WalletPassword;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.exceptions.EncryptedFileReaderWriterException;
import org.multibit.hd.core.exceptions.HistoryLoadException;
import org.multibit.hd.core.exceptions.HistorySaveException;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.store.HistoryProtobufSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <p>Service to provide the following to application:</p>
 * <ul>
 * <li>CRUD operations on History</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class PersistentHistoryService extends AbstractService implements HistoryService {

  private static final Logger log = LoggerFactory.getLogger(PersistentHistoryService.class);

  /**
   * The in-memory cache of history for the current wallet
   */
  private final Set<HistoryEntry> history = Sets.newHashSet();

  /**
   * The location of the backing writeHistory for the history
   */
  private File backingStoreFile;

  /**
   * The serializer for the backing writeHistory
   */
  private HistoryProtobufSerializer protobufSerializer;

  /**
   * <p>Create a HistoryService for a Wallet with the given walletPassword</p>
   *
   * <p>Reduced visibility constructor to prevent accidental instance creation outside of CoreServices.</p>
   */
  PersistentHistoryService(WalletPassword walletPassword) {

    super();

    Preconditions.checkNotNull(walletPassword, "'walletPassword' must be present");

    // Work out where to store the history for this wallet id.
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    String walletRoot = WalletManager.createWalletRoot(walletPassword.getWalletId());

    File walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);

    File historyDirectory = new File(walletDirectory.getAbsolutePath() + File.separator + HISTORY_DIRECTORY_NAME);
    SecureFiles.verifyOrCreateDirectory(historyDirectory);

    this.backingStoreFile = new File(historyDirectory.getAbsolutePath() + File.separator + HISTORY_DATABASE_NAME);

    initialise((String)walletPassword.getPassword());
  }

  /**
   * <p>Create a History service with the specified File as the backing for writeHistory. (This exists primarily for testing where you just run things in a temporary directory)</p>
   * <p>Reduced visibility constructor to prevent accidental instance creation outside of CoreServices.</p>
   */
  PersistentHistoryService(File backingStoreFile) {

    this.backingStoreFile = backingStoreFile;

    initialise((String)WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletPassword().getPassword());
  }

  private void initialise(String password) {

    protobufSerializer = new HistoryProtobufSerializer();

    // Load the history data from the backing writeHistory if it exists
    if (backingStoreFile.exists()) {
      loadHistory(password);
    }

  }


  @Override
  protected boolean startInternal() {

    Preconditions.checkNotNull(protobufSerializer,"protobufSerializer not present. Have you called initialise()?");

    return true;
  }

  @Override
  protected boolean shutdownNowInternal(ShutdownEvent.ShutdownType shutdownType) {

    protobufSerializer = null;
    backingStoreFile = null;

    return true;
  }

  @Override
  public HistoryEntry newHistoryEntry(String description) {

    log.debug("New history event '{}'", description);

    HistoryEntry historyEntry = new HistoryEntry(UUID.randomUUID(), description);

    history.add(historyEntry);

    return historyEntry;

  }

  @Override
  public List<HistoryEntry> allHistory() {

    return Lists.newArrayList(history);

  }

  @Override
  public List<HistoryEntry> filterHistoryByContent(String query) {

    String lowerQuery = query.toLowerCase();

    List<HistoryEntry> filteredHistory = Lists.newArrayList();

    for (HistoryEntry historyEntry : history) {

      boolean isDescriptionMatched = historyEntry.getDescription().toLowerCase().contains(lowerQuery);
      boolean isNoteMatched = historyEntry.getNotes().or("").toLowerCase().contains(lowerQuery);

      if (isDescriptionMatched || isNoteMatched) {
        filteredHistory.add(historyEntry);
      }
    }

    return filteredHistory;
  }

  @Override
  public void addAll(Collection<HistoryEntry> selectedHistory) {

    history.addAll(selectedHistory);

  }

  @Override
  public void loadHistory(String password) throws HistoryLoadException {

    log.debug("Loading history from '{}'", backingStoreFile.getAbsolutePath());
    try {
      ByteArrayInputStream decryptedInputStream = EncryptedFileReaderWriter.readAndDecrypt(backingStoreFile,
              password,
              WalletManager.scryptSalt(),
              WalletManager.aesInitialisationVector());
      Set<HistoryEntry> loadedHistory = protobufSerializer.readHistoryEntries(decryptedInputStream);
      history.clear();
      history.addAll(loadedHistory);

    } catch (EncryptedFileReaderWriterException e) {
      throw new HistoryLoadException("Could not loadHistory history db '" + backingStoreFile.getAbsolutePath() + "'. Error was '" + e.getMessage() + "'.");
    }
  }

  /**
   * <p>Clear all history data</p>
   * <p>Reduced visibility for testing</p>
   */
  void clear() {
    history.clear();
  }

  @Override
  public void removeAll(Collection<HistoryEntry> selectedHistory) {

    Preconditions.checkNotNull(selectedHistory, "'selectedHistory' must be present");

    log.debug("Removing {} history entries", selectedHistory.size());

    history.removeAll(selectedHistory);

  }

  @Override
  public void updateHistory(Collection<HistoryEntry> editedHistory) {

    Preconditions.checkNotNull(editedHistory, "'editedHistory' must be present");

    log.debug("Updating {} history entries", editedHistory.size());

    for (HistoryEntry editedHistoryEntry : editedHistory) {

      if (!history.contains(editedHistoryEntry)) {

        history.add(editedHistoryEntry);

      }

    }

  }

  @Override
  public void writeHistory() throws HistorySaveException {

    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().isPresent(), "Current wallet summary must be present");
    Preconditions.checkNotNull(protobufSerializer, "'protobufSerializer' must be present");

    log.debug("Writing {} history(s)", history.size());

    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
      protobufSerializer.writeHistoryEntries(history, byteArrayOutputStream);
      EncryptedFileReaderWriter.encryptAndWrite(
        byteArrayOutputStream.toByteArray(),
        WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletPassword().getPassword(),
        backingStoreFile
      );

    } catch (Exception e) {
      throw new HistorySaveException("Could not save history db '" + backingStoreFile.getAbsolutePath() + "'. Error was '" + e.getMessage() + "'.", e);
    }
  }

  /**
   * Provided for test purposes
   */
  /* package */ void addDemoHistory() {

    // Only add the demo history if there are none present
    if (!history.isEmpty()) {
      return;
    }

    // Expect filter inclusion on "1"
    HistoryEntry history1 = newHistoryEntry("Something happened 1");
    history1.setNotes("This is a really long note that should span over several lines when finally rendered to the screen. It began with Alice Capital.");

    HistoryEntry history2 = newHistoryEntry("Something happened 2");
    history2.setNotes("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    // Expect filter inclusion on "1"
    HistoryEntry history3 = newHistoryEntry("Something happened 3");
    history3.setNotes("Charles Capital's note 1\n\nCharles Capital's note 3");

    HistoryEntry history4 = newHistoryEntry("Something happened 4");
    history4.setNotes("Derek Capital's note 2\n\nDerek Capital's note 4");

    newHistoryEntry("Something happened 5");

    newHistoryEntry("Something happened 6");

  }

}
