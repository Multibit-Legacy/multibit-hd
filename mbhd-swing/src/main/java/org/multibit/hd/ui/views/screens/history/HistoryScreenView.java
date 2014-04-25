package org.multibit.hd.ui.views.screens.history;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.events.HistoryChangedEvent;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchModel;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchView;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.components.tables.HistoryTableModel;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.edit_history.EditHistoryState;
import org.multibit.hd.ui.views.wizards.edit_history.EditHistoryWizardModel;
import org.multibit.hd.ui.views.wizards.edit_history.EnterHistoryDetailsMode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the history detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class HistoryScreenView extends AbstractScreenView<HistoryScreenModel> implements ActionListener {

  // View components
  private ModelAndView<EnterSearchModel, EnterSearchView> enterSearchMaV;
  private JComboBox<String> checkSelectorComboBox;

  private JTable historyTable;
  private HistoryTableModel historyTableModel;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public HistoryScreenView(HistoryScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {

    MigLayout layout = new MigLayout(
      Panels.migLayout("fill,insets 10 5 0 0"),
      "[][]push[][]", // Column constraints
      "[shrink][shrink][grow]" // Row constraints
    );

    // Populate the model

    historyTable = Tables.newHistoryTable(getScreenModel().getHistory());
    historyTableModel = (HistoryTableModel) historyTable.getModel();

    JPanel contentPanel = Panels.newPanel(layout);

    // Create view components
    enterSearchMaV = Components.newEnterSearchMaV(getScreen().name());
    checkSelectorComboBox = ComboBoxes.newHistoryCheckboxComboBox(this);

    JButton editButton = Buttons.newEditButton(getEditAction());

    // Detect clicks on the table
    historyTable.addMouseListener(getTableMouseListener());

    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(historyTable);
    scrollPane.setViewportBorder(null);

    // Add to the panel
    contentPanel.add(enterSearchMaV.getView().newComponentPanel(), "span 6,growx,push,wrap");
    contentPanel.add(checkSelectorComboBox, "shrink");
    contentPanel.add(editButton, "shrink");
    contentPanel.add(Labels.newBlankLabel(), "grow,push,wrap"); // Empty label to pack buttons
    contentPanel.add(scrollPane, "span 6,grow,push");

    return contentPanel;
  }

  /**
   * <p>Called when the search box is updated</p>
   *
   * @param event The "component changed" event
   */
  @Subscribe
  public void onComponentChangedEvent(ComponentChangedEvent event) {

    // Check if this event applies to us
    if (event.getPanelName().equals(getScreen().name())) {

      // Check the search MaV model for a query and apply it
      List<HistoryEntry> historyEntries = getScreenModel().filterHistoryByContent(enterSearchMaV.getModel().getValue());

      // Repopulate the table accordingly
      historyTableModel.setHistoryEntries(historyEntries, true);

    }
  }

  /**
   * <p>Handle the transfer of data from the "edit history" wizard</p>
   *
   * @param event The "wizard hide" event
   */
  @Subscribe
  public void onWizardHideEvent(WizardHideEvent event) {

    // Filter other events
    if (!event.getPanelName().equals(EditHistoryState.HISTORY_ENTER_DETAILS.name())) {
      return;
    }
    if (event.isExitCancel()) {
      return;
    }

    // Transfer the data from the wizard model back into the table model
    List<HistoryEntry> historyEntries = ((EditHistoryWizardModel) event.getWizardModel()).getHistoryEntries();

    getScreenModel().getHistoryService().updateHistory(historyEntries);
    getScreenModel().getHistoryService().writeHistory();

    update();

  }

  /**
   * <p>Handle the addition of data as a result of a "history" event</p>
   *
   * @param event The "history changed" event
   */
  @Subscribe
  public void onHistoryChangedEvent(HistoryChangedEvent event) {

    update();

  }

  /**
   * @return The edit history entry action
   */
  private Action getEditAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Get the currently selected history entries
        final List<HistoryEntry> historyEntries = historyTableModel.getHistoryEntriesBySelection(true);

        // Ensure we have at least one contact to work with
        if (!historyEntries.isEmpty()) {

          if (historyEntries.size() == 1) {

            // Fire up a wizard in single mode
            Panels.showLightBox(Wizards.newEditHistoryWizard(historyEntries, EnterHistoryDetailsMode.EDIT_SINGLE).getWizardScreenHolder());

          } else {

            // Fire up a wizard in multi mode
            Panels.showLightBox(Wizards.newEditHistoryWizard(historyEntries, EnterHistoryDetailsMode.EDIT_MULTIPLE).getWizardScreenHolder());

          }

        }

      }
    };
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    // User has selected from the checkboxes so interpret the result
    int checkSelectorIndex = checkSelectorComboBox.getSelectedIndex();

    historyTableModel.updateSelectionCheckboxes(checkSelectorIndex);

  }

  /**
   * @return The table mouse listener
   */
  private MouseAdapter getTableMouseListener() {

    return new MouseAdapter() {

      public void mousePressed(MouseEvent e) {

        if (e.getClickCount() == 1) {

          // Toggle the check mark
          JTable target = (JTable) e.getSource();
          int row = target.getSelectedRow();

          if (row != -1) {

            int modelRow = historyTable.convertRowIndexToModel(row);

            historyTableModel.setSelectionCheckmark(
              modelRow,
              !(boolean) historyTableModel.getValueAt(modelRow, ContactTableModel.CHECKBOX_COLUMN_INDEX)
            );
          }

        }

      }

    };

  }

  private void update() {
    if (historyTable != null) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          // Repopulate the table accordingly
          historyTableModel.setHistoryEntries(getScreenModel().getHistory(),true);
        }
      });
    }
  }

}
