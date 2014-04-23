package org.multibit.hd.core.dto.comparators;

import org.multibit.hd.core.dto.Contact;

import java.util.Comparator;

/**
 * <p>Comparator to provide the following to application:</p>
 * <ul>
 * <li>Sorting by contact name</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactNameComparator implements Comparator<Contact> {


  @Override
  public int compare(Contact o1, Contact o2) {

    if (o1 == null && o2 != null) {
      return -1;
    }

    if (o2 == null) {
      return 1;
    }

    if (o1.getName() == null && o2.getName() != null) {
      return -1;
    }

    if (o2.getName() == null) {
      return 1;
    }

    return o1.getName().compareTo(o2.getName());
  }
}
