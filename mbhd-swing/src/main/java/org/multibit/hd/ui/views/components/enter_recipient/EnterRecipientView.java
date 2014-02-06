package org.multibit.hd.ui.views.components.enter_recipient;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.Recipient;
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
  private Optional<Recipient> currentRecipient=Optional.absent();

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

    boolean showGravatar = false;
    if (selectedItem instanceof Recipient) {

      // We have a select from the drop down
      Recipient recipient = (Recipient) selectedItem;

      // Avoid double events triggering calls
      if (currentRecipient.isPresent() && currentRecipient.equals(recipient)) {
        return;
      }
      currentRecipient = Optional.of(recipient);

      getModel().get().setValue(recipient);

      // Display a gravatar if we have a contact
      if (recipient.getContact().isPresent()) {
        if (recipient.getContact().get().getEmail().isPresent()) {

          // We have an email address
          String emailAddress = recipient.getContact().get().getEmail().get();

          Optional<BufferedImage> image = Gravatars.retrieveGravatar(emailAddress);

          if (image.isPresent()) {
            imageLabel.setIcon(new ImageIcon(image.get()));
            imageLabel.setVisible(true);
            showGravatar = true;
          }
        }
      }
    }

    // There is no gravatar to show
    if (!showGravatar) {
      imageLabel.setVisible(false);
    }
  }

  /**
   * @return A new action for pasting the recipient information
   */
  private Action getPasteAction() {
    // Show or hide the seed phrase
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        Optional<String> pastedText = ClipboardUtils.pasteStringFromClipboard();

        if (pastedText.isPresent()) {
          recipientComboBox.getEditor().setItem(pastedText.get());
        }

      }
    };
  }

}
