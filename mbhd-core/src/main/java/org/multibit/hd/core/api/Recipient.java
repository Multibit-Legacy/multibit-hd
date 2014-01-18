package org.multibit.hd.core.api;

import com.google.common.base.Optional;

/**
 * <p>Value object to provide the following to Contact API:</p>
 * <ul>
 * <li>Recipient details</li>
 * </ul>
 * <p>A recipient is a combination of a Bitcoin address representation (could be a standard address, extended public key, address generator etc)</p>
 *
 * @since 0.0.1
 *  
 */
public class Recipient {

  private final String bitcoinAddress;
  private Optional<Contact> contact = Optional.absent();

  /**
   * @param bitcoinAddress The Bitcoin address representation (address, key, seed etc)
   */
  public Recipient(String bitcoinAddress) {
    this.bitcoinAddress = bitcoinAddress;
  }

  /**
   * @return The address representation (address, key, seed etc)
   */
  public String getBitcoinAddress() {
    return bitcoinAddress;
  }

  /**
   * @return The Contact associated with the recipient
   */
  public Optional<Contact> getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = Optional.fromNullable(contact);
  }

  @Override
  public String toString() {
    return "Recipient{" +
      "bitcoinAddress='" + bitcoinAddress + '\'' +
      ", contact=" + contact.orNull() +
      '}';
  }

  /**
   * @return The Bitcoin address or the contact name if present
   */
  public String getSummary() {

    if (contact.isPresent()) {
      return contact.get().getName();
    }

    return bitcoinAddress;
  }
}
