package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
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
 * <li>Select the backup location</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CreateWalletSelectBackupLocationPanelView extends AbstractWizardPanelView<WelcomeWizardModel, SelectFileModel> {

  private ModelAndView<SelectFileModel, SelectFileView> selectFileMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public CreateWalletSelectBackupLocationPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.SELECT_BACKUP_LOCATION_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    selectFileMaV = Components.newSelectFileMaV(getPanelName());
    setPanelModel(selectFileMaV.getModel());

    getWizardModel().setBackupLocationSelectFileModel(selectFileMaV.getModel());

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.FOLDER_OPEN);

    panel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Panels.newSelectBackupDirectory(), "wrap");
    panel.add(selectFileMaV.getView().newComponentPanel(), "wrap");

    return panel;
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

  public void fireInitialStateViewEvents() {

    // Enable the Next button - user can skip entering a cloud backup location
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);
  }

}
