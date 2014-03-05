package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.select_backup_summary.SelectBackupSummaryModel;
import org.multibit.hd.ui.views.components.select_backup_summary.SelectBackupSummaryView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>Wizard panel to provide the following to UI:</p>
 * <ul>
 * <li>Welcome users to the application and allow them to select a language</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class RestoreWalletSelectBackupPanelView extends AbstractWizardPanelView<WelcomeWizardModel, SelectBackupSummaryModel> {

  private static final Logger log = LoggerFactory.getLogger(RestoreWalletSelectBackupPanelView.class);

  // Model
  private ModelAndView<SelectBackupSummaryModel, SelectBackupSummaryView> selectBackupMaV;

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public RestoreWalletSelectBackupPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.RESTORE_WALLET_SELECT_BACKUP_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    selectBackupMaV = Components.newSelectBackupSummaryMaV(getPanelName());

    setPanelModel(selectBackupMaV.getModel());

    // Bind it to the wizard model
    getWizardModel().setSelectBackupSummaryModel(selectBackupMaV.getModel());

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.BITCOIN);

    panel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Labels.newSelectBackupNote(), "wrap");
    panel.add(selectBackupMaV.getView().newComponentPanel(), "growx,wrap");

    return panel;
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the "next" button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public boolean beforeShow() {

    // Update the backup list with data from the wizard model
    selectBackupMaV.getModel().setBackupSummaries(getWizardModel().getBackupSummaries());
    selectBackupMaV.getView().updateViewFromModel();

    return true;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        selectBackupMaV.getView().requestInitialFocus();
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing - panel model is updated via an action and wizard model is not applicable

  }

}
