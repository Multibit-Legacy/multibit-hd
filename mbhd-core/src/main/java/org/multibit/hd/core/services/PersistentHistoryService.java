package org.multibit.hd.core.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exceptions.HistoryLoadException;
import org.multibit.hd.core.exceptions.HistorySaveException;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.store.HistoryProtobufSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class PersistentHistoryService implements HistoryService {

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
   * <p>Create a HistoryService for a Wallet with the given walletId</p>
   *
   * <p>Reduced visibility constructor to prevent accidental instance creation outside of CoreServices.</p>
   */
  PersistentHistoryService(WalletId walletId) {

    Preconditions.checkNotNull(walletId, "'walletId' must be present");

    log.debug("Providing history for wallet ID: '{}'", walletId.toFormattedString());

    // Register for events
    CoreServices.uiEventBus.register(this);

    // Work out where to writeContacts the contacts for this wallet id.
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    String walletRoot = WalletManager.createWalletRoot(walletId);

    File walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);

    File historyDirectory = new File(walletDirectory.getAbsolutePath() + File.separator + HISTORY_DIRECTORY_NAME);
    SecureFiles.verifyOrCreateDirectory(historyDirectory);

    this.backingStoreFile = new File(historyDirectory.getAbsolutePath() + File.separator + HISTORY_DATABASE_NAME);

    initialise();
  }

  /**
   * <p>Create a History service with the specified File as the backing for writeHistory. (This exists primarily for testing where you just run things in a temporary directory)</p>
   * <p>Reduced visibility constructor to prevent accidental instance creation outside of CoreServices.</p>
   */
  PersistentHistoryService(File backingStoreFile) {

    log.debug("Providing history for file: '{}'", backingStoreFile.getAbsolutePath());

    this.backingStoreFile = backingStoreFile;

    initialise();
  }

  private void initialise() {

    protobufSerializer = new HistoryProtobufSerializer();

    // Load the history data from the backing writeHistory if it exists
    if (backingStoreFile.exists()) {
      loadHistory();
    }

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
  public void loadHistory() throws HistoryLoadException {

    log.debug("Loading history from '{}'", backingStoreFile.getAbsolutePath());

    try (FileInputStream fis = new FileInputStream(backingStoreFile)) {

      Set<HistoryEntry> loadedHistory = protobufSerializer.readHistoryEntries(fis);
      history.clear();
      history.addAll(loadedHistory);

    } catch (IOException e ) {
      ExceptionHandler.handleThrowable(new HistoryLoadException("Could not loadHistory history db '" + backingStoreFile.getAbsolutePath() + "'. Error was '" + e.getMessage() + "'."));
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

    log.debug("Writing {} history(s)", history.size());

    try (FileOutputStream fos = new FileOutputStream(backingStoreFile)) {

      protobufSerializer.writeHistoryEntries(history, fos);

    } catch (IOException e) {
      throw new HistorySaveException("Could not save history db '" + backingStoreFile.getAbsolutePath() + "'. Error was '" + e.getMessage() + "'.");
    }
  }

  @Override
  public void addDemoHistory() {

    // Only add the demo history if there are none present
    if (!history.isEmpty()) {
      return;
    }

    HistoryEntry history1 = newHistoryEntry("Something happened 1");
    history1.setNotes("This is a really long note that should span over several lines when finally rendered to the screen. It began with Alice Capital.");

    HistoryEntry history2 = newHistoryEntry("Something happened 2");
    history2.setNotes("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    HistoryEntry history3 = newHistoryEntry("Something happened 2");
    history2.setNotes("Charles Capital's note 1\n\nCharles Capital's note 2");

    // No email for Derek
    HistoryEntry history4 = newHistoryEntry("Something happened 2");
    history2.setNotes("Derek Capital's note 1\n\nDerek Capital's note 2");

    HistoryEntry history5 = newHistoryEntry("Something happened 2");

    HistoryEntry history6 = newHistoryEntry("Something happened 2");

  }

}
