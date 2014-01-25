package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordView;
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

public class CreateWalletPasswordPanelView extends AbstractWizardPanelView<WelcomeWizardModel, ConfirmPasswordModel> {

  private ModelAndView<ConfirmPasswordModel, ConfirmPasswordView> confirmPasswordMaV;

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public CreateWalletPasswordPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.CREATE_WALLET_PASSWORD_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    confirmPasswordMaV = Components.newConfirmPasswordMaV(WelcomeWizardState.CREATE_WALLET_PASSWORD.name());
    setPanelModel(confirmPasswordMaV.getModel());

    getWizardModel().setConfirmPasswordModel(confirmPasswordMaV.getModel());

  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constraints
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Labels.newWalletPasswordNote(),"wrap");
    panel.add(confirmPasswordMaV.getView().newComponentPanel(),"wrap");

    return panel;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing

  }

}
