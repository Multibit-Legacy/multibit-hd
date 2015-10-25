package org.multibit.hd.core.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a single entry within the site
 * @since 0.1.5
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"rel", "href"})
public class AtomLink {

  @XmlAttribute
  private String rel;

  @XmlAttribute
  private String href;

  public AtomLink() {
  }

  /**
   * <p>Indicates the mandatory fields for manual creation</p>
   *
   * @param rel  The link relation (e.g. "self")
   * @param href The URL
   */
  public AtomLink(String rel, String href) {
    this.rel = rel;
    this.href = href;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getRel() {
    return rel;
  }

  public void setRel(String rel) {
    this.rel = rel;
  }
}