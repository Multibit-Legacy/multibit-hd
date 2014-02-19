package org.multibit.hd.ui.views.screens.settings;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class SettingsLanguageScreenView implements ActionListener {


  private JComboBox<String> languageComboBox;

  public SettingsLanguageScreenView() {

  }

  public JPanel initComponents() {

    MigLayout layout = new MigLayout(
      "fillx", // Layout constraints
      "[right]rel[grow,fill]", // Column constraints
      "[]10[]" // Row constraints
    );
    JPanel panel = Panels.newPanel(layout);

    languageComboBox = ComboBoxes.newLanguagesComboBox(this);

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

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Implement this
  }
}
