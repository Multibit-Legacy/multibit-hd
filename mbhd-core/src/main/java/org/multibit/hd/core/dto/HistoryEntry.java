package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.multibit.hd.core.utils.Dates;

import java.util.UUID;

/**
 * <p>DTO to provide the following to History API:</p>
 * <ul>
 * <li>Historical entry</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class HistoryEntry {

  private final UUID id;

  private final String description;

  private Optional<String> notes = Optional.absent();

  private final DateTime created;

  /**
   * @param id          The unique identifier
   * @param description The first name
   */
  public HistoryEntry(UUID id, String description) {
    this(id, description, Dates.nowUtc());
  }

  /**
   * <p>The protobuf constructor</p>
   *
   * @param id          The unique identifier
   * @param created     When this entry was created
   * @param description The first name
   */
  public HistoryEntry(UUID id, String description, DateTime created) {

    this.id = id;
    this.description = description;
    this.created = created;

  }

  /**
   * @return The unique identifier for this contact
   */
  public UUID getId() {
    return id;
  }

  /**
   * @return The immutable MultiBit description of what happened
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return The user notes associated with the entry
   */
  public Optional<String> getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = Optional.fromNullable(notes);
  }

  /**
   * @return The date time in UTC when this entry was created
   */
  public DateTime getCreated() {
    return created;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HistoryEntry contact = (HistoryEntry) o;

    return !(id != null ? !id.equals(contact.id) : contact.id != null);

  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "HistoryEntry{" +
      "description='" + description + '\'' +
      ", notes=" + notes +
      ", created=" + created +
      '}';
  }
}
