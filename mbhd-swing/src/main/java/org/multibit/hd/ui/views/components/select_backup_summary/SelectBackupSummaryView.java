package org.multibit.hd.ui.views.components.select_backup_summary;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.BackupSummary;
import org.multibit.hd.ui.MultiBitUI;
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
 */
public class SelectBackupSummaryView extends AbstractComponentView<SelectBackupSummaryModel> implements ActionListener {

  // View components
  private JComboBox<BackupSummary> selectedBackupComboBox;
  private JLabel createdLabel;
  private JLabel nameLabel;

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
    nameLabel = Labels.newBlankLabel();

    // Add to the panel
    panel.add(selectedBackupComboBox, "grow,push," + MultiBitUI.COMBO_BOX_WIDTH_MIG + ",wrap");
    panel.add(createdLabel, "grow,push,wrap");
    panel.add(nameLabel, "grow,push,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    selectedBackupComboBox.requestFocusInWindow();
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

    if (backupSummaries != null) {

      // The combo box provides the sorting
      for (BackupSummary backupSummary : backupSummaries) {
       selectedBackupComboBox.addItem(backupSummary);
      }

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
    final BackupSummary selectedBackup = (BackupSummary) source.getSelectedItem();

    if (selectedBackup != null) {

      getModel().get().setValue(selectedBackup);

    }
  }

}


