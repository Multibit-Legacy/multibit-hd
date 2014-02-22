package org.multibit.hd.ui.views.wizards.edit_contact;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_tags.EnterTagsModel;
import org.multibit.hd.ui.views.components.enter_tags.EnterTagsView;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.util.List;
import java.util.Set;

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

  private boolean multiEdit;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public EditContactEnterDetailsPanelView(AbstractWizard<EditContactWizardModel> wizard, String panelName, boolean multiEdit) {

    super(wizard.getWizardModel(), panelName, multiEdit ? MessageKey.EDIT_CONTACTS_TITLE : MessageKey.EDIT_CONTACT_TITLE);

    PanelDecorator.addCancelApply(this, wizard);

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
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.EDIT);

    panel.setLayout(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[][]", // Column constraints
      "[][][]" // Row constraints
    ));

    List<Contact> contacts = getWizardModel().getContacts();

    Preconditions.checkState(!contacts.isEmpty(), "'contacts' cannot be empty");

    this.multiEdit = contacts.size() > 1;

    Contact firstContact = getWizardModel().getContacts().get(0);

    name = TextBoxes.newEnterName(multiEdit);
    emailAddress = TextBoxes.newEnterEmailAddress(multiEdit);
    bitcoinAddress = TextBoxes.newEnterBitcoinAddress(multiEdit);
    extendedPublicKey = TextBoxes.newEnterExtendedPublicKey(multiEdit);

    // Always allow non-unique fields
    notes = TextBoxes.newEnterNotes();

    List<String> allNames = Lists.newArrayList();

    // Populate the fields from the model
    if (multiEdit) {

      // Multiple contacts so some fields are not for display

      // Combine all notes from all contacts (with a double line break between each)
      StringBuilder sb = new StringBuilder();
      for (Contact contact : contacts) {
        if (contact.getNotes().isPresent()) {
          sb.append(contact.getNotes().get());
          sb.append("\n\n");
        }
      }
      notes.setText(sb.toString());

      // Combine all tags from all contacts (no duplicates)
      Set<String> allTags = Sets.newHashSet();
      for (Contact contact : contacts) {
        allTags.addAll(contact.getTags());
        allNames.add(contact.getName());
      }

      // Base the tags on all tags
      enterTagsMaV = Components.newEnterTagsMaV(getPanelName(), Lists.newArrayList(allTags));

    } else {

      // Use a single contact
      name.setText(firstContact.getName());
      emailAddress.setText(firstContact.getEmail().or(""));
      bitcoinAddress.setText(firstContact.getBitcoinAddress().or(""));
      extendedPublicKey.setText(firstContact.getExtendedPublicKey().or(""));

      notes.setText(firstContact.getNotes().or(""));

      // Base the tags on first contact tags
      enterTagsMaV = Components.newEnterTagsMaV(getPanelName(), firstContact.getTags());

    }

    if (!multiEdit) {

      // Allow unique contact fields
      panel.add(Labels.newName());
      panel.add(name, "grow,push,wrap");

      panel.add(Labels.newEmailAddress());
      panel.add(emailAddress, "grow,push,wrap");

      panel.add(Labels.newBitcoinAddress());
      panel.add(bitcoinAddress, "grow,push,wrap");

      panel.add(Labels.newExtendedPublicKey());
      panel.add(extendedPublicKey, "grow,push,wrap");

    }

    // Provide a note
    if (multiEdit) {
      panel.add(Labels.newMultiEditNote(), "grow,push,span 2,wrap");

      // Provide a short list of names with ellipsis
      panel.add(Labels.newNames());
      panel.add(TextBoxes.newTruncatedList(allNames, 400), "grow,push,wrap");
    }

    // Tags must be top aligned since it is a tall component
    panel.add(Labels.newTags(), "aligny top");
    panel.add(enterTagsMaV.getView().newComponentPanel(), "growx,aligny top,wrap");

    // Ensure we shrink to avoid scrunching up if no tags are present
    panel.add(Labels.newNotes(), "aligny top");
    panel.add(notes, "shrink,wrap");

    return panel;

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Finish button is always enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        name.requestFocusInWindow();

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

    List<Contact> contacts = getWizardModel().getContacts();
    List<String> tags = enterTagsMaV.getModel().getValue();

    // Update the selected contacts
    for (Contact contact : contacts) {

      contact.setNotes(contact.getNotes().or(""));
      contact.setTags(tags);

      if (!multiEdit) {
        contact.setName(name.getText());
        contact.setEmail(emailAddress.getText());
        contact.setBitcoinAddress(bitcoinAddress.getText());
        contact.setExtendedPublicKey(extendedPublicKey.getText());

        // TODO Support image

      }

    }

  }

}
