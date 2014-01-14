package org.multibit.hd.ui.views.components.auto_complete;

import com.google.common.base.Preconditions;

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

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            String enteredText = textField.getText();

            T[] popupItems = filter.update(enteredText);

            if (popupItems.length == 0) {
              comboBox.hidePopup();
            } else {
              comboBox.setModel(new DefaultComboBoxModel<>(popupItems));
              comboBox.setSelectedItem(enteredText);
              comboBox.showPopup();

            }

          }
        });
      }
    });

  }

}
