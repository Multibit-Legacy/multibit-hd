package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Create and confirm a master password</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class CreateWalletCreatePasswordPanelView extends AbstractWizardPanelView<WelcomeWizardModel, ConfirmPasswordModel> {

  private ModelAndView<ConfirmPasswordModel, ConfirmPasswordView> confirmPasswordMaV;

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public CreateWalletCreatePasswordPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.CREATE_WALLET_PASSWORD_TITLE, AwesomeIcon.KEY);

  }

  @Override
  public void newPanelModel() {

    confirmPasswordMaV = Components.newConfirmPasswordMaV(getPanelName());
    setPanelModel(confirmPasswordMaV.getModel());

    getWizardModel().setConfirmPasswordModel(confirmPasswordMaV.getModel());

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    contentPanel.add(Labels.newWalletPasswordNote(), "wrap");
    contentPanel.add(confirmPasswordMaV.getView().newComponentPanel(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public boolean beforeHide(boolean isExitCancel, ModelAndView... mavs) {

    // Always call super() before hide
    return super.beforeHide(isExitCancel, confirmPasswordMaV);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        confirmPasswordMaV.getView().requestInitialFocus();
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing

  }

}
