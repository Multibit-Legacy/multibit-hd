package org.multibit.hd.core.services;

import com.google.bitcoin.core.Address;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.exceptions.ContactsLoadException;
import org.multibit.hd.core.exceptions.ContactsSaveException;

import java.util.Collection;
import java.util.List;

/**
 *  <p>Interface to provide the following to Contact API:<br>
 *  <ul>
 *  <li>Common methods for contact data access</li>
 *  </ul>
 *  </p>
 *  
 */
public interface ContactService {
  /**
   * The name of the directory (within the wallets directory) that contains the contacts database
   */
  String CONTACTS_DIRECTORY_NAME = "contacts";

  /**
   * The name of the contacts database
   */
  String CONTACTS_DATABASE_NAME = "contacts.db";

  /**
   * @param name The mandatory name of the contact
   *
   * @return A new contact
   */
  Contact newContact(String name);

  /**
   * @return All the contacts
   */
  List<Contact> allContacts();

  /**
   * @param address The Bitcoin address to query on
   *
   * @return A filtered set of Contacts for the given query
   */
  List<Contact> filterContactsByBitcoinAddress(Address address);

  /**
   * @param query The text to match across all fields (name, tags, notes etc)
   *
   * @return Any matching contacts
   */
  List<Contact> filterContactsByContent(String query);

  /**
   * @param selectedContacts The selected contacts to add to the store
   */
  void addAll(Collection<Contact> selectedContacts);

  /**
   * <p>Load the contacts from the store</p>
   *
   * @throws ContactsLoadException If something goes wrong
   */
  void loadContacts() throws ContactsLoadException;

  /**
   * @param selectedContacts The selected contacts to remove from the store
   */
  void removeAll(Collection<Contact> selectedContacts);

  /**
   * <p>Update the store with the edited contacts</p>
   *
   * @param editedContacts The edited contacts
   */
  void updateContacts(Collection<Contact> editedContacts);

  /**
   * <p>Write the contacts to the store</p>
   *
   * @throws ContactsSaveException If something goes wrong
   */
  void writeContacts() throws ContactsSaveException;

  /**
   * <p>Create some demonstration contacts for testing purposes</p>
   */
  void addDemoContacts();
}
