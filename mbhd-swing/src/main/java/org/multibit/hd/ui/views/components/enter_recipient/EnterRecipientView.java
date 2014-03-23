package org.multibit.hd.ui.views.components.enter_recipient;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.gravatar.Gravatars;
import org.multibit.hd.ui.utils.ClipboardUtils;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteFilter;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a dual-purpose combo box</li>
 * <li>Support for locating contacts by name</li>
 * <li>Support for entering recipient Bitcoin address representations (address, seed, key etc)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterRecipientView extends AbstractComponentView<EnterRecipientModel> {

  private static final Logger log = LoggerFactory.getLogger(EnterRecipientView.class);

  // View components
  private JComboBox<Recipient> recipientComboBox;
  private JLabel imageLabel;

  /**
   * @param model The model backing this view
   */
  public EnterRecipientView(EnterRecipientModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {


    JPanel panel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(),
      "[][][][]", // Columns
      "[]" // Rows
    ));

    // Start with an invisible gravatar image label
    imageLabel = Labels.newImageLabel(Optional.<BufferedImage>absent());
    imageLabel.setVisible(false);

    AutoCompleteFilter<Recipient> filter = AutoCompleteFilters.newRecipientFilter();

    recipientComboBox = ComboBoxes.newRecipientComboBox(filter);

    // Set the recipient before the action listener is added
    if (getModel().get().getRecipient().isPresent()) {
      Recipient recipient = getModel().get().getRecipient().get();
      recipientComboBox.getEditor().setItem(recipient);

      // If the recipient is a contact with an email address, then attempt to show the gravatar
      if (recipient.getContact().isPresent() && recipient.getContact().get().getEmail().isPresent()) {
        displayGravatar(recipient);
      }
    }

    // Bind a key listener to allow instant update of UI to matched passwords
    recipientComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateModelFromView();
      }
    });


    panel.add(Labels.newRecipient());
    // Specify minimum width for consistent appearance across contact names and locales
    panel.add(recipientComboBox, "growx,width min:350:,push");
    panel.add(Buttons.newPasteButton(getPasteAction()), "shrink");
    panel.add(imageLabel, "shrink,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {

  }

  @Override
  public void updateModelFromView() {

    Object selectedItem = recipientComboBox.getSelectedItem();
    Object editedItem = recipientComboBox.getEditor().getItem();

    // Use pastes in preference to selection
    if (editedItem != null) {
      selectedItem = editedItem;
    }

    Optional<Recipient> currentRecipient = getModel().get().getRecipient();

    if (selectedItem instanceof Recipient) {

      // We have a select from the drop down
      Recipient selectedRecipient = (Recipient) selectedItem;

      // Avoid double events triggering calls
      if (currentRecipient.isPresent() && currentRecipient.get().equals(selectedRecipient)) {
        return;
      }

      // Update the current with the selected
      currentRecipient = Optional.of(selectedRecipient);

      getModel().get().setValue(selectedRecipient);

      // Display a gravatar if we have a contact
      if (selectedRecipient.getContact().isPresent()) {
        if (selectedRecipient.getContact().get().getEmail().isPresent()) {

          displayGravatar(selectedRecipient);

        } else {
          imageLabel.setVisible(false);
        }
      }
    } else {
      // Create a recipient based on the text entry
      currentRecipient = Optional.of(new Recipient((String) selectedItem));
      imageLabel.setVisible(false);
    }

    // Update the model
    getModel().get().setValue(currentRecipient.get());

  }

  /**
   * <p>Display the gravatar of the recipient</p>
   *
   * @param recipient The recipient (must have an email address)F
   */
  private void displayGravatar(Recipient recipient) {
    // We have an email address
    String emailAddress = recipient.getContact().get().getEmail().get();

    final ListenableFuture<Optional<BufferedImage>> imageFuture = Gravatars.retrieveGravatar(emailAddress);
    Futures.addCallback(imageFuture, new FutureCallback<Optional<BufferedImage>>() {
      public void onSuccess(Optional<BufferedImage> image) {
        if (image.isPresent()) {

          // Apply the rounded corners
          ImageIcon imageIcon = new ImageIcon(ImageDecorator.applyRoundedCorners(image.get(), MultiBitUI.IMAGE_CORNER_RADIUS));

          imageLabel.setIcon(imageIcon);
          imageLabel.setVisible(true);
        }
      }

      public void onFailure(Throwable thrown) {
        imageLabel.setVisible(false);
      }
    });
  }

  /**
   * @return A new action for pasting the recipient information
   */
  private Action getPasteAction() {
    // Paste the recipient information
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        Optional<String> pastedText = ClipboardUtils.pasteStringFromClipboard();

        if (pastedText.isPresent()) {
          log.debug("Pasted text :'" + pastedText.get());

          recipientComboBox.getEditor().setItem(pastedText.get());
          updateModelFromView();
        }

      }
    };
  }

}
