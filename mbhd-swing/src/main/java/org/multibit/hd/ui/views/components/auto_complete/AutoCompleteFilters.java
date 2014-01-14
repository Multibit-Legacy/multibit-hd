package org.multibit.hd.ui.views.components.auto_complete;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.multibit.hd.core.api.Contact;
import org.multibit.hd.core.contacts.Contacts;

import java.util.List;

/**
 * <p>Factory to provide the following to views:</p>
 * <ul>
 * <li>Creation of filters for auto-complete combo boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class AutoCompleteFilters {

  /**
   * Utilities have private constructors
   */
  private AutoCompleteFilters() {
  }

  /**
   * @return A simple theme-aware detail panel with the given layout
   */
  public static AutoCompleteFilter<Contact> newContactFilter() {

    return new AutoCompleteFilter<Contact>() {

      final Contact[] contacts = new Contact[]{
        Contacts.newDefault("alice"),
        Contacts.newDefault("alicia"),
        Contacts.newDefault("bob"),
        Contacts.newDefault("bobby"),
        Contacts.newDefault("charles"),
        Contacts.newDefault("mallory"),
        Contacts.newDefault("trent")
      };

      @Override
      public Contact[] create() {
        return contacts;
      }

      @Override
      public Contact[] update(String fragment) {

        List<Contact> filteredContactList = Lists.newArrayList();

        if (Strings.isNullOrEmpty(fragment)) {
          return new Contact[] {};
        }

        for (Contact contact : contacts) {
          if (contact.getName().startsWith(fragment)) {
            filteredContactList.add(contact);
          }
        }

        return filteredContactList.toArray(new Contact[filteredContactList.size()]);
      }
    };

  }

}
