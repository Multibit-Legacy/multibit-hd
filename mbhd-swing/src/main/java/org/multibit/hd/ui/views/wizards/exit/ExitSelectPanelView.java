package org.multibit.hd.ui.views.wizards.exit;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Exit: Select</li>
 * </ul>
 * <p>For UI consistency the cancel button must lead the action button (exit)</p>
 *
 * @since 0.0.1
 */
public class ExitSelectPanelView extends AbstractWizardPanelView<ExitWizardModel, ExitState> implements ActionListener {

  // Model
  private ExitState currentSelection;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public ExitSelectPanelView(AbstractWizard<ExitWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.EXIT_OR_SWITCH_TITLE, AwesomeIcon.RANDOM);

  }

  @Override
  public void newPanelModel() {

    // Default to confirm exit
    currentSelection = ExitState.CONFIRM_EXIT;
    setPanelModel(currentSelection);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]" // Row constraints
      ));

    contentPanel.add(
      Panels.newExitSelector(
        this,
        ExitState.CONFIRM_EXIT.name(),
        ExitState.SWITCH_WALLET.name()
      ), "span 2, wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ExitWizardModel> wizard) {
    PanelDecorator.addCancelNext(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {

    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public void afterShow() {

    decorateNextButton();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Next has been clicked

    setPanelModel(currentSelection);

    // Bind this to the wizard model
    getWizardModel().setCurrentSelection(currentSelection);

  }

  /**
   * <p>Handle the "select exit" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JRadioButton source = (JRadioButton) e.getSource();

    currentSelection = ExitState.valueOf(source.getActionCommand());

    decorateNextButton();

  }

  private void decorateNextButton() {
    // Change the button colour to indicate a dangerous operation
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          if (ExitState.CONFIRM_EXIT.equals(currentSelection)) {
            getNextButton().setText(Languages.safeText(MessageKey.EXIT));
            AccessibilityDecorator.apply(getNextButton(), MessageKey.EXIT, MessageKey.EXIT_TOOLTIP);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), getNextButton());
          }
          if (ExitState.SWITCH_WALLET.equals(currentSelection)) {
            getNextButton().setText(Languages.safeText(MessageKey.SWITCH));
            AccessibilityDecorator.apply(getNextButton(), MessageKey.SWITCH, MessageKey.SWITCH_TOOLTIP);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.detailPanelBackground() ,getNextButton());
          }
        }
      });
  }

}