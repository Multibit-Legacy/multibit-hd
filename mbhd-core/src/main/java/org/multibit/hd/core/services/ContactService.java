package org.multibit.hd.core.services;

import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.exceptions.ContactsLoadException;
import org.multibit.hd.core.exceptions.ContactsSaveException;

import java.util.Collection;
import java.util.List;

/**
 *  <p>[Pattern] to provide the following to [related classes]:<br>
 *  <ul>
 *  <li></li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
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

  Contact newContact(String name);

  List<Contact> allContacts();

  List<Contact> filterContactsByContent(String query);

  void addAll(Collection<Contact> selectedContacts);

  void loadContacts() throws ContactsLoadException;

  void removeAll(Collection<Contact> selectedContacts);

  void updateContacts(Collection<Contact> editedContacts);

  void writeContacts() throws ContactsSaveException;

  void addDemoContacts();
}
