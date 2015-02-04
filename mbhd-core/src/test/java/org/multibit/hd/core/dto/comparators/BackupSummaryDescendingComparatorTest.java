package org.multibit.hd.core.dto.comparators;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.multibit.hd.core.dto.BackupSummary;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.utils.Dates;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class BackupSummaryDescendingComparatorTest {

  @Test
  public void testCompare_OneBeforeTwo() throws Exception {

    BackupSummary summary1 = newBackupSummary();
    summary1.setCreated(Dates.thenUtc(2000,1,1,0,0,1));

    BackupSummary summary2 = newBackupSummary();
    summary2.setCreated(Dates.thenUtc(2000,1,1,0,0,0));

    BackupSummaryDescendingComparator testObject = new BackupSummaryDescendingComparator();

    assertThat(testObject.compare(summary1, summary2)).isEqualTo(-1);

  }

  @Test
  public void testCompare_TwoBeforeOne() throws Exception {

    BackupSummary summary1 = newBackupSummary();
    summary1.setCreated(Dates.thenUtc(2000,1,1,0,0,0));

    BackupSummary summary2 = newBackupSummary();
    summary2.setCreated(Dates.thenUtc(2000,1,1,0,0,1));

    BackupSummaryDescendingComparator testObject = new BackupSummaryDescendingComparator();

    assertThat(testObject.compare(summary1, summary2)).isEqualTo(1);

  }

  @Test
  public void testCompare_SortDescending() throws Exception {

    BackupSummary summary1 = newBackupSummary();
    summary1.setCreated(Dates.thenUtc(2000,1,1,0,0,1));

    BackupSummary summary2 = newBackupSummary();
    summary2.setCreated(Dates.thenUtc(2000,1,1,0,0,0));

    BackupSummary summary3 = newBackupSummary();
    summary3.setCreated(Dates.thenUtc(2000,1,1,0,0,2));

    List<BackupSummary> backupSummaryList = Lists.newArrayList(summary1, summary2, summary3);

    BackupSummaryDescendingComparator testObject = new BackupSummaryDescendingComparator();

    Collections.sort(backupSummaryList, testObject);

    assertThat(backupSummaryList.get(0).getCreated().getSecondOfMinute()).isEqualTo(2);
    assertThat(backupSummaryList.get(1).getCreated().getSecondOfMinute()).isEqualTo(1);
    assertThat(backupSummaryList.get(2).getCreated().getSecondOfMinute()).isEqualTo(0);

  }

  private BackupSummary newBackupSummary() {
    return new BackupSummary(
        new WalletId("23bb865e-161bfefc-3020c418-66bf6f75-7fecdfcc"),
        "Test",
        new File(".")
      );
  }


}