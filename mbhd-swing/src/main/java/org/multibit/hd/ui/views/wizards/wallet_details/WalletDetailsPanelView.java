package org.multibit.hd.ui.views.wizards.wallet_details;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileView;
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
  JTextField name;
  JTextArea notes;

  private ModelAndView<WalletDetailModel, WalletDetailView> walletDetailMaV;

  private ModelAndView<SelectFileModel, SelectFileView> selectFileMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public WalletDetailsPanelView(AbstractWizard<WalletDetailsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.WALLET_DETAILS_TITLE, AwesomeIcon.DASHBOARD);
  }

  @Override
  public void newPanelModel() {

    selectFileMaV = Components.newSelectFileMaV(getPanelName());
    setPanelModel(selectFileMaV.getModel());
    if (Configurations.currentConfiguration != null) {
      selectFileMaV.getModel().setValue(Configurations.currentConfiguration.getAppearance().getCloudBackupLocation());
    }

    // Register components
    registerComponents(selectFileMaV);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[][]", // Column constraints
        "[]" // Row constraints
      ));

    // Name
    contentPanel.add(Labels.newLabel(MessageKey.NAME));
    name = TextBoxes.newEnterName(getWizardModel(), false);
    contentPanel.add(name, "push,wrap");

    // Public notes
    contentPanel.add(Labels.newLabel(MessageKey.NOTES));
    notes = TextBoxes.newEnterNotes(getWizardModel());
    contentPanel.add(notes, "push,wrap");

    contentPanel.add(Labels.newLabel(MessageKey.CLOUD_BACKUP_LOCATION));
    contentPanel.add(selectFileMaV.getView().newComponentPanel(), "span 2,wrap");

    // Details
    walletDetailMaV = Components.newWalletDetailMaV(getPanelName());
    contentPanel.add(walletDetailMaV.getView().newComponentPanel(), "grow,push,span 2");

    name.setText(getWizardModel().getWalletSummary().getName());
    notes.setText(getWizardModel().getWalletSummary().getNotes());

    // Register components
    registerComponents(walletDetailMaV);
  }

  @Override
  protected void initialiseButtons(AbstractWizard<WalletDetailsWizardModel> wizard) {

    PanelDecorator.addCancelFinish(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(
      new Runnable() {
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

    if (walletSummary != null) {
      if (name != null) {
        walletSummary.setName(name.getText());
      }
      if (notes != null) {
        walletSummary.setNotes(notes.getText());
      }
    }

    log.debug("selectFileMaV.getModel().getValue() = '" + selectFileMaV.getModel().getValue() + "', isSelected = " + selectFileMaV.getModel().isSelected());
    if (Configurations.currentConfiguration != null) {
      if (selectFileMaV.getModel().isSelected()) {
        Configurations.currentConfiguration.getAppearance().setCloudBackupLocation(selectFileMaV.getModel().getValue());
      }
    }
    log.debug("Cloud backup location = '" + Configurations.currentConfiguration.getAppearance().getCloudBackupLocation() + "'");
  }
}