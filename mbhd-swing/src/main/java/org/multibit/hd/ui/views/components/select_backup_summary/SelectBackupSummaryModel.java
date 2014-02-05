package org.multibit.hd.ui.views.components.select_backup_summary;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.multibit.hd.core.api.BackupSummary;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;

import java.util.List;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Show/hide the seed phrase (initially hidden)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SelectBackupSummaryModel implements Model<BackupSummary> {

  private BackupSummary selectedBackup;
  private List<BackupSummary> backupSummaries= Lists.newArrayList();

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public SelectBackupSummaryModel(String panelName) {
    this.panelName = panelName;
  }

  @Override
  public BackupSummary getValue() {
    return selectedBackup;
  }

  @Override
  public void setValue(BackupSummary value) {
    this.selectedBackup = value;

    // Ensure the "next" button is kept disabled
    ViewEvents.fireWizardComponentModelChangedEvent(panelName, Optional.of(this));

  }

  /**
   * @return The backup summaries to be presented
   */
  public List<BackupSummary> getBackupSummaries() {
    return backupSummaries;
  }

  public void setBackupSummaries(List<BackupSummary> backupSummaries) {
    this.backupSummaries = backupSummaries;
  }
}
