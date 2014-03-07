package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Restore wallet from backup</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RestoreWalletSelectBackupLocationPanelView extends AbstractWizardPanelView<WelcomeWizardModel, SelectFileModel> {

  private ModelAndView<SelectFileModel, SelectFileView> selectFileMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public RestoreWalletSelectBackupLocationPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.RESTORE_WALLET_SELECT_BACKUP_TITLE, AwesomeIcon.FOLDER_OPEN);

  }

  @Override
  public void newPanelModel() {

    // Component model
    selectFileMaV = Components.newSelectFileMaV(getPanelName());

    setPanelModel(selectFileMaV.getModel());

    getWizardModel().setRestoreLocationSelectFileModel(selectFileMaV.getModel());

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[][][]" // Row constraints
    ));

    contentPanel.add(Panels.newRestoreFromBackup(), "span 2,grow,wrap");
    contentPanel.add(Labels.newSelectFolder());
    contentPanel.add(selectFileMaV.getView().newComponentPanel(), "grow,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the "next" button is enabled (so that the user can click next without a backup)

    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        selectFileMaV.getView().requestInitialFocus();
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing we have a direct reference

  }

}
