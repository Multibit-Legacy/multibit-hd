package org.multibit.hd.ui.views.wizards.edit_contact;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_tags.EnterTagsModel;
import org.multibit.hd.ui.views.components.enter_tags.EnterTagsView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.util.List;
import java.util.Set;

import static org.multibit.hd.ui.views.wizards.edit_contact.EnterContactDetailsMode.EDIT_MULTIPLE;
import static org.multibit.hd.ui.views.wizards.edit_contact.EnterContactDetailsMode.NEW;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Edit Contact: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class EditContactEnterDetailsPanelView extends AbstractWizardPanelView<EditContactWizardModel, EditContactEnterDetailsPanelModel> {

  // Panel specific components
  private JTextField name;
  private JTextField emailAddress;
  private JTextField bitcoinAddress;
  private JTextField extendedPublicKey;
  private JTextArea notes;
  private ModelAndView<EnterTagsModel, EnterTagsView> enterTagsMaV;

  private EnterContactDetailsMode mode;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   * @param mode      The editing more to use
   */
  public EditContactEnterDetailsPanelView(AbstractWizard<EditContactWizardModel> wizard, String panelName, EnterContactDetailsMode mode) {

    super(wizard, panelName, mode.getMessageKey(), AwesomeIcon.EDIT);

    this.mode = mode;
  }

  @Override
  public void newPanelModel() {

    name = TextBoxes.newEnterLabel();

    // Configure the panel model
    setPanelModel(new EditContactEnterDetailsPanelModel(
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

    List<Contact> contacts = getWizardModel().getContacts();

    Preconditions.checkState(!contacts.isEmpty(), "'contacts' cannot be empty");

    // Cannot reference "mode" here due to super constructor sequence
    boolean multiEdit = contacts.size() > 1;

    Contact firstContact = getWizardModel().getContacts().get(0);

    name = TextBoxes.newEnterName(getWizardModel(), multiEdit);
    emailAddress = TextBoxes.newEnterEmailAddress(getWizardModel(), multiEdit);
    bitcoinAddress = TextBoxes.newEnterBitcoinAddress(getWizardModel(), multiEdit);
    extendedPublicKey = TextBoxes.newEnterExtendedPublicKey(getWizardModel(), multiEdit);

    // Always allow non-unique fields
    notes = TextBoxes.newEnterNotes(getWizardModel());

    List<String> allNames = Lists.newArrayList();

    // Populate the fields from the model
    if (multiEdit) {

      // Multiple contacts so some fields are not for display

      // Notes are initially empty since concatenating from multiple contacts
      // quickly becomes unmanageable

      // Combine all tags from all contacts (no duplicates)
      Set<String> allTags = Sets.newHashSet();
      for (Contact contact : contacts) {
        allTags.addAll(contact.getTags());
        allNames.add(contact.getName());
      }

      // Base the tags on all tags
      enterTagsMaV = Components.newEnterTagsMaV(getPanelName(), Lists.newArrayList(allTags));

    } else {

      if (firstContact != null) {
        // Use a single contact
        name.setText(firstContact.getName() == null ? "" : firstContact.getName().trim());
        emailAddress.setText(firstContact.getEmail().or("").trim());
        bitcoinAddress.setText(firstContact.getBitcoinAddress().or("").trim());
        extendedPublicKey.setText(firstContact.getExtendedPublicKey().or("").trim());
        notes.setText(firstContact.getNotes().or("").trim());

        // Base the tags on first contact tags
        enterTagsMaV = Components.newEnterTagsMaV(getPanelName(), firstContact.getTags() == null ? Lists.<String>newArrayList() : firstContact.getTags());
      } else {
        name.setText("");
        emailAddress.setText("");
        bitcoinAddress.setText("");
        extendedPublicKey.setText("");
        notes.setText("");

        // Empty tags
        enterTagsMaV = Components.newEnterTagsMaV(getPanelName(), Lists.<String>newArrayList());

      }
    }

    if (!multiEdit) {

      // Allow unique contact fields
      contentPanel.add(Labels.newName());
      contentPanel.add(name, "grow,push,wrap");

      contentPanel.add(Labels.newEmailAddress());
      contentPanel.add(emailAddress, "grow,push,wrap");

      contentPanel.add(Labels.newBitcoinAddress());
      contentPanel.add(bitcoinAddress, "grow,push,wrap");

      contentPanel.add(Labels.newExtendedPublicKey());
      contentPanel.add(extendedPublicKey, "grow,push,wrap");

    }

    // Provide a note
    if (multiEdit) {
      contentPanel.add(Labels.newMultiEditNote(), "grow,push,span 2,wrap");

      // Provide a short list of names with ellipsis
      contentPanel.add(Labels.newNames(), "aligny top");
      contentPanel.add(TextBoxes.newTruncatedList(allNames, 400), "grow,push,aligny top,wrap");
    }

    // Tags must be top aligned since it is a tall component
    contentPanel.add(Labels.newTags(), "aligny top");
    contentPanel.add(enterTagsMaV.getView().newComponentPanel(), "growx,aligny top,wrap");

    // Ensure we shrink to avoid scrunching up if no tags are present
    contentPanel.add(Labels.newNotes());
    contentPanel.add(notes, "shrink,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<EditContactWizardModel> wizard) {

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

        name.requestFocusInWindow();

        // Ensure user overwrites
        if (mode.equals(NEW)) {
          name.selectAll();
        }

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Ensure the wizard model correctly reflects the contents of the components
      updateFromComponentModels(Optional.absent());

      // TODO Apply validation to various fields

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    List<String> tags = enterTagsMaV.getModel().getValue();

    // Update the selected contacts
    List<Contact> contacts = getWizardModel().getContacts();
    for (Contact contact : contacts) {

      if (!mode.equals(EDIT_MULTIPLE)) {

        // Handle the single item properties
        contact.setName(name.getText());
        contact.setEmail(emailAddress.getText());
        contact.setBitcoinAddress(bitcoinAddress.getText());
        contact.setExtendedPublicKey(extendedPublicKey.getText());

        // Notes are not appended
        contact.setNotes(notes.getText().trim());

        // TODO Support image

      } else {

        // Ignore the single item properties

        // Append the given notes to the contacts
        String contactNotes = contact.getNotes().or("").trim();
        if (Strings.isNullOrEmpty(contactNotes)) {
          contact.setNotes(notes.getText().trim());
        } else {
          contact.setNotes(contactNotes + "\n\n" + notes.getText().trim());
        }
      }

      // Tags are treated the same
      contact.setTags(tags);

    }

  }

}
