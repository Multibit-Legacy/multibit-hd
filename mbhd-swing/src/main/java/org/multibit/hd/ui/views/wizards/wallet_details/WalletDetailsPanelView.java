package org.multibit.hd.ui.views.wizards.wallet_details;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetailModel;
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetailView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Wallet details: View details</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class WalletDetailsPanelView extends AbstractWizardPanelView<WalletDetailsWizardModel, SelectFileModel> {

  // View components
  private JTextArea notes;

  private ModelAndView<WalletDetailModel, WalletDetailView> walletDetailMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public WalletDetailsPanelView(AbstractWizard<WalletDetailsWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.DASHBOARD, MessageKey.WALLET_DETAILS_TITLE);
  }

  @Override
  public void newPanelModel() {

    // No model we are read only

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[][]", // Column constraints
        "[][][][]" // Row constraints
      ));

    // This should always be present
    WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();

    // Name
    contentPanel.add(Labels.newLabel(MessageKey.NAME));
    contentPanel.add(Labels.newValueLabel(walletSummary.getName()), "push,wrap");

    // Public notes
    notes = TextBoxes.newReadOnlyTextArea(2, 50);
    notes.setText(walletSummary.getNotes());
    contentPanel.add(Labels.newLabel(MessageKey.NOTES),"wrap");
    contentPanel.add(notes, "span2,wrap");

    // Details
    walletDetailMaV = Components.newWalletDetailMaV(getPanelName());
    contentPanel.add(walletDetailMaV.getView().newComponentPanel(), "grow,span 2");

    // Register components
    registerComponents(walletDetailMaV);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WalletDetailsWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing (read only)
  }
}