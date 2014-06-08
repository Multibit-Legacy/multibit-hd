package org.multibit.hd.ui.views.wizards.edit_contact;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.gravatar.Gravatars;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_tags.EnterTagsModel;
import org.multibit.hd.ui.views.components.enter_tags.EnterTagsView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
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
  private JLabel imageLabel;

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

    // Configure the panel model
    setPanelModel(new EditContactEnterDetailsPanelModel(
      getPanelName()
    ));

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[]" // Row constraints
    ));

    List<Contact> contacts = getWizardModel().getContacts();

    Preconditions.checkState(!contacts.isEmpty(), "'contacts' cannot be empty");

    // Cannot reference "mode" here due to super constructor sequence
    boolean multiEdit = contacts.size() > 1;

    Contact firstContact = getWizardModel().getContacts().get(0);

    // Start with a "no network" contact image label by overriding the default theme
    imageLabel = Labels.newImageLabel(Optional.<BufferedImage>absent());
    imageLabel.setForeground(Themes.currentTheme.fadedText());

    // Ensure it is accessible
    AccessibilityDecorator.apply(imageLabel, MessageKey.CONTACT_IMAGE);

    name = TextBoxes.newEnterName(getWizardModel(), multiEdit);

    // Email address triggers a Gravatar
    emailAddress = TextBoxes.newEnterEmailAddress(getWizardModel(), multiEdit);
    emailAddress.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        displayContactImage();
      }
    });

    bitcoinAddress = TextBoxes.newEnterBitcoinAddress(getWizardModel(), multiEdit);
    extendedPublicKey = TextBoxes.newEnterExtendedPublicKey(getWizardModel(), multiEdit);

    // Always allow non-unique fields
    notes = TextBoxes.newEnterPrivateNotes(getWizardModel());

    List<String> allNames = Lists.newArrayList();

    // Populate the fields from the model
    if (multiEdit) {

      // Multiple contacts so some fields are not for display

      // Notes are initially empty since concatenating from multiple contacts
      // quickly becomes unmanageable

      List<String> allTags = Lists.newArrayList();
      int contactCount = contacts.size();

      // Gather the list of all tags and names of all contacts (we want duplicates)
      for (Contact contact : contacts) {
        allTags.addAll(contact.getTags());
        allNames.add(contact.getName());
      }

      // Count the occurrences of each tag to identify shared tags
      Multiset<String> tagOccurrences = HashMultiset.create(allTags);

      // Extract those that are common to all contacts
      Set<String> sharedTags = Sets.newHashSet();
      for (String tag : allTags) {
        if (tagOccurrences.count(tag) >= contactCount) {
          sharedTags.add(tag);
        }
      }

      // Base the tags on all tags
      enterTagsMaV = Components.newEnterTagsMaV(getPanelName(), Lists.newArrayList(sharedTags));

    } else {

      if (firstContact != null) {

        // Use a single contact
        name.setText(firstContact.getName() == null ? "" : firstContact.getName().trim());

        // Populate the email address and update the image
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

      // Ensure contact image is available
      displayContactImage();

    }

    if (!multiEdit) {

      // Allow unique contact fields
      contentPanel.add(Labels.newName());
      contentPanel.add(name, "grow,push");

      contentPanel.add(imageLabel, "spany 2,grow,wrap");

      contentPanel.add(Labels.newEmailAddress());
      contentPanel.add(emailAddress, "grow,push,wrap");

      contentPanel.add(Labels.newBitcoinAddress());
      contentPanel.add(bitcoinAddress, "grow,span 2,push,wrap");

      contentPanel.add(Labels.newExtendedPublicKey());
      contentPanel.add(extendedPublicKey, "grow,span 2,push,wrap");

    }

    // Provide a note
    if (multiEdit) {
      contentPanel.add(Labels.newMultiEditNote(), "grow,push,span 2,wrap");

      // Provide a short list of names with ellipsis
      contentPanel.add(Labels.newNames(), "aligny top");
      JTextArea allNamesList = TextBoxes.newTruncatedList(allNames, 400);
      allNamesList.setName(MessageKey.NAMES.getKey());
      contentPanel.add(allNamesList, "grow,push,aligny top,wrap");
    }

    // Tags must be top aligned since it is a tall component
    contentPanel.add(Labels.newTags(), "aligny top");
    contentPanel.add(enterTagsMaV.getView().newComponentPanel(), "growx,span 2,aligny top,wrap");

    // Ensure we shrink to avoid scrunching up if no tags are present
    contentPanel.add(Labels.newNotes(), "aligny top");
    contentPanel.add(notes, "grow,span 2,wrap");

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
  public boolean beforeHide(boolean isExitCancel, ModelAndView... mavs) {

    // Always call super() before hiding
    super.beforeHide(isExitCancel, enterTagsMaV);

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

    List<String> originalTags = enterTagsMaV.getModel().getOriginalTags();
    List<String> newTags = enterTagsMaV.getModel().getNewTags();

    // Determine which tags should be added
    List<String> addToAllTags = Lists.newArrayList();
    for (String newTag : newTags) {

      if (!originalTags.contains(newTag)) {
        // Tag is new
        addToAllTags.add(newTag);
      }

    }

    // Determine which tags should be removed
    List<String> removeFromAllTags = Lists.newArrayList();
    for (String originalTag : originalTags) {

      if (!newTags.contains(originalTag)) {
        // Tag has been removed
        removeFromAllTags.add(originalTag);
      }

    }

    // Update the selected contacts
    List<Contact> contacts = getWizardModel().getContacts();
    for (Contact contact : contacts) {

      // Single edit mode
      if (!mode.equals(EDIT_MULTIPLE)) {

        // Handle the single item properties
        contact.setName(name.getText().trim());
        contact.setEmail(emailAddress.getText().trim());
        contact.setBitcoinAddress(bitcoinAddress.getText().trim());
        contact.setExtendedPublicKey(extendedPublicKey.getText().trim());

        // Notes are not appended
        contact.setNotes(notes.getText().trim());

        // Overwrite existing tags
        contact.setTags(newTags);

      } else {

        // Ignore the single item properties

        // Append the given notes to the contact
        String contactNotes = contact.getNotes().or("").trim();
        if (Strings.isNullOrEmpty(contactNotes)) {
          contact.setNotes(notes.getText().trim());
        } else {
          contact.setNotes(contactNotes + "\n\n" + notes.getText().trim());
        }

        // Adjust the existing tags
        List<String> existingTags = contact.getTags();
        for (String addToAllTag : addToAllTags) {

          if (!existingTags.contains(addToAllTag)) {
            // Add the tag
            existingTags.add(addToAllTag.trim());
          }

        }

        for (String removeFromAllTag : removeFromAllTags) {

          if (existingTags.contains(removeFromAllTag)) {
            // Add the tag
            existingTags.remove(removeFromAllTag);
          }

        }

      }

    }

  }

  /**
   * <p>Display the gravatar of the contact</p>
   */
  private void displayContactImage() {

    // No images in multi-edit mode
    if (EnterContactDetailsMode.EDIT_MULTIPLE.equals(mode)) {
      return;
    }

    // Always attempt a lookup (a blank image will be returned on failure)
    final ListenableFuture<Optional<BufferedImage>> imageFuture = Gravatars.retrieveGravatar(emailAddress.getText());
    Futures.addCallback(imageFuture, new FutureCallback<Optional<BufferedImage>>() {
      public void onSuccess(Optional<BufferedImage> image) {
        if (image.isPresent()) {

          // Apply the rounded corners
          final ImageIcon imageIcon = new ImageIcon(ImageDecorator.applyRoundedCorners(image.get(), MultiBitUI.IMAGE_CORNER_RADIUS));

          // Update the UI
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              imageLabel.setIcon(imageIcon);
            }
          });
        } else {

          // Update the UI to use the "no network" icon
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

              // Use the internal "no network" icon
              final ImageIcon imageIcon = Images.newNoNetworkContactImageIcon();

              imageLabel.setIcon(imageIcon);
            }
          });
        }
      }

      public void onFailure(Throwable thrown) {

        // Update the UI to use the "no network" icon
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {

            // Use the internal "no network" icon
            final ImageIcon imageIcon = Images.newNoNetworkContactImageIcon();

            imageLabel.setIcon(imageIcon);
          }
        });

      }
    });
  }

}
