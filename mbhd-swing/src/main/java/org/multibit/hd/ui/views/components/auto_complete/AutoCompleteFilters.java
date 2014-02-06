package org.multibit.hd.ui.views.components.auto_complete;

import com.google.common.base.Strings;
import org.multibit.hd.core.api.Contact;
import org.multibit.hd.core.api.Recipient;
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
  public static AutoCompleteFilter<Recipient> newRecipientFilter() {

    return new AutoCompleteFilter<Recipient>() {

      @Override
      public Recipient[] create() {

        Set<Contact> contacts = CoreServices.getContactService().allContacts(1, 10);

        return populateRecipients(contacts);

      }

      @Override
      public Recipient[] update(String fragment) {

        if (Strings.isNullOrEmpty(fragment)) {
          return new Recipient[]{};
        }

        Set<Contact> contacts = CoreServices.getContactService().filterContactsByName(1, 10, fragment);

        return populateRecipients(contacts);
      }

      /**
       *
       * @param contacts The contacts to add to the recipients
       * @return The recipients
       */
      private Recipient[] populateRecipients(Set<Contact> contacts) {

        Recipient[] recipients = new Recipient[contacts.size()];

        int i = 0;
        for (Contact contact : contacts) {
          Recipient recipient = new Recipient(contact.getBitcoinAddress().or(""));
          recipient.setContact(contact);
          recipients[i] = recipient;
          i++;
        }

        return recipients;
      }
    };

  }

}
