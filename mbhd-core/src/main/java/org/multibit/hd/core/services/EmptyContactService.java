package org.multibit.hd.core.services;

import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.exceptions.ContactsLoadException;
import org.multibit.hd.core.exceptions.ContactsSaveException;

import java.util.Collection;
import java.util.List;

/**
 *  <p>Class to provide the following to ContactService consumers:</p>
 *  <ul>
 *  <li>An empty contact service i.e no backing store</li>
 *  </ul>
 */
public class EmptyContactService implements ContactService {
  @Override
  public Contact newContact(String name) {
    return null;
  }

  @Override
  public List<Contact> allContacts() {
    return Lists.newArrayList();
  }

  @Override
  public List<Contact> filterContactsByContent(String query) {
    return Lists.newArrayList();
  }

  @Override
  public void addAll(Collection<Contact> selectedContacts) {

  }

  @Override
  public void loadContacts() throws ContactsLoadException {

  }

  @Override
  public void removeAll(Collection<Contact> selectedContacts) {

  }

  @Override
  public void updateContacts(Collection<Contact> editedContacts) {

  }

  @Override
  public void writeContacts() throws ContactsSaveException {

  }

  @Override
  public void addDemoContacts() {

  }
}
