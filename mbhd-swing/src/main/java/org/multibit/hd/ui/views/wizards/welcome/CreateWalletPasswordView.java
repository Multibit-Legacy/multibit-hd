package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

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

public class CreateWalletPasswordView extends AbstractWizardView<WelcomeWizardModel, ConfirmPasswordModel>  {

  private ModelAndView<ConfirmPasswordModel, ConfirmPasswordView> confirmPasswordMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public CreateWalletPasswordView(AbstractWizard<WelcomeWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.CREATE_WALLET_PASSWORD_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    confirmPasswordMaV = Components.newConfirmPassword(WelcomeWizardState.CREATE_WALLET_PASSWORD.name());
    setPanelModel(confirmPasswordMaV.getModel());

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constrains
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Labels.newWalletPasswordNote(),"wrap");
    panel.add(confirmPasswordMaV.getView().newPanel(),"wrap");

    return panel;
  }

  @Override
  public boolean updatePanelModel() {
    confirmPasswordMaV.getView().updateModel();
    return false;
  }

}
