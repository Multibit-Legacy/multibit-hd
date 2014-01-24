package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

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
public class SelectBackupLocationView extends AbstractWizardView<WelcomeWizardModel, SelectFileModel> {

  private ModelAndView<SelectFileModel, SelectFileView> selectFileMaV;

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public SelectBackupLocationView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.SELECT_BACKUP_LOCATION_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public JPanel newWizardViewPanel() {

    selectFileMaV = Components.newSelectFileMaV(WelcomeWizardState.SELECT_BACKUP_LOCATION.name());
    setPanelModel(selectFileMaV.getModel());

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constraints
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Panels.newSelectBackupDirectory(), "wrap");
    panel.add(selectFileMaV.getView().newPanel(), "wrap");

    return panel;
  }

  @Override
  public boolean updateFromComponentModels() {
    selectFileMaV.getView().updateModel();
    return false;
  }

}
