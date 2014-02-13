package org.multibit.hd.core.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.StarStyle;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.exceptions.ContactsLoadException;
import org.multibit.hd.core.exceptions.ContactsSaveException;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.store.ContactsProtobufSerializer;
import org.multibit.hd.core.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <p>Service to provide the following to application:</p>
 * <ul>
 * <li>CRUD operations on Contacts</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactService {

  public final static String CONTACTS_DIRECTORY_NAME = "contacts";
  public final static String CONTACTS_DATABASE_NAME = "contacts.db";

  /**
   * The in-memory cache of contacts for the current wallet
   */
  private final Set<Contact> contacts = Sets.newHashSet();

  /**
   * The location of the backing store for the contacts
   */
  private File backingStoreFile;
  /**
   * The serializer for the backing store
   */
  private ContactsProtobufSerializer protobufSerializer;

  /**
   * <p>Create a ContactService for a Wallet with the given walletId</p>
   *
   * <p>Reduced visibility constructor to prevent accidental instance creation outside of CoreServices.</p>
   */
  ContactService(WalletId walletId) {

    Preconditions.checkNotNull(walletId, "'walletId' must be present");

    // Work out where to store the contacts for this wallet id.
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    String walletRoot = WalletManager.createWalletRoot(walletId);
    File walletDirectory = WalletManager.getWalletDirectory(applicationDataDirectory.getAbsolutePath(), walletRoot);
    File contactsDirectory = new File(walletDirectory.getAbsolutePath() + File.separator + CONTACTS_DIRECTORY_NAME);
    FileUtils.createDirectoryIfNecessary(contactsDirectory);

    this.backingStoreFile = new File(contactsDirectory.getAbsolutePath() + File.separator + CONTACTS_DATABASE_NAME);

    initialise();
  }

  /**
   * <p>Create a ContactService with the specified File as the backing store. (This exists primarily for testing where you just run things in a temporary directory)</p>
   * <p>Reduced visibility constructor to prevent accidental instance creation outside of CoreServices.</p>
   */
  ContactService(File backingStoreFile) {

    this.backingStoreFile = backingStoreFile;

    initialise();
  }

  private void initialise() {

    protobufSerializer = new ContactsProtobufSerializer();

    // Load the contact data from the backing store if it exists
    if (backingStoreFile.exists()) {
      load();
    }

  }

  /**
   * <p>Create a new contact and add it to the internal cache</p>
   *
   * @param name A name (normally first name and last name)
   *
   * @return A new contact with a fresh ID
   */
  public Contact newContact(String name) {

    Contact contact = new Contact(UUID.randomUUID(), name);

    contacts.add(contact);

    return contact;

  }

  /**
   * @param page            The page number (1-based)
   * @param contactsPerPage The 1-based number of contacts per page
   *
   * @return A list of all Contacts for the given page
   */
  public List<Contact> allContacts(int page, int contactsPerPage) {
    return Lists.newArrayList(contacts);
  }

  /**
   * @param page            The page number (1-based)
   * @param contactsPerPage The number of contacts per page
   * @param query           The text fragment to match (case-insensitive, anywhere in the name)
   *
   * @return A filtered set of Contacts for the given page and query
   */
  public List<Contact> filterContactsByName(int page, int contactsPerPage, String query) {

    String lowerQuery = query.toLowerCase();

    List<Contact> filteredContacts = Lists.newArrayList();

    for (Contact contact : contacts) {
      if (contact.getName().toLowerCase().contains(lowerQuery)) {
        filteredContacts.add(contact);
      }
    }

    return filteredContacts;
  }

  /**
   * <p>Clear the contact data</p>
   */
  public void clear() {
    contacts.clear();
  }

  /**
   * <p>Populate the internal cache of Contacts from the backing store</p>
   */
  public void load() throws ContactsLoadException {
    try (FileInputStream fis = new FileInputStream(backingStoreFile)) {
      Set<Contact> loadedContacts = protobufSerializer.readContacts(fis);
      contacts.clear();
      contacts.addAll(loadedContacts);
    } catch (IOException e) {
      throw new ContactsLoadException("Could not load contacts db '" + backingStoreFile.getAbsolutePath() + "'. Error was '" + e.getMessage() + "'.");
    }
  }

  /**
   * <p>Save the contact data to the backing store</p>
   */
  public void store() throws ContactsSaveException {
    try (FileOutputStream fos = new FileOutputStream(backingStoreFile)) {
      protobufSerializer.writeContacts(contacts, fos);
    } catch (IOException e) {
      throw new ContactsSaveException("Could not save contacts db '" + backingStoreFile.getAbsolutePath() + "'. Error was '" + e.getMessage() + "'.");
    }
  }

  /**
   * <p>Add some demo contacts to the contacts list</p>
   * <p>Used by ComponentTestBed and tests</p>
   */
  public void addDemoContacts() {

    Contact contact1 = newContact("Alice Capital");
    contact1.setEmail("g.rowe@froot.co.uk");
    contact1.getTags().add("VIP");
    contact1.getTags().add("Family");
    contact1.setStarStyle(StarStyle.EMPTY);

    Contact contact2 = newContact("Bob Capital");
    contact2.setEmail("bob.capital@example.org");
    contact2.getTags().add("VIP");
    contact2.getTags().add("Merchandise");
    contact2.setStarStyle(StarStyle.FILL_1);

    Contact contact3 = newContact("Charles Capital");
    contact3.setEmail("charles.capital@example.org");
    contact3.setStarStyle(StarStyle.FILL_2);

    // No email for Derek
    Contact contact4 = newContact("Derek Capital");
    contact4.getTags().add("Family");
    contact4.setStarStyle(StarStyle.FILL_3);

    Contact contact5 = newContact("alice Lower");
    contact5.setEmail("alice.lower@example.org");

    Contact contact6 = newContact("alicia Lower");
    contact6.setEmail("alicia.lower@example.org");

  }
}
