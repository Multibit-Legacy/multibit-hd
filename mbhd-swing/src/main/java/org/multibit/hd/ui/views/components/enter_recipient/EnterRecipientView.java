package org.multibit.hd.ui.views.components.enter_recipient;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Address;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Addresses;
import org.multibit.hd.core.utils.BitcoinNetwork;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.gravatar.Gravatars;
import org.multibit.hd.ui.utils.ClipboardUtils;
import org.multibit.hd.ui.views.components.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
 */
public class EnterRecipientView extends AbstractComponentView<EnterRecipientModel> {

  // View components
  private JComboBox<Recipient> recipientComboBox;
  private JLabel imageLabel;

  // TODO This should be injected
  private ContactService contactService = CoreServices.getCurrentContactService();

  /**
   * @param model The model backing this view
   */
  public EnterRecipientView(EnterRecipientModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    JPanel panel = Panels.newPanel(
      new MigLayout(
        Panels.migXLayout(),
        "[][][][]", // Columns
        "[]" // Rows
      ));

    // Start with an invisible gravatar image label
    imageLabel = Labels.newImageLabel(Optional.<BufferedImage>absent());
    imageLabel.setVisible(false);

    recipientComboBox = ComboBoxes.newRecipientComboBox(contactService, BitcoinNetwork.current().get());

    // Set the recipient before the action listener is added
    if (getModel().get().getRecipient().isPresent()) {
      Recipient recipient = getModel().get().getRecipient().get();
      recipientComboBox.getEditor().setItem(recipient);

      // If the recipient is a contact with an email address, then attempt to show the gravatar
      if (recipient.getContact().isPresent() && recipient.getContact().get().getEmail().isPresent()) {
        displayContactImage(recipient);
      }
    }

    // Bind an action listener to allow instant update of UI to matched contacts
    recipientComboBox.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          updateModelFromView();
        }
      });

    // Extra listener to cater for direct paste into recipientComboBox using ctrl-V
    recipientComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent event) {
        if (event.getKeyChar() == 'v' && event.isMetaDown()) {
          performPaste();

          // Consume the event to stop a double paste
          event.consume();

          // Fire a component change event so that handlers for things like button enabling get to run
          ViewEvents.fireComponentChangedEvent(getModel().get().getPanelName(), getModel());
        }
      }
    });

    panel.add(Labels.newRecipient());
    // Specify minimum width for consistent appearance across contact names and locales
    panel.add(recipientComboBox, "growx," + MultiBitUI.COMBO_BOX_WIDTH_MIG + ",push");
    panel.add(Buttons.newPasteButton(getPasteAction()), "shrink");
    panel.add(imageLabel, "shrink,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {

    recipientComboBox.requestFocusInWindow();

  }

  @Override
  public void updateModelFromView() {

    // The editor maintains the selected or inferred Recipient
    Object editorItem = recipientComboBox.getEditor().getItem();

    // No recipient if no item
    if (editorItem == null) {

      getModel().get().setValue(null);
      return;
    }

    // Check for a valid Recipient
    if (editorItem instanceof Recipient) {

      Recipient editorRecipient = (Recipient) editorItem;

      // Avoid double events triggering calls
      Optional<Recipient> currentRecipient = getModel().get().getRecipient();
      if (currentRecipient.isPresent() && currentRecipient.get().equals(editorRecipient)) {
        return;
      }

      // Update the model
      getModel().get().setValue(editorRecipient);

      // Display a gravatar if we have a contact
      if (editorRecipient.getContact().isPresent()) {
        if (editorRecipient.getContact().get().getEmail().isPresent()) {

          displayContactImage(editorRecipient);

        } else {
          imageLabel.setVisible(false);
        }

      }

    } else {

      // Random text is not a recipient
      getModel().get().setValue(null);
      imageLabel.setVisible(false);

    }

  }

  /**
   * <p>Display the contact image of the recipient</p>
   *
   * @param recipient The recipient (must have an email address)
   */
  private void displayContactImage(Recipient recipient) {

    // We have an email address
    String emailAddress = recipient.getContact().get().getEmail().get();

    final ListenableFuture<Optional<BufferedImage>> imageFuture = Gravatars.retrieveGravatar(emailAddress);
    Futures.addCallback(
      imageFuture, new FutureCallback<Optional<BufferedImage>>() {

        @Override
        public void onSuccess(final Optional<BufferedImage> image) {
          if (image.isPresent()) {

            SwingUtilities.invokeLater(
              new Runnable() {
                @Override
                public void run() {
                  // Apply the rounded corners
                  ImageIcon imageIcon = new ImageIcon(
                    ImageDecorator.applyRoundedCorners(
                      image.get(),
                      MultiBitUI.IMAGE_CORNER_RADIUS)
                  );

                  imageLabel.setIcon(imageIcon);
                  imageLabel.setVisible(true);
                }
              });
          }
        }

        @Override
        public void onFailure(Throwable thrown) {

          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                imageLabel.setVisible(false);
              }
            });
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
      performPaste();
      }
    };
  }

  private void performPaste() {
    Optional<String> pastedText = ClipboardUtils.pasteStringFromClipboard();
    if (pastedText.isPresent()) {
      Optional<Address> address = Addresses.parse(pastedText.get());
      if (address.isPresent()) {
        Recipient recipient = new Recipient(address.get());
        recipientComboBox.getEditor().setItem(recipient);
      } else {
        recipientComboBox.getEditor().setItem(pastedText.get());
      }

      // Clear the gravatar
      imageLabel.setVisible(false);

      updateModelFromView();

    }
  }

}
