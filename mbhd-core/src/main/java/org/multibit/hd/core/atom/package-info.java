@XmlSchema(
  xmlns = {
    @XmlNs(prefix = "", namespaceURI = "http://www.w3.org/2005/Atom")
  },
  namespace = "http://www.w3.org/2005/Atom",
  elementFormDefault = XmlNsForm.QUALIFIED)
@XmlJavaTypeAdapters({
  @XmlJavaTypeAdapter(type = DateTime.class,
    value = DateTimeJaxbAdapter.class)
})
package org.multibit.hd.core.atom;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;