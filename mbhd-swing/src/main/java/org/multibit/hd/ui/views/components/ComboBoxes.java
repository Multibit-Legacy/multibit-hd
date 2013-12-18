package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.i18n.Languages;

import javax.swing.*;
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
  public static JComboBox<String> newLanguagesComboBox(ActionListener listener) {

    JComboBox comboBox = new JComboBox<>(Languages.getLanguageNames(true));
    comboBox.addActionListener(listener);

    return comboBox;

  }

}
