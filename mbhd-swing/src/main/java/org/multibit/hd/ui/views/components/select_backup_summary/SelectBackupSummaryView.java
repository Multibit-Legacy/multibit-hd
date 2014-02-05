package org.multibit.hd.ui.views.components.select_backup_summary;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.BackupSummary;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>User entry of a wallet backup</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SelectBackupSummaryView extends AbstractComponentView<SelectBackupSummaryModel> implements ActionListener {

  // View components
  private JComboBox<BackupSummary> selectedBackupComboBox;
  private JLabel createdLabel;
  private JLabel descriptionLabel;

  /**
   * @param model The model backing this view
   */
  public SelectBackupSummaryView(SelectBackupSummaryModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    SelectBackupSummaryModel model = getModel().get();

    panel = Panels.newPanel(new MigLayout(
      "insets 0", // Layout
      "[]", // Columns
      "[][][]" // Rows
    ));

    // Provide the initial list
    selectedBackupComboBox = ComboBoxes.newBackupSummaryComboBox(this, model.getBackupSummaries());

    // Create the labels
    createdLabel = Labels.newBlankLabel();
    descriptionLabel = Labels.newBlankLabel();

    // Add to the panel
    panel.add(selectedBackupComboBox, "grow,push,w min:350:,wrap");
    panel.add(createdLabel, "grow,push,wrap");
    panel.add(descriptionLabel, "grow,push,wrap");

    return panel;

  }

  @Override
  public void updateModelFromView() {

    // See the action listener

  }

  @Override
  public void updateViewFromModel() {

    selectedBackupComboBox.removeActionListener(this);
    selectedBackupComboBox.removeAllItems();

    List<BackupSummary> backupSummaries = getModel().get().getBackupSummaries();
    for (BackupSummary backupSummary : backupSummaries) {
      selectedBackupComboBox.addItem(backupSummary);
    }
    selectedBackupComboBox.addActionListener(this);

  }

  /**
   * <p>Handle the change locale action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    BackupSummary selectedBackup = (BackupSummary) source.getSelectedItem();

    if (selectedBackup != null) {

      getModel().get().setValue(selectedBackup);

      // Update the UI with additional info
      createdLabel.setText(Dates.formatSmtpDate(selectedBackup.getCreated()));
      descriptionLabel.setText(selectedBackup.getDescription().or(""));
    }
  }

}


