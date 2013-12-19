package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.i18n.Languages;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.event.ActionListener;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised combo boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ComboBoxes {

  /**
   * Utilities have no public constructor
   */
  private ComboBoxes() {
  }

  /**
   * @param listener The action listener
   * @return A new languages combo box
   */
  @SuppressWarnings("unchecked")
  public static JComboBox<String> newLanguagesComboBox(ActionListener listener) {

    JComboBox<String> comboBox = new JComboBox<>(Languages.getLanguageNames(true));
    comboBox.addActionListener(listener);
    comboBox.setEditable(false);

    // Use a basic renderer to avoid confusing visual artifacts like tick marks
    comboBox.setRenderer(new BasicComboBoxRenderer());

    return comboBox;

  }

}
