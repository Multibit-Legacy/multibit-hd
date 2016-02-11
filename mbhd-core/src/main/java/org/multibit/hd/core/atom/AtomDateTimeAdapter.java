package org.multibit.hd.core.atom;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * <p>XML adapter to provide the following to Atom feed API:</p>
 * <ul>
 * <li>Correct formatting of DateTime entries</li>
 * </ul>
 *
 * @since 0.1.5
 *  
 */
public class AtomDateTimeAdapter extends XmlAdapter<String, DateTime> {

  // UTC with no millis
  private static final DateTimeFormatter utcDateTimeFormatter = DateTimeFormat
    .forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    .withZone(DateTimeZone.UTC);

  @Override
  public DateTime unmarshal(String v) throws Exception {
    return new DateTime(v);
  }

  @Override
  public String marshal(DateTime v) throws Exception {
    return utcDateTimeFormatter.print(v);
  }

}
