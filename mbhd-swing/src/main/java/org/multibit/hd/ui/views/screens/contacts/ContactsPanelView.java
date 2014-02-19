package org.multibit.hd.ui.views.screens.contacts;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchModel;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchView;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the contact detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactsPanelView extends AbstractScreenView<ContactsPanelModel> implements ActionListener {

  // View components
  private ModelAndView<EnterSearchModel, EnterSearchView> enterSearchMaV;
  private JComboBox<String> checkSelectorComboBox;
  private ContactTableModel contactsTableModel;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public ContactsPanelView(ContactsPanelModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel newScreenViewPanel() {

    MigLayout layout = new MigLayout(
      "fill,insets 10 5 0 0", // Layout constraints
      "[][][][][]push[]", // Column constraints
      "[shrink][shrink][grow]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    // Create view components
    enterSearchMaV = Components.newEnterSearchMaV(getScreen().name());
    checkSelectorComboBox = ComboBoxes.newContactsCheckboxComboBox(this);
    JButton addButton = Buttons.newAddButton(getAddAction());
    JButton editButton = Buttons.newEditButton(getEditAction());
    JButton deleteButton = Buttons.newDeleteButton(getDeleteAction());
    JButton undoButton = Buttons.newUndoButton(getUndoAction());

    // Populate the model

    JTable contactsTable = Tables.newContactsTable(getScreenModel().getContacts());
    contactsTableModel = (ContactTableModel) contactsTable.getModel();

    // Detect clicks on the table
    contactsTable.addMouseListener(getTableMouseListener());

    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(contactsTable);
    scrollPane.setViewportBorder(null);

    // Add to the panel
    contentPanel.add(enterSearchMaV.getView().newComponentPanel(), "span 6,growx,push,wrap");
    contentPanel.add(checkSelectorComboBox, "shrink");
    contentPanel.add(addButton, "shrink");
    contentPanel.add(editButton, "shrink");
    contentPanel.add(deleteButton, "shrink");
    contentPanel.add(undoButton, "shrink");
    contentPanel.add(new JLabel(""), "grow,push,wrap"); // Empty label to pack buttons
    contentPanel.add(scrollPane, "span 6,grow,push");

    return contentPanel;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        enterSearchMaV.getView().requestInitialFocus();
      }
    });

  }

  @Subscribe
  public void onComponentChangedEvent(ComponentChangedEvent event) {

    // Check if this event applies to us
    if (event.getPanelName().equals(getScreen().name())) {

      // Check the search MaV model for a query and apply it
      List<Contact> contacts = getScreenModel().filterContactsByContent(enterSearchMaV.getModel().getValue());

      // Repopulate the table accordingly
      contactsTableModel.populateTableData(contacts);

    }
  }

  /**
   * @return The add contact action
   */
  private Action getAddAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // TODO Show contact edit wizard

      }
    };
  }

  /**
   * @return The edit contact action
   */
  private Action getEditAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // TODO Show contact edit wizard

      }
    };
  }

  /**
   * @return The delete contact action
   */
  private Action getDeleteAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Get the list of selected contacts
        List<Contact> selectedContacts = contactsTableModel.getContactsBySelection(true);

        // Remove them from the table model
        contactsTableModel.removeContacts(selectedContacts);

        // Remove them from the screen model
        getScreenModel().removeAll(selectedContacts);

      }
    };
  }

  /**
   * @return The undo action
   */
  private Action getUndoAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        List<Contact> contacts = getScreenModel().undo();

        contactsTableModel.populateTableData(contacts);

      }
    };
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    // User has selected from the checkboxes so interpret the result
    int checkSelectorIndex = checkSelectorComboBox.getSelectedIndex();

    contactsTableModel.updateSelectionCheckboxes(checkSelectorIndex);

  }

  /**
   * @return The table mouse listener
   */
  private MouseAdapter getTableMouseListener() {
    return new MouseAdapter() {

      public void mousePressed(MouseEvent e) {

        // Check box handling
        if (e.getClickCount() > 0) {
          JTable target = (JTable) e.getSource();
          int row = target.getSelectedRow();

          // Check the check box
          contactsTableModel.setSelectionCheckmark(row, true);

        }

        // Edit requested
        if (e.getClickCount() == 2) {

          JTable target = (JTable) e.getSource();
          int row = target.getSelectedRow();

          // TODO Show edit wizard

        }

      }

    };
  }

}
