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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a dual-purpose combobox</li>
 * <li>Support for locating contacts by name</li>
 * <li>Support for entering recipient Bitcoin address representations (address, seed, key etc)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterRecipientView extends AbstractComponentView<EnterRecipientModel> {

  // View components
  private JComboBox<Recipient> recipientComboBox;
  private JLabel imageLabel;
  private JButton pasteButton;

  /**
   * @param model The model backing this view
   */
  public EnterRecipientView(EnterRecipientModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    AutoCompleteFilter<Recipient> filter = AutoCompleteFilters.newRecipientFilter();

    // Bind a key listener to allow instant update of UI to matched passwords
    recipientComboBox = ComboBoxes.newRecipientComboBox(filter);
    recipientComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateModelFromView();
      }
    });

    pasteButton = Buttons.newPasteButton(getPasteAction());

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[][][][]", // Columns
      "[]" // Rows
    ));

    // Start with an invisible label
    imageLabel = Labels.newImageLabel(Optional.<BufferedImage>absent());
    imageLabel.setVisible(false);

    panel.add(Labels.newRecipient());
    // Specify minimum width for consistent appearance across contact names and locales
    panel.add(recipientComboBox, "growx,w min:350:,push");
    panel.add(pasteButton, "shrink");
    panel.add(imageLabel, "shrink,wrap");

    return panel;

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

          // We have an email address
          String emailAddress = selectedRecipient.getContact().get().getEmail().get();

          final ListenableFuture<Optional<BufferedImage>> imageFuture = Gravatars.retrieveGravatar(emailAddress);
          Futures.addCallback(imageFuture, new FutureCallback<Optional<BufferedImage>>() {

            // we want this handler to run immediately after we push the big red button!
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
   * @return A new action for pasting the recipient information
   */
  private Action getPasteAction() {
    // Paste the recipient information
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        Optional<String> pastedText = ClipboardUtils.pasteStringFromClipboard();

        if (pastedText.isPresent()) {

          recipientComboBox.getEditor().setItem(pastedText.get());
          updateModelFromView();
        }

      }
    };
  }

}
