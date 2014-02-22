package org.multibit.hd.ui.views.screens.contacts;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchModel;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchView;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactState;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactWizardModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
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
public class ContactsScreenView extends AbstractScreenView<ContactsScreenModel> implements ActionListener {

  // View components
  private ModelAndView<EnterSearchModel, EnterSearchView> enterSearchMaV;
  private JComboBox<String> checkSelectorComboBox;

  private JTable contactsTable;
  private ContactTableModel contactsTableModel;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public ContactsScreenView(ContactsScreenModel panelModel, Screen screen, MessageKey title) {
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

    // Populate the model

    contactsTable = Tables.newContactsTable(getScreenModel().getContacts());
    contactsTableModel = (ContactTableModel) contactsTable.getModel();

    JPanel contentPanel = Panels.newPanel(layout);

    // Create view components
    enterSearchMaV = Components.newEnterSearchMaV(getScreen().name());
    checkSelectorComboBox = ComboBoxes.newContactsCheckboxComboBox(this);
    JButton addButton = Buttons.newAddButton(getAddAction());
    JButton editButton = Buttons.newEditButton(getEditAction());
    JButton deleteButton = Buttons.newDeleteButton(getDeleteAction());
    JButton undoButton = Buttons.newUndoButton(getUndoAction());

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
      List<Contact> contacts = getScreenModel().filterContactsByContent(enterSearchMaV.getModel().getValue());

      // Repopulate the table accordingly
      contactsTableModel.populateTableData(contacts);

    }
  }

  /**
   * <p>Handle the transfer of data from the "edit contact" wizard</p>
   *
   * @param event The "wizard hide" event
   */
  @Subscribe
  public void onWizardHideEvent(WizardHideEvent event) {

    // Filter other events
    if (!event.getPanelName().equals(EditContactState.ENTER_DETAILS.name())) {
      return;
    }

    // Transfer the data from the wizard model back into the table model (we may have a new contact)
    List<Contact> contacts = ((EditContactWizardModel) event.getWizardModel()).getContacts();

    getScreenModel().getContactService().updateContacts(contacts);
    getScreenModel().getContactService().writeContacts();

    // Repopulate the table accordingly
    contactsTableModel.populateTableData(getScreenModel().getContacts());

  }

  /**
   * @return The add contact action
   */
  private Action getAddAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Get the currently selected contacts
        final List<Contact> contacts = Lists.newArrayList();

        Contact contact = getScreenModel().getContactService().newContact(Languages.safeText(MessageKey.NAME));

        contacts.add(contact);

        // Fire up a wizard
        Panels.showLightBox(Wizards.newEditContactWizard(contacts).getWizardPanel());

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

        // Get the currently selected contacts
        final List<Contact> contacts = contactsTableModel.getContactsBySelection(true);

        // Ensure we have at least one contact to work with
        if (!contacts.isEmpty()) {

          // Fire up a wizard
          Panels.showLightBox(Wizards.newEditContactWizard(contacts).getWizardPanel());

        }

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

        // Get all contacts after undo is applied
        Collection<Contact> contacts = getScreenModel().undo();

        // Clear the current search query to ensure users can see an effect
        enterSearchMaV.getView().clear();

        // Repopulate
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

        if (e.getClickCount() == 1) {

          // Toggle the check mark
          JTable target = (JTable) e.getSource();
          int row = target.getSelectedRow();

          if (row != -1) {

            int modelRow = contactsTable.convertRowIndexToModel(row);

            contactsTableModel.setSelectionCheckmark(
              modelRow,
              !(boolean) contactsTableModel.getValueAt(modelRow, ContactTableModel.CHECKBOX_COLUMN_INDEX)
            );
          }

        }

      }

    };

  }

}
