package org.multibit.hd.ui.views.components.auto_complete;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <p>Decorator to provide the following to combo boxes:</p>
 * <ul>
 * <li>Application of auto-complete behaviour</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class AutoCompleteDecorator {

  private static final Logger log = LoggerFactory.getLogger(AutoCompleteDecorator.class);

  /**
   * Utilities have a private constructor
   */
  private AutoCompleteDecorator() {
  }

  /**
   * <p>Create an auto-complete combo box</p>
   *
   * @param comboBox The combo box to decorate
   */
  public static <T> void apply(final JComboBox<T> comboBox, final AutoCompleteFilter<T> filter) {

    Preconditions.checkNotNull(comboBox, "'comboBox' must be present");

    comboBox.setEditable(true);

    final JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
    textField.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent ke) {

        // Prevent action keys (navigation etc) from triggering changes
        if (ke.isActionKey() || ke.getKeyCode()==KeyEvent.VK_TAB) {
          return;
        }

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            String enteredText = textField.getText();

            // Use the entered text to update the available popup items
            T[] popupItems = filter.update(enteredText);

            if (popupItems.length == 0) {
              // Nothing to show
              comboBox.hidePopup();
            } else {
              // Popup contains items

              // Update the model to reflect the new items (fires setItem() in editor)
              comboBox.setModel(new DefaultComboBoxModel<>(popupItems));

              // Update the selected item with the text to allow edits
              comboBox.setSelectedItem(enteredText);

              // Ensure that the popup is showing
              comboBox.showPopup();

            }

          }
        });
      }
    });

  }

}
