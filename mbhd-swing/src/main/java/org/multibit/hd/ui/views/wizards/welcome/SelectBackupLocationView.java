package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

import static org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState.SELECT_BACKUP_LOCATION;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Select the backup location</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SelectBackupLocationView extends AbstractWizardView<WelcomeWizardModel, SelectFileModel> {

  private ModelAndView<SelectFileModel, SelectFileView> selectFileMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public SelectBackupLocationView(AbstractWizard<WelcomeWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.SELECT_BACKUP_LOCATION_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    selectFileMaV = Components.newSelectFileMaV(WelcomeWizardState.SELECT_BACKUP_LOCATION.name());
    setPanelModel(selectFileMaV.getModel());

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constrains
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Panels.newSelectBackupDirectory(), "wrap");
    panel.add(selectFileMaV.getView().newPanel(), "wrap");

    return panel;
  }

  @Override
  public void fireViewEvents() {
    ViewEvents.fireWizardButtonEnabledEvent(SELECT_BACKUP_LOCATION.name(), WizardButton.NEXT, false);
  }

  @Override
  public boolean updatePanelModel() {
    selectFileMaV.getView().updateModel();
    return false;
  }

}
