package org.multibit.hd.core.dto.comparators;

import org.multibit.hd.core.dto.BackupSummary;

import java.util.Comparator;

/**
 * <p>Comparator to provide the following to application:</p>
 * <ul>
 * <li>Sorting by backup summary date descending (newest first)</li>
 * </ul>
 *
 * @since 0.0.5
 *
 */
public class BackupSummaryComparator implements Comparator<BackupSummary> {


  @Override
  public int compare(BackupSummary o1, BackupSummary o2) {

    if (o1 == null && o2 != null) {
      return -1;
    }

    if (o2 == null) {
      return 1;
    }

    if (o1.getCreated() == null && o2.getCreated() != null) {
      return -1;
    }

    if (o2.getCreated() == null) {
      return 1;
    }

    return o1.getCreated().compareTo(o2.getCreated());
  }
}
