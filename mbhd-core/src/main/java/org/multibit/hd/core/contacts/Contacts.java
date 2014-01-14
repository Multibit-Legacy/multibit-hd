package org.multibit.hd.core.contacts;

import org.multibit.hd.core.api.Contact;

import java.util.UUID;

/**
 * <p>Factory to provide the following to Contact API:</p>
 * <ul>
 * <li>Entry point to configured instances of contacts</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Contacts {

  /**
   * Utilities have a private constructor
   */
  private Contacts() {
  }

  /**
   * @param firstName The first name
   *
   * @return A new contact with random ID
   */
  public static Contact newDefault(String firstName) {

    return new Contact(UUID.randomUUID(), firstName);

  }

}
