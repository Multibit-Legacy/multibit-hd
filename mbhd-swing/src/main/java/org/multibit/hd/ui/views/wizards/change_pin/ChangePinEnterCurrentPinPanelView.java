package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_pin.EnterPinModel;
import org.multibit.hd.ui.views.components.enter_pin.EnterPinView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Enter current PIN</li>
 * </ul>
 *
 * @since 0.0.5
 *
 */
public class ChangePinEnterCurrentPinPanelView extends AbstractWizardPanelView<ChangePinWizardModel, ChangePinEnterPinPanelModel> {

  // Panel specific components
  private ModelAndView<EnterPinModel, EnterPinView> enterPinMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public ChangePinEnterCurrentPinPanelView(AbstractWizard<ChangePinWizardModel> wizard, String panelName) {

    // Need to use the LOCK icon here because TH is visually confusing
    super(wizard, panelName, AwesomeIcon.LOCK, MessageKey.CHANGE_PIN_ENTER_CURRENT_PIN_TITLE, null);

  }

  @Override
  public void newPanelModel() {

    enterPinMaV = Components.newEnterPinMaV(getPanelName());

    // Configure the panel model
    final ChangePinEnterPinPanelModel panelModel = new ChangePinEnterPinPanelModel(
      getPanelName(),
      enterPinMaV.getModel()
    );
    setPanelModel(panelModel);

    // Register components
    registerComponents(enterPinMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[][]10[]" // Row constraints
    ));

    // Use the initial state to set this

    contentPanel.add(Labels.newEnterCurrentPin(), "align center,wrap");
    contentPanel.add(Labels.newEnterPinLookAtDevice(), "align center,wrap");
    contentPanel.add(enterPinMaV.getView().newComponentPanel(), "align center,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ChangePinWizardModel> wizard) {

    PanelDecorator.addCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        enterPinMaV.getView().requestInitialFocus();

      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      isNextEnabled()
    );

    getWizardModel().setMostRecentPin(enterPinMaV.getModel().getValue());

  }

  /**
   * @return True if the "next" button should be enabled
   */
  private boolean isNextEnabled() {

    return !Strings.isNullOrEmpty(enterPinMaV.getModel().getValue());

  }
}