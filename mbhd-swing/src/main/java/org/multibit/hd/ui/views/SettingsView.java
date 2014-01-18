package org.multibit.hd.ui.views;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.controllers.SettingsController;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.detail_views.SettingsLanguageView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;

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
public class SettingsView {

  private JButton applyButton;
  private JButton undoButton;

  private SettingsLanguageView settingsLanguageView = new SettingsLanguageView();

  private final SettingsController controller;

  public SettingsView(SettingsController controller) {

    this.controller = controller;

  }

  public JPanel initComponents() {

    MigLayout settingsLayout = new MigLayout(
      "fill", // Layout constraints
      "[]", // Column constraints
      "[grow]10[shrink]" // Row constraints
    );
    JPanel panel = Panels.newPanel(settingsLayout);

    // Create the tabbed pane
    JTabbedPane tabbedPane = new JTabbedPane();


    tabbedPane.addTab("Language 1", settingsLanguageView.initComponents());
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

    tabbedPane.addTab("Language 2", new SettingsLanguageView().initComponents());
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

    tabbedPane.addTab("Language 3", new SettingsLanguageView().initComponents());
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

    tabbedPane.addTab("Language 4", new SettingsLanguageView().initComponents());
    tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

    applyButton = Buttons.newApplyButton(null);
    undoButton = Buttons.newUndoButton(null);

    panel.add(tabbedPane, "grow,wrap");
    panel.add(applyButton, "split,right");
    panel.add(undoButton, "");

    bindListeners();

    return panel;
  }

  public void bindListeners() {

    applyButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        controller.onApplyClicked();
      }
    });

  }

  /**
   * <p>Snapshot the current control values and present them as a configuration</p>
   *
   * @return A configuration populated from the current configuration with all settings applied
   */
  public Configuration takeSnapshot() {

    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    Locale locale = Languages.newLocaleFromCode(settingsLanguageView.getLanguageCode());

    configuration.getI18NConfiguration().setLocale(locale);

    return configuration;

  }

  public JButton getApplyButton() {
    return applyButton;
  }

  public JButton getUndoButton() {
    return undoButton;
  }
}
