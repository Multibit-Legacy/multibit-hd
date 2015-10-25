package org.multibit.hd.core.atom;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Set;

/**
 * <p>Value object to provide the following to resources:</p>
 * <ul>
 * <li>Provision of sitemap markup</li>
 * </ul>
 *
 * @since 0.1.5
 *  
 */
@XmlRootElement(name = "feed")
@XmlSeeAlso(AtomEntry.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"title", "subTitle", "atomLinks", "id", "author", "updated", "atomEntries"})
public class AtomFeed {

  @XmlElement
  private String id;

  @XmlElement
  private String title;

  @XmlElement
  private String subTitle;

  @XmlElement
  @XmlJavaTypeAdapter(type=DateTime.class, value=AtomDateTimeAdapter.class)
  private DateTime updated;

  @XmlElement
  private String author;

  @XmlElement(name = "link", namespace = "http://www.w3.org/2005/Atom")
  private Set<AtomLink> atomLinks = Sets.newLinkedHashSet();

  @XmlElement(name = "entry", namespace = "http://www.w3.org/2005/Atom")
  private List<AtomEntry> atomEntries = Lists.newArrayList();

  public AtomFeed() {
  }

  /**
   * <p>Indicates the mandatory fields for manual creation</p>
   *
   * @param id      A URN containing a UUID (e.g. urn:uuid:abcd-efgh etc)
   * @param title   The overall feed title (e.g. "MultiBit blog")
   * @param updated When the feed was last updated
   */
  public AtomFeed(String id, String title, DateTime updated) {
    this.id = id;
    this.title = title;
    this.updated = updated;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }

  public DateTime getUpdated() {
    return updated;
  }

  public void setUpdated(DateTime updated) {
    this.updated = updated;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Set<AtomLink> getAtomLinks() {
    return atomLinks;
  }

  public void setAtomLinks(Set<AtomLink> atomLinks) {
    this.atomLinks = atomLinks;
  }

  public List<AtomEntry> getAtomEntries() {
    return atomEntries;
  }

  public void setAtomEntries(List<AtomEntry> atomEntries) {
    this.atomEntries = atomEntries;
  }
}
