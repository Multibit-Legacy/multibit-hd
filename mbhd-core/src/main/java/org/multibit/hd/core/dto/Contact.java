package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

/**
 * <p>DTO to provide the following to Contact API:</p>
 * <ul>
 * <li>Contact details</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Contact {

  private UUID id;
  private String name;

  private Optional<String> email = Optional.absent();
  private Optional<String> imagePath = Optional.absent();
  private Optional<String> bitcoinAddress = Optional.absent();
  private Optional<String> extendedPublicKey = Optional.absent();
  private Optional<String> notes = Optional.absent();
  private List<String> tags = Lists.newArrayList();

  /**
   * @param id   The unique identifier
   * @param name The first name
   */
  public Contact(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * @return The unique identifier for this contact
   */
  public UUID getId() {
    return id;
  }

  /**
   * @return The first name
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The optional email
   */
  public Optional<String> getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = Optional.fromNullable(email);
  }

  /**
   * @return The optional image file path
   */
  public Optional<String> getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = Optional.fromNullable(imagePath);
  }

  /**
   * @return The Bitcoin address
   */
  public Optional<String> getBitcoinAddress() {
    return bitcoinAddress;
  }

  public void setBitcoinAddress(String bitcoinAddress) {
    this.bitcoinAddress = Optional.fromNullable(bitcoinAddress);
  }

  /**
   * @return The extended public key (HD address generator)
   */
  public Optional<String> getExtendedPublicKey() {
    return extendedPublicKey;
  }

  public void setExtendedPublicKey(String extendedPublicKey) {
    this.extendedPublicKey = Optional.fromNullable(extendedPublicKey);
  }

  /**
   * @return The notes associated with the contact
   */
  public Optional<String> getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = Optional.fromNullable(notes);
  }

  /**
   * @return The tags associated with the contact
   */
  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    Preconditions.checkNotNull(tags, "'tags' must be present");
    this.tags = tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Contact contact = (Contact) o;

    return !(id != null ? !id.equals(contact.id) : contact.id != null);

  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Contact{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", email=" + email +
      ", imagePath=" + imagePath +
      ", bitcoinAddress=****" +
      ", extendedPublicKey=****" +
      ", notes=****" +
      ", tags=****" +
      '}';
  }
}
