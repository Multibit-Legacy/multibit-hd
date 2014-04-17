package org.multibit.hd.ui.views.wizards.edit_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
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
 * <li>Edit Wallet: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EditWalletPanelView extends AbstractWizardPanelView<EditWalletWizardModel, String> {

  // View components
  JTextField name;
  JTextArea notes;

  private ModelAndView<WalletDetailModel, WalletDetailView> walletDetailMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public EditWalletPanelView(AbstractWizard<EditWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.EDIT_WALLET_TITLE, AwesomeIcon.EDIT);

  }

  @Override
  public void newPanelModel() {

    setPanelModel("");

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[]" // Row constraints
    ));

    // Name
    contentPanel.add(Labels.newLabel(MessageKey.NAME));
    name = TextBoxes.newEnterName(getWizardModel(), false);
    contentPanel.add(name, "push,wrap");

    // Description
    contentPanel.add(Labels.newLabel(MessageKey.NOTES));
    notes = TextBoxes.newEnterNotes(getWizardModel());
    contentPanel.add(notes, "push,wrap");

    // Details
    walletDetailMaV = Components.newWalletDetailMaV(getPanelName());
    contentPanel.add(walletDetailMaV.getView().newComponentPanel(), "grow,push,span 2");

    name.setText(getWizardModel().getWalletSummary().getName());
    notes.setText(getWizardModel().getWalletSummary().getNotes());

  }

  @Override
  protected void initialiseButtons(AbstractWizard<EditWalletWizardModel> wizard) {

    PanelDecorator.addCancelApply(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        name.requestFocusInWindow();
        name.selectAll();

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Ensure the wizard model correctly reflects the contents of the components
      updateFromComponentModels(Optional.absent());

    }

    // Must be OK to proceed
    return true;

  }


  @Override
  public void updateFromComponentModels(Optional componentModel) {

    WalletSummary walletSummary = getWizardModel().getWalletSummary();

    walletSummary.setName(name.getText());
    walletSummary.setNotes(notes.getText());

  }

}