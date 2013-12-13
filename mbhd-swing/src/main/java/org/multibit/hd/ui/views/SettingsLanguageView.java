package org.multibit.hd.ui.views;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *         
 */
public class SettingsLanguageView {


  private JComboBox<String> languageComboBox;

  public SettingsLanguageView() {

  }

  public JPanel initComponents() {

    MigLayout layout = new MigLayout(
      "fillx", // Layout constrains
      "[right]rel[grow,fill]", // Column constraints
      "[]10[]" // Row constraints
    );
    JPanel panel = Panels.newPanel(layout);

    languageComboBox = ComboBoxes.newLanguagesComboBox();

    panel.add(Labels.newSelectLanguageLabel(), "");
    panel.add(languageComboBox, "wrap");

    return panel;
  }

  public JComboBox<String> getLanguageComboBox() {
    return languageComboBox;
  }

  /**
   * @return The currently selected language code
   */
  public String getLanguageCode() {

    return ( (String) languageComboBox.getSelectedItem()).substring(2);

  }
}
