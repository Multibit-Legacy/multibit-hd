package org.multibit.hd.core.api;

import com.google.common.base.Optional;

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

  private Optional<String> email;
  private Optional<String> imagePath;

  /**
   * @param id        The unique identifier
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

  /**
   * @return The optional email
   */
  public Optional<String> getEmail() {
    return email;
  }

  public void setEmail(Optional<String> email) {
    this.email = email;
  }

  /**
   * @return The optional image file path
   */
  public Optional<String> getImagePath() {
    return imagePath;
  }

  public void setImagePath(Optional<String> imagePath) {
    this.imagePath = imagePath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Contact contact = (Contact) o;

    if (id != null ? !id.equals(contact.id) : contact.id != null) return false;

    return true;
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
      '}';
  }
}
