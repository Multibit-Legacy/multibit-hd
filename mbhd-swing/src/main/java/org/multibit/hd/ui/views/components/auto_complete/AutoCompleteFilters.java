package org.multibit.hd.ui.views.components.auto_complete;

import com.google.common.base.Strings;
import org.multibit.hd.core.api.Contact;
import org.multibit.hd.core.services.CoreServices;

import java.util.Set;

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
   * @return An auto-complete filter linked to the Contact API
   */
  public static AutoCompleteFilter<Contact> newContactFilter() {

    return new AutoCompleteFilter<Contact>() {

      @Override
      public Contact[] create() {

        Set<Contact> contacts = CoreServices.getContactService().allContacts(1, 10);

        return contacts.toArray(new Contact[contacts.size()]);
      }

      @Override
      public Contact[] update(String fragment) {

        if (Strings.isNullOrEmpty(fragment)) {
          return new Contact[] {};
        }

        Set<Contact> contacts = CoreServices.getContactService().filterContactsByName(1, 10, fragment);

        return contacts.toArray(new Contact[contacts.size()]);
      }
    };

  }

}
