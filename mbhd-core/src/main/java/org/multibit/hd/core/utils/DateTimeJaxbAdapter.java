package org.multibit.hd.core.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * <p>JAXB adapter to provide the following to Joda DateTime:</p>
 * <ul>
 * <li>JAXB un/marshalling</li>
 * </ul>
 * <p>All supplied times are in ISO 8509 UTC</p>
 *
 * @since 0.1.5
 *  
 */
public class DateTimeJaxbAdapter extends XmlAdapter<String, DateTime> {

  public DateTime unmarshal(String v) throws Exception {
    return new DateTime(v).withZone(DateTimeZone.UTC);
  }

  public String marshal(DateTime v) throws Exception {
    return v.toString();
  }

}