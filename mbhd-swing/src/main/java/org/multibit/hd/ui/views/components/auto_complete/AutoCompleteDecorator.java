package org.multibit.hd.ui.views.components.auto_complete;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

    // Configure the auto complete
    final JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
    textField.addKeyListener(getAutoCompleteKeyListener(comboBox, filter, textField));

    // Prevent mouse or key events showing an empty popup (users get confused)
    applyPreventPopupOnEmpty(comboBox);

  }

  /**
   * @param comboBox  The combo box
   * @param filter    The autocomplete filter
   * @param textField The editor text field
   * @param <T>       The generic type
   *
   * @return The key adapter
   */
  private static <T> KeyAdapter getAutoCompleteKeyListener(final JComboBox<T> comboBox, final AutoCompleteFilter<T> filter, final JTextField textField) {

    return new KeyAdapter() {

      public void keyReleased(KeyEvent ke) {

        // Prevent action keys (navigation etc) from triggering changes
        if (ke.isActionKey() || ke.getKeyCode() == KeyEvent.VK_TAB) {
          return;
        }

        // Must be user key press to be here
        // Invoke a new EDT operation to avoid slowing the keyboard down
        SwingUtilities.invokeLater(new Runnable() {
          @Override
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
    };

  }

  /**
   * <p>Prevent the button from opening the popup menu if there is no content</p>
   */
  private static void applyPreventPopupOnEmpty(final JComboBox comboBox) {

    ComboPopup popup = (ComboPopup) comboBox.getAccessibleContext().getAccessibleChild(0);

    // Track the original listeners
    final MouseListener mouseDelegate = popup.getMouseListener();

    // Wrap the mouse delegate with prevention code
    MouseListener mouseWrapper = new MouseListener() {

      /**
       * @return True if the combo box has some entries
       */
      private boolean isPopupAllowed() {
        return comboBox.getItemCount() > 0;
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (isPopupAllowed()) {
          mouseDelegate.mousePressed(e);
        }
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        mouseDelegate.mouseClicked(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        mouseDelegate.mouseReleased(e);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        mouseDelegate.mouseEntered(e);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        mouseDelegate.mouseExited(e);
      }
    };

    replaceUIMouseListener(comboBox, mouseDelegate, mouseWrapper);
    replaceUIMouseListener(comboBox.getComponent(0), mouseDelegate, mouseWrapper);

  }

  private static void replaceUIMouseListener(Component comboBox, MouseListener delegate, MouseListener wrapper) {

    MouseListener[] listeners = comboBox.getMouseListeners();

    // Remove existing listeners temporarily
    for (int i = listeners.length - 1; i >= 0; i--) {
      comboBox.removeMouseListener(listeners[i]);
    }

    // Add the wrappers in place of the original delegates and replace any existing listeners
    for (MouseListener listener : listeners) {
      if (listener == delegate) {
        comboBox.addMouseListener(wrapper);
      } else {
        comboBox.addMouseListener(listener);
      }
    }

  }

}
