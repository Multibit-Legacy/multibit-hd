package org.multibit.hd.ui.views.wizards.i18n_settings;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_tags.EnterTagsModel;
import org.multibit.hd.ui.views.components.enter_tags.EnterTagsView;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

  private String localeCode = Languages.currentLocale().getLanguage();

  // Panel specific components
  private JTextField name;
  private JTextField emailAddress;
  private JTextField bitcoinAddress;
  private JTextField extendedPublicKey;
  private JTextArea notes;
  private ModelAndView<EnterTagsModel, EnterTagsView> enterLanguageMaV;

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

    name = TextBoxes.newEnterLabel();

    // Configure the panel model
    setPanelModel(new I18NSettingsPanelModel(
      getPanelName()
    ));

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.GLOBE);

    panel.setLayout(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[][]", // Column constraints
      "[][][]" // Row constraints
    ));

    Locale locale = getWizardModel().getLocale();

    Preconditions.checkNotNull(locale, "'locale' cannot be empty");

    JComboBox<String> languagesComboBox = ComboBoxes.newLanguagesComboBox(this);

    panel.add(Labels.newSelectLanguageLabel(), "wrap");
    panel.add(languagesComboBox, "growx,width min:350:,push,wrap");
    panel.add(Labels.newWelcomeNote(), "wrap");


    name = TextBoxes.newReadOnlyTextField(10);
    emailAddress = TextBoxes.newReadOnlyTextField(10);
    bitcoinAddress = TextBoxes.newReadOnlyTextField(10);
    extendedPublicKey = TextBoxes.newReadOnlyTextField(10);

    // Always allow non-unique fields
    notes = TextBoxes.newEnterNotes();

    // Empty tags
    enterLanguageMaV = Components.newEnterTagsMaV(getPanelName(), Lists.<String>newArrayList());

    // Allow unique contact fields
    panel.add(Labels.newName());
    panel.add(name, "grow,push,wrap");

    panel.add(Labels.newEmailAddress());
    panel.add(emailAddress, "grow,push,wrap");

    panel.add(Labels.newBitcoinAddress());
    panel.add(bitcoinAddress, "grow,push,wrap");

    panel.add(Labels.newExtendedPublicKey());
    panel.add(extendedPublicKey, "grow,push,wrap");


    // Tags must be top aligned since it is a tall component
    panel.add(Labels.newTags(), "aligny top");
    panel.add(enterLanguageMaV.getView().newComponentPanel(), "growx,aligny top,wrap");

    // Ensure we shrink to avoid scrunching up if no tags are present
    panel.add(Labels.newNotes());
    panel.add(notes, "shrink,wrap");

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

        name.requestFocusInWindow();

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
   * <p>Handle the change locale action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    localeCode = String.valueOf(source.getSelectedItem()).substring(0, 2);

    // TODO Provide a demonstration panel containing a Bitcoin amount with language

  }


}
