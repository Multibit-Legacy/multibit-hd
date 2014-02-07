package org.multibit.hd.core.services;

import com.google.common.collect.Sets;
import org.multibit.hd.core.api.Contact;

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

  /**
   * Reduced visibility constructor to prevent accidental instance creation outside of CoreServices
   */
  ContactService() {

    // Load the contact data
    loadContactData();
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
   * @return A set of all Contacts for the given page
   */
  public Set<Contact> allContacts(int page, int contactsPerPage) {
    return contacts;
  }

  /**
   * @param page            The page number (1-based)
   * @param contactsPerPage The number of contacts per page
   * @param query           The text fragment to match (case-insensitive, anywhere in the name)
   *
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
   * <p>Populate the internal cache of Contacts</p>
   */
  private void loadContactData() {

    // TODO Replace this data with a ContactManager derived from WalletManager

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
