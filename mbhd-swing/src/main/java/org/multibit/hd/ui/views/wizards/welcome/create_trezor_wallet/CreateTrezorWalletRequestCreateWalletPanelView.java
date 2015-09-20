package org.multibit.hd.ui.views.wizards.welcome.create_trezor_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.trezor_display.TrezorDisplayModel;
import org.multibit.hd.ui.views.components.trezor_display.TrezorDisplayView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Create Trezor wallet: Transitional screen to request secure create</li>
 * </ul>
 *
 * @since 0.0.5
 *  
 */
public class CreateTrezorWalletRequestCreateWalletPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> {

  /**
   * @param wizard The wizard managing the states
   */
  public CreateTrezorWalletRequestCreateWalletPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.CREATE_HARDWARE_WALLET_REQUEST_CREATE_WALLET_TITLE, AwesomeIcon.LOCK);

  }

  @Override
  public void newPanelModel() {

    // Nothing to bind

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    ModelAndView<TrezorDisplayModel, TrezorDisplayView> trezorDisplayMaV = Components.newTrezorDisplayMaV(getPanelName());

    // Need some text here in case device fails just as we being the process
    contentPanel.add(trezorDisplayMaV.getView().newComponentPanel(), "align center,wrap");

    // Ensure we register the components to avoid memory leaks
    registerComponents(trezorDisplayMaV);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

    // Start the add/change request process immediately
    getWizardModel().requestCreateWallet();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing we are a transitional view

  }

}