package org.multibit.hd.ui.views.wizards.i18n_settings;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Locale;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Edit Contact: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class I18NSettingsPanelView extends AbstractWizardPanelView<I18NSettingsWizardModel, I18NSettingsPanelModel> implements ActionListener {

  // Panel specific components
  private JComboBox<String> languagesComboBox;
  private JComboBox<String> decimalComboBox;
  private JComboBox<String> groupingComboBox;
  private JLabel decimalErrorStatus;
  private JLabel groupingErrorStatus;

//  private int decimalLastIndex = 0;
//  private int groupingLastIndex = 0;

  private ModelAndView<DisplayAmountModel, DisplayAmountView> displayAmountMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public I18NSettingsPanelView(AbstractWizard<I18NSettingsWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.SHOW_I18N_WIZARD);

    PanelDecorator.addCancelApply(this, wizard);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration().deepCopy();

    // Configure the panel model
    setPanelModel(new I18NSettingsPanelModel(
      getPanelName(),
      i18nConfiguration
    ));

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.GLOBE);

    panel.setLayout(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[][][][]", // Column constraints
      "[][][]" // Row constraints
    ));

    Locale locale = Configurations.currentConfiguration.getLocale();

    Preconditions.checkNotNull(locale, "'locale' cannot be empty");

    displayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.TRANSACTION_DETAIL_AMOUNT);
    displayAmountMaV.getModel().setSatoshis(BigInteger.valueOf(1_234_567_890_123L));
    displayAmountMaV.getModel().setLocalAmountVisible(false);

    languagesComboBox = ComboBoxes.newLanguagesComboBox(this, Languages.currentLocale());
    decimalComboBox = ComboBoxes.newDecimalComboBox(this, Languages.currentLocale());
    groupingComboBox = ComboBoxes.newGroupingComboBox(this, Languages.currentLocale());

    decimalErrorStatus = Labels.newErrorStatus(false);
    decimalErrorStatus.setVisible(false);
    groupingErrorStatus = Labels.newErrorStatus(false);
    groupingErrorStatus.setVisible(false);

    panel.add(Labels.newI18NSettingsNote(), "growx,push,span 4,wrap");

    panel.add(Labels.newSelectLanguageLabel(), "shrink");
    panel.add(languagesComboBox, "growx,span 3,width min:200:,wrap");

    panel.add(Labels.newSelectDecimalLabel(), "shrink");
    panel.add(decimalComboBox, "shrink");
    panel.add(decimalErrorStatus, "grow");
    panel.add(Labels.newBlankLabel(),"grow,push,wrap");

    panel.add(Labels.newSelectGroupingLabel(), "shrink");
    panel.add(groupingComboBox, "shrink");
    panel.add(groupingErrorStatus, "grow");
    panel.add(Labels.newBlankLabel(),"grow,push,wrap");

    panel.add(Labels.newExampleLabel(), "shrink");
    panel.add(displayAmountMaV.getView().newComponentPanel(), "grow,push,span 3,wrap");

    return panel;

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        languagesComboBox.requestFocusInWindow();

        displayAmountMaV.getView().updateView(getPanelModel().get().getI18NConfiguration());

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Ensure the wizard model correctly reflects the contents of the components
      updateFromComponentModels(Optional.absent());

      // TODO Apply validation to various fields

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {


  }


  /**
   * <p>Handle one of the combo boxes changing</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    if ("languages".equals(e.getActionCommand())) {
      handleLanguageSelection(e);
    }
    if ("decimal".equals(e.getActionCommand())) {
      handleDecimalSelection(e);
    }
    if ("grouping".equals(e.getActionCommand())) {
      handleGroupingSelection(e);
    }

  }

  /**
   * <p>The language selection has changed</p>
   *
   * @param e The action event
   */
  private void handleLanguageSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    String localeCode = String.valueOf(source.getSelectedItem()).substring(0, 5);

    Locale locale = Languages.newLocaleFromCode(localeCode);

    // Create a new configuration to reset the separators
    I18NConfiguration i18nConfiguration = new I18NConfiguration(locale);

    // Update the model
    getWizardModel().setI18nConfiguration(i18nConfiguration);

    // Update the combo box
    //JComboBox<String> newLanguages = ComboBoxes.newLanguagesComboBox(this, locale);

    displayAmountMaV.getView().updateView(getWizardModel().getI18nConfiguration());

  }

  /**
   * <p>The decimal separator selection has changed</p>
   *
   * @param e The action event
   */
  private void handleDecimalSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    Character decimal = String.valueOf(source.getSelectedItem()).charAt(0);

    // Validate the combination
    if (decimal.equals(getWizardModel().getI18nConfiguration().getGroupingSeparator())) {
      Sounds.playBeep();
      groupingErrorStatus.setVisible(false);
      decimalErrorStatus.setVisible(true);
      return;
    } else {
      groupingErrorStatus.setVisible(false);
      decimalErrorStatus.setVisible(false);
    }

    // Update the model
    getWizardModel().getI18nConfiguration().setDecimalSeparator(decimal);

    displayAmountMaV.getView().updateView(getWizardModel().getI18nConfiguration());

  }

  /**
   * <p>The grouping separator selection has changed</p>
   *
   * @param e The action event
   */
  private void handleGroupingSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    Character grouping = String.valueOf(source.getSelectedItem()).charAt(0);

    // Validate the combination
    if (grouping.equals(getWizardModel().getI18nConfiguration().getDecimalSeparator())) {
      Sounds.playBeep();
      decimalErrorStatus.setVisible(false);
      groupingErrorStatus.setVisible(true);
      return;
    } else {
      decimalErrorStatus.setVisible(false);
      groupingErrorStatus.setVisible(false);
    }

    // Update the model
    getWizardModel().getI18nConfiguration().setGroupingSeparator(grouping);

    displayAmountMaV.getView().updateView(getWizardModel().getI18nConfiguration());

  }

}
