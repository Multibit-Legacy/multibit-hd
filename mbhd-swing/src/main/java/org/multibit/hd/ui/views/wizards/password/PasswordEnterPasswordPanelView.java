package org.multibit.hd.ui.views.wizards.password;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class PasswordEnterPasswordPanelView extends AbstractWizardPanelView<PasswordWizardModel, PasswordEnterPasswordPanelModel> {

  // Panel specific components
  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public PasswordEnterPasswordPanelView(AbstractWizard<PasswordWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.PASSWORD_TITLE);

    PanelDecorator.addExitCancelRestoreUnlock(this, wizard);
  }

  @Override
  public void newPanelModel() {

    enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());

    // Configure the panel model
    final PasswordEnterPasswordPanelModel panelModel = new PasswordEnterPasswordPanelModel(
      getPanelName(),
      enterPasswordMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setEnterPasswordPanelModel(panelModel);

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.KEY);

    panel.setLayout(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Labels.newPasswordNote(),"wrap");

    panel.add(enterPasswordMaV.getView().newComponentPanel(), "wrap");

    return panel;
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      false
    );

  }

  @Override
  public void afterShow() {

    registerDefaultButton(getFinishButton());

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        enterPasswordMaV.getView().requestInitialFocus();
      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExiting) {
    // If a password has been entered, put it into the WalletData (so that it is available for address generation)
    // TODO - remove when we have proper HD wallets  - won't need password for address generation
    CharSequence password = enterPasswordMaV.getModel().getValue();
    if (!"".equals(password)) {
      // TODO should be using WalletService
      Optional<WalletData> walletDataOptional = WalletManager.INSTANCE.getCurrentWalletData();
      if (walletDataOptional.isPresent()) {
        walletDataOptional.get().setPassword(password);
      }
    }
    return true;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      isFinishEnabled()
    );

  }

  /**
   * @return True if the "finish" button should be enabled
   */
  private boolean isFinishEnabled() {

    return !Strings.isNullOrEmpty(
      getPanelModel().get()
      .getEnterPasswordModel()
      .getValue());

  }

}