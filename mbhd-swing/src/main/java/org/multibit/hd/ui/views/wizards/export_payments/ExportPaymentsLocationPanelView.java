package org.multibit.hd.ui.views.wizards.export_payments;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
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
 * <li>Select the backup location</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ExportPaymentsLocationPanelView extends AbstractWizardPanelView<ExportPaymentsWizardModel, SelectFileModel> {

  private ModelAndView<SelectFileModel, SelectFileView> selectFileMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public ExportPaymentsLocationPanelView(AbstractWizard<ExportPaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.FOLDER_OPEN, MessageKey.SELECT_EXPORT_PAYMENTS_LOCATION, null);

  }

  @Override
  public void newPanelModel() {

    selectFileMaV = Components.newSelectFileMaV(getPanelName());
    setPanelModel(selectFileMaV.getModel());

    getWizardModel().setExportPaymentsLocationSelectFileModel(selectFileMaV.getModel());

    // Register components
    registerComponents(selectFileMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    contentPanel.add(Panels.newSelectExportPaymentsDirectory(), "wrap");
    contentPanel.add(selectFileMaV.getView().newComponentPanel(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ExportPaymentsWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

     selectFileMaV.getView().requestInitialFocus();

  }

  public void fireInitialStateViewEvents() {

    // Enable the Next button - user can skip entering a cloud backup location
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);
  }

  @Override
   public void updateFromComponentModels(Optional componentModel) {
     // No need to update the wizard it has the references

     // Determine any events
     ViewEvents.fireWizardButtonEnabledEvent(
       getPanelName(),
       WizardButton.NEXT,
       isNextEnabled()
     );
   }

   /**
    * @return True if the "next" button should be enabled
    */
   private boolean isNextEnabled() {
     return !Strings.isNullOrEmpty(getPanelModel().get().getValue());
   }

}
