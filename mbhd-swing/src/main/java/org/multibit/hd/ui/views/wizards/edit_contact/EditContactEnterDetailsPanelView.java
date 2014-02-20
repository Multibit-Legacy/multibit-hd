package org.multibit.hd.ui.views.wizards.edit_contact;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
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
  private JTextArea tags;
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

    Preconditions.checkState(!contacts.isEmpty(),"'contacts' cannot be empty");

    this.multiEdit = contacts.size() > 1;

    Contact firstContact  = getWizardModel().getContacts().get(0);

    name = TextBoxes.newEnterName(multiEdit);
    emailAddress = TextBoxes.newEnterEmailAddress(multiEdit);
    bitcoinAddress = TextBoxes.newEnterBitcoinAddress(multiEdit);
    extendedPublicKey = TextBoxes.newEnterExtendedPublicKey(multiEdit);

    // Always allow non-unique fields
    notes = TextBoxes.newEnterNotes();
    tags = TextBoxes.newEnterTags();

    // Populate the fields from the model
    if (multiEdit) {

      // Multiple contacts so some fields are not for display
      name.setText(Languages.safeText(MessageKey.MULTIPLE));
      emailAddress.setText(Languages.safeText(MessageKey.MULTIPLE));
      bitcoinAddress.setText(Languages.safeText(MessageKey.MULTIPLE));
      extendedPublicKey.setText(Languages.safeText(MessageKey.MULTIPLE));

      // Merge all notes across all contacts


      // Combine all tags from all contacts
      Set<String> allTags = Sets.newHashSet();
      for (Contact contact: contacts) {
        allTags.addAll(contact.getTags());
      }

      tags.setText(Joiner.on(" ").join(allTags));

    } else {

      // Use a single contact
      name.setText(firstContact.getName());
      emailAddress.setText(firstContact.getEmail().or(""));
      bitcoinAddress.setText(firstContact.getBitcoinAddress().or(""));
      extendedPublicKey.setText(firstContact.getExtendedPublicKey().or(""));

      notes.setText(firstContact.getNotes().or(""));
      tags.setText(Joiner.on(" ").join(firstContact.getTags()));

    }


    // Exclude unique contact fields
    panel.add(Labels.newName());
    panel.add(name, "wrap");

    panel.add(Labels.newEmailAddress());
    panel.add(emailAddress, "wrap");

    panel.add(Labels.newBitcoinAddress());
    panel.add(bitcoinAddress, "wrap");

    panel.add(Labels.newExtendedPublicKey());
    panel.add(extendedPublicKey, "wrap");

    // Always allow non-unique fields
    if (multiEdit) {
      panel.add(Labels.newMultiEditNote(),"span 2,wrap");
    }
    panel.add(Labels.newNotes());
    panel.add(notes, "wrap");

    panel.add(Labels.newTags());
    panel.add(tags, "wrap");

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
        getApplyButton().requestFocusInWindow();
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update since we expose the component models

    // No view events to fire

  }

}
