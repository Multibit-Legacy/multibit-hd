package org.multibit.hd.ui.views.wizards.welcome.create_trezor_wallet;

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
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Confirm new PIN</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class CreateTrezorWalletConfirmNewPinPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> {

  // Panel specific components
  private ModelAndView<EnterPinModel, EnterPinView> enterPinMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public CreateTrezorWalletConfirmNewPinPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    // Need to use the LOCK icon here because TH is visually confusing
    super(wizard, panelName, AwesomeIcon.LOCK, MessageKey.CHANGE_PIN_CONFIRM_NEW_PIN_TITLE);

  }

  @Override
  public void newPanelModel() {

    enterPinMaV = Components.newEnterPinMaV(getPanelName());

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

    contentPanel.add(Labels.newConfirmNewPin(), "align center,wrap");
    contentPanel.add(Labels.newEnterPinLookAtDevice(), "align center,wrap");
    contentPanel.add(enterPinMaV.getView().newComponentPanel(), "align center,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

    enterPinMaV.getView().requestInitialFocus();

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {
    // Ensure unsubscribed for events
    unsubscribe();
    return true;
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