package org.multibit.hd.ui.views.components.select_backup_summary;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.BackupSummary;
import org.multibit.hd.core.dto.comparators.BackupSummaryDescendingComparator;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Select the backup summary to use</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SelectBackupSummaryModel implements Model<BackupSummary> {

  private BackupSummary selectedBackup;
  private List<BackupSummary> backupSummaries = Lists.newArrayList();

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public SelectBackupSummaryModel(String panelName) {
    this.panelName = panelName;

    // Ensure the "next" button is enabled (so that user can next without a backup
    ViewEvents.fireWizardButtonEnabledEvent(panelName, WizardButton.NEXT, true);
  }

  @Override
  public BackupSummary getValue() {
    return selectedBackup;
  }

  @Override
  public void setValue(BackupSummary value) {
    this.selectedBackup = value;

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
      }
    });
  }

  /**
   * @return The backup summaries to be presented
   */
  public List<BackupSummary> getBackupSummaries() {
    return backupSummaries;
  }

  public void setBackupSummaries(List<BackupSummary> backupSummaries) {

    this.backupSummaries = backupSummaries;

    if (backupSummaries != null) {

      // Sort into descending date order (newest first)
      Collections.sort(backupSummaries, new BackupSummaryDescendingComparator());

      // Initialise the selected value to the first backup summary
      if (!backupSummaries.isEmpty()) {
        selectedBackup = backupSummaries.get(0);
      }

    }
  }
}
