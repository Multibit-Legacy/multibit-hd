package org.multibit.hd.ui.views.screens.contacts;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.comparators.ContactNameComparator;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchModel;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchView;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactState;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactWizardModel;
import org.multibit.hd.ui.views.wizards.edit_contact.EnterContactDetailsMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.Collections;
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

  private static final Logger log = LoggerFactory.getLogger(ContactsScreenView.class);

  // View components
  private ModelAndView<EnterSearchModel, EnterSearchView> enterSearchMaV;
  private JComboBox<String> checkSelectorComboBox;

  private JTable contactsTable;
  private ContactTableModel contactsTableModel;

  private JButton editButton;

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
  public JPanel initialiseScreenViewPanel() {

    MigLayout layout = new MigLayout(
      Panels.migXYDetailLayout(),
      "[][][][][]push[]", // Column constraints
      "[shrink][shrink][grow]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    // Create view components
    enterSearchMaV = Components.newEnterSearchMaV(getScreen().name());
    checkSelectorComboBox = ComboBoxes.newContactsCheckboxComboBox(this);
    JButton addButton = Buttons.newAddButton(getAddAction());
    editButton = Buttons.newEditButton(getEditAction());
    JButton deleteButton = Buttons.newDeleteButton(getDeleteAction());
    JButton undoButton = Buttons.newUndoButton(getUndoAction());

    // Populate the model
    contactsTable = Tables.newContactsTable(getScreenModel().getContacts(), editButton);
    contactsTableModel = (ContactTableModel) contactsTable.getModel();

    // Detect clicks and keyboard on the table
    contactsTable.addMouseListener(getTableMouseListener());
    contactsTable.addKeyListener(getTableKeyListener());
    contactsTable.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        // User is most likely to want the edit button after losing table focus
        editButton.requestFocusInWindow();
      }
    });

    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(contactsTable);
    scrollPane.setViewportBorder(null);

    // Ensure we maintain the overall theme
    ScrollBarUIDecorator.apply(scrollPane, contactsTable);

    // Add to the panel
    contentPanel.add(enterSearchMaV.getView().newComponentPanel(), "span 6,growx,push,wrap");
    contentPanel.add(checkSelectorComboBox, "shrink");
    contentPanel.add(addButton, "shrink");
    contentPanel.add(editButton, "shrink");
    contentPanel.add(deleteButton, "shrink");
    contentPanel.add(undoButton, "shrink");
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

    if (!isInitialised()) {
      return;
    }

    // Check if this event applies to us
    if (event.getPanelName().equals(getScreen().name())) {

      // Check the search MaV model for a query and apply it
      List<Contact> contacts = getScreenModel().filterContactsByContent(enterSearchMaV.getModel().getValue());

      // Repopulate the table accordingly
      contactsTableModel.setContacts(contacts, true);

    }
  }

  /**
   * <p>Handle the transfer of data from the "edit contact" wizard</p>
   *
   * @param event The "wizard hide" event
   */
  @Subscribe
  public void onWizardHideEvent(final WizardHideEvent event) {

    // Filter other events
    if (!event.getPanelName().equals(EditContactState.EDIT_CONTACT_ENTER_DETAILS.name())) {
      return;
    }
    if (event.isExitCancel()) {
      return;
    }
    if (contactsTableModel == null) {
      return;
    }

    // Transfer the data from the wizard model back into the table model (we may have a new contact)
    List<Contact> contacts = ((EditContactWizardModel) event.getWizardModel()).getContacts();

    getScreenModel().getContactService().updateContacts(contacts);
    getScreenModel().getContactService().writeContacts();

    // Repopulate the table accordingly
    contactsTableModel.setContacts(getScreenModel().getContacts(), true);

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

        Collections.sort(contacts, new ContactNameComparator());

        // Fire up a wizard in new mode
        Panels.showLightBox(Wizards.newEditContactWizard(contacts, EnterContactDetailsMode.NEW).getWizardScreenHolder());

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

          Collections.sort(contacts, new ContactNameComparator());

          if (contacts.size() == 1) {

            // Fire up a wizard in single mode
            Panels.showLightBox(Wizards.newEditContactWizard(contacts, EnterContactDetailsMode.EDIT_SINGLE).getWizardScreenHolder());

          } else {

            // Fire up a wizard in multi mode
            Panels.showLightBox(Wizards.newEditContactWizard(contacts, EnterContactDetailsMode.EDIT_MULTIPLE).getWizardScreenHolder());

          }

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
        contactsTableModel.setContacts(contacts, true);

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

        log.debug("Mouse click");

        if (e.getClickCount() == 1) {

          log.debug("Mouse click 1");

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

        if (e.getClickCount() == 2) {

          log.debug("Mouse click 2");

          // Force select the check mark
          JTable target = (JTable) e.getSource();
          int row = target.getSelectedRow();

          if (row != -1) {

            int modelRow = contactsTable.convertRowIndexToModel(row);

            contactsTableModel.setSelectionCheckmark(
              modelRow,
              true
            );
          }

          editButton.doClick();

        }

      }

    };

  }

  /**
   * @return The table key listener
   */
  private KeyAdapter getTableKeyListener() {

    return new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {

        // Use space for checkbox selection
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

          log.debug("Space pressed");

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
