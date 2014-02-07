package org.multibit.hd.core.services;

import com.google.common.collect.Sets;
import org.multibit.hd.core.api.Contact;
import org.multibit.hd.core.api.WalletId;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.FileUtils;

import java.io.File;
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

  private final Set<Contact> contacts = Sets.newHashSet();

  public final static String CONTACTS_DIRECTORY_NAME = "contacts";
  public final static String CONTACTS_DATABASE_NAME = "contacts.db";

  /**
   * The location of the backing store for the contacts
   */
  private File backingStoreFile;

  /**
   * Create a ContactService for a Wallet with the given walletId
   *
   * Reduced visibility constructor to prevent accidental instance creation outside of CoreServices.
   */
  ContactService(WalletId walletId) {
    // Work out where to store the contacts for this wallet id.
    File applicationDataDirectory = InstallationManager.createApplicationDataDirectory();
    String walletRoot = WalletManager.createWalletRoot(walletId);
    File walletDirectory = WalletManager.getWalletDirectory(applicationDataDirectory.getAbsolutePath(), walletRoot);
    File contactsDirectory = new File(walletDirectory.getAbsolutePath() + File.separator + CONTACTS_DIRECTORY_NAME);
    FileUtils.createDirectoryIfNecessary(contactsDirectory);
    this.backingStoreFile = new File(contactsDirectory.getAbsolutePath() + File.separator + CONTACTS_DATABASE_NAME);

    // Load the contact data from the backing store
    load();
  }


  /**
   * Create a ContactService with the specified File as the backing store.
   * (This exists primarily for testing where you just run things in a temporary directory)
   *
   * Reduced visibility constructor to prevent accidental instance creation outside of CoreServices.
   */
  ContactService(File backingStoreFile) {
    this.backingStoreFile = backingStoreFile;

    // Load the contact data from the backing store
    load();
  }

  /**
   * <p>Create a new contact and add it to the internal cache</p>
   *
   * @param name A name (normally first name and last name)
   * @return A new contact with a fresh ID
   */
  public Contact newContact(String name) {

    Contact contact = new Contact(UUID.randomUUID(), name);

    contacts.add(contact);

    return contact;

  }

  /**
   * @param page            The page number
   * @param contactsPerPage The number of contacts per page
   * @return A set of all Contacts for the given page
   */
  public Set<Contact> allContacts(int page, int contactsPerPage) {
    return contacts;
  }

  /**
   * @param page            The page number
   * @param contactsPerPage The number of contacts per page
   * @param query           The text fragment to match (case-insensitive, anywhere in the name)
   * @return A filtered set of Contacts for the given page and query
   */
  public Set<Contact> filterContactsByName(int page, int contactsPerPage, String query) {

    String lowerQuery = query.toLowerCase();

    Set<Contact> filteredContacts = Sets.newHashSet();

    for (Contact contact : contacts) {
      if (contact.getName().toLowerCase().contains(lowerQuery)) {
        filteredContacts.add(contact);
      }
    }

    return filteredContacts;
  }


  /**
   * Clear the contact data
   */
  public void clear() {
    contacts.clear();
  }

  /**
   * <p>Populate the internal cache of Contacts from the backing store</p>
   */
  public void load() {

  }

  /**
   * <p>Save the contact data to the backing store</p>
   */
  public void store() {

  }

  /**
   * <p>Add some demo contacts to the contacts list</p>
   * This is used mainly for testing.
   */
  void addDemoContacts() {

    Contact contact1 = newContact("Alice Capital");
    contact1.setEmail("alice.capital@example.org");

    Contact contact2 = newContact("Bob Capital");
    contact2.setEmail("bob.capital@example.org");

    Contact contact3 = newContact("Charles Capital");
    contact3.setEmail("charles.capital@example.org");

    // No email for Derek
    Contact contact4 = newContact("Derek Capital");

    Contact contact5 = newContact("alice Lower");
    contact5.setEmail("alice.lower@example.org");

    Contact contact6 = newContact("alicia Lower");
    contact6.setEmail("alicia.lower@example.org");

  }
}
