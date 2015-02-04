package org.multibit.hd.core.dto;

import org.bitcoinj.core.Address;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

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

  private final Address bitcoinAddress;
  private Optional<Contact> contact = Optional.absent();

  /**
   * @param bitcoinAddress A Bitcoin address is mandatory for a Recipient, optional for a Contact
   */
  public Recipient(Address bitcoinAddress) {

    Preconditions.checkNotNull(bitcoinAddress, "'bitcoinAddress' must be present");

    this.bitcoinAddress = bitcoinAddress;
  }

  /**
   * @return The Bitcoin address
   */
  public Address getBitcoinAddress() {
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
      "bitcoinAddress='" + bitcoinAddress + "'" +
      ", contact=" + contact.orNull() +
      '}';
  }

  /**
   * @return The contact name if present, otherwise the Bitcoin address
   */
  public String getSummary() {

    if (contact.isPresent()) {
      return contact.get().getName();
    }

    // The Base58 representation of this address
    return bitcoinAddress.toString();
  }
}
