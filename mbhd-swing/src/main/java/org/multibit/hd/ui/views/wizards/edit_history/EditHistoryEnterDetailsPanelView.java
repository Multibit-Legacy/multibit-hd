package org.multibit.hd.ui.views.wizards.edit_history;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.util.List;
import java.util.Set;

import static org.multibit.hd.ui.views.wizards.edit_history.EnterHistoryDetailsMode.EDIT_MULTIPLE;
import static org.multibit.hd.ui.views.wizards.edit_history.EnterHistoryDetailsMode.NEW;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Edit Contact: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class EditHistoryEnterDetailsPanelView extends AbstractWizardPanelView<EditHistoryWizardModel, EditHistoryEnterDetailsPanelModel> {

  // Panel specific components
  private JTextArea descriptionReadOnly;
  private JTextArea notes;

  private EnterHistoryDetailsMode mode;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   * @param mode      The editing more to use
   */
  public EditHistoryEnterDetailsPanelView(AbstractWizard<EditHistoryWizardModel> wizard, String panelName, EnterHistoryDetailsMode mode) {

    super(wizard, panelName, mode.getMessageKey(), AwesomeIcon.EDIT);

    this.mode = mode;
  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    setPanelModel(new EditHistoryEnterDetailsPanelModel(
      getPanelName()
    ));

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[][][]" // Row constraints
    ));

    List<HistoryEntry> historyEntries = getWizardModel().getHistoryEntries();

    Preconditions.checkState(!historyEntries.isEmpty(), "'historyEntries' cannot be empty");

    // Cannot reference "mode" here due to super constructor sequence
    boolean multiEdit = historyEntries.size() > 1;

    HistoryEntry firstEntry = getWizardModel().getHistoryEntries().get(0);

    descriptionReadOnly = TextBoxes.newTruncatedList(Lists.newArrayList(""), 400);

    // Ensure it is accessible
    AccessibilityDecorator.apply(descriptionReadOnly, MessageKey.DESCRIPTION_READ_ONLY);

    // Always allow non-unique fields
    notes = TextBoxes.newEnterPrivateNotes(getWizardModel());

    Set<String> allDescriptions = Sets.newHashSet();

    // Populate the fields from the model
    if (multiEdit) {

      // Multiple history entries so some fields are not for display

      // Notes are initially empty since concatenating from multiple contacts
      // quickly becomes unmanageable

      // Combine all descriptions from all history entries (no duplicates)
      for (HistoryEntry historyEntry : getWizardModel().getHistoryEntries()) {
        allDescriptions.add(historyEntry.getDescription());
      }

    } else {


      if (firstEntry != null) {
        // Use a single history entry
        notes.setText(firstEntry.getNotes().or("").trim());
        descriptionReadOnly.setText(firstEntry.getDescription());

      } else {
        notes.setText("");
        descriptionReadOnly.setText("");

      }
    }

    // Provide a note
    if (multiEdit) {
      contentPanel.add(Labels.newMultiEditNote(), "grow,push,span 2,wrap");

      // Provide a short list of names with ellipsis
      contentPanel.add(Labels.newDescription(), "aligny top");
      contentPanel.add(TextBoxes.newTruncatedList(allDescriptions, 400), "grow,push,aligny top,wrap");

    } else {

      // Provide a single description
      contentPanel.add(Labels.newDescription());
      contentPanel.add(descriptionReadOnly, "growx,push,wrap");
    }

    // Ensure we grow to avoid scrunching up
    contentPanel.add(Labels.newNotes());
    contentPanel.add(notes, "grow,push,aligny top,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<EditHistoryWizardModel> wizard) {

    PanelDecorator.addCancelApply(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        descriptionReadOnly.requestFocusInWindow();

        // Ensure user overwrites
        if (mode.equals(NEW)) {
          descriptionReadOnly.selectAll();
        }

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Ensure the wizard model correctly reflects the contents of the components
      updateFromComponentModels(Optional.absent());

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Update the selected history entries
    List<HistoryEntry> historyEntries = getWizardModel().getHistoryEntries();
    for (HistoryEntry historyEntry : historyEntries) {

      if (!mode.equals(EDIT_MULTIPLE)) {

        // Notes are not appended
        historyEntry.setNotes(notes.getText().trim());

      } else {

        // Ignore the single item properties

        // Append the given notes to the history items
        String previousNotes = historyEntry.getNotes().or("").trim();
        if (Strings.isNullOrEmpty(previousNotes)) {
          historyEntry.setNotes(notes.getText().trim());
        } else {
          historyEntry.setNotes(previousNotes + "\n\n" + notes.getText().trim());
        }
      }

    }

  }

}
