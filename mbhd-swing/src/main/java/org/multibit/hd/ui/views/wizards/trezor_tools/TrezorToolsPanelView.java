package org.multibit.hd.ui.views.wizards.trezor_tools;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Repair wallet: Show</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class TrezorToolsPanelView extends AbstractWizardPanelView<TrezorToolsWizardModel, String> {

  // View components

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public TrezorToolsPanelView(AbstractWizard<TrezorToolsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.REPAIR_WALLET_TITLE, AwesomeIcon.SHIELD);

  }

  @Override
  public void newPanelModel() {

    setPanelModel("");

    // No wizard model
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    //walletDetailMaV = Components.newWalletDetailMaV(getPanelName());

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]" // Row constraints
    ));

    contentPanel.add(Labels.newRepairWalletNote());

  }

  @Override
  protected void initialiseButtons(AbstractWizard<TrezorToolsWizardModel> wizard) {

    PanelDecorator.addCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

}