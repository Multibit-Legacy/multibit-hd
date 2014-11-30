package org.multibit.hd.ui.views.wizards.exit;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
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
    PanelDecorator.addCancelFinish(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {

    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

  }

  @Override
  public void afterShow() {

    decorateFinishButton();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Finish has been clicked

    setPanelModel(currentSelection);

    // Bind this to the wizard model
    getWizardModel().setState(currentSelection);

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

    // Bind this to the wizard model
    getWizardModel().setState(currentSelection);

    decorateFinishButton();

  }

  private void decorateFinishButton() {

    // Change the button colour to indicate a dangerous operation
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          // Require the finish button to ensure the wizard hide event
          JButton finishButton = getFinishButton();

          if (ExitState.CONFIRM_EXIT.equals(currentSelection)) {

            finishButton.setText(Languages.safeText(MessageKey.EXIT));

            AccessibilityDecorator.apply(finishButton, MessageKey.EXIT, MessageKey.EXIT_TOOLTIP);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), finishButton);

            AwesomeDecorator.applyIcon(AwesomeIcon.SIGN_OUT, finishButton, false, MultiBitUI.NORMAL_ICON_SIZE);
          }
          if (ExitState.SWITCH_WALLET.equals(currentSelection)) {

            finishButton.setText(Languages.safeText(MessageKey.SWITCH));

            AccessibilityDecorator.apply(finishButton, MessageKey.SWITCH, MessageKey.SWITCH_TOOLTIP);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.detailPanelBackground(), finishButton);

            AwesomeIcon icon = AwesomeDecorator.select(AwesomeIcon.ANGLE_DOUBLE_RIGHT, AwesomeIcon.ANGLE_DOUBLE_LEFT);
            AwesomeDecorator.applyIcon(icon, finishButton, false, MultiBitUI.NORMAL_ICON_SIZE);

          }
        }
      });
  }

}