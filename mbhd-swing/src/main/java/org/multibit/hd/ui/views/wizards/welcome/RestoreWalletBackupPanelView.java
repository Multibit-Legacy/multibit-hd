package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileView;
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
public class RestoreWalletBackupPanelView extends AbstractWizardPanelView<WelcomeWizardModel, RestoreWalletBackupPanelModel> {

  private ModelAndView<SelectFileModel, SelectFileView> selectFileMaV;
  private ModelAndView<EnterPasswordModel, EnterPasswordView> passwordMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public RestoreWalletBackupPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.RESTORE_WALLET_BACKUP_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    // Component models
    selectFileMaV = Components.newSelectFileMaV(getPanelName());
    passwordMaV = Components.newEnterPasswordMaV(getPanelName());

    RestoreWalletBackupPanelModel panelModel = new RestoreWalletBackupPanelModel(
      getPanelName(),
      selectFileMaV.getModel(),
      passwordMaV.getModel()
    );
    setPanelModel(panelModel);

    getWizardModel().setBackupLocationSelectFileModel(selectFileMaV.getModel());

  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[][]", // Column constraints
      "[][][]" // Row constraints
    ));

    panel.add(Panels.newRestoreFromBackup(), "span 2,wrap");
    panel.add(Labels.newSelectFolder());
    panel.add(selectFileMaV.getView().newComponentPanel(), "grow,wrap");
    panel.add(Labels.newEnterPassword());
    panel.add(passwordMaV.getView().newComponentPanel(), "grow,wrap");

    return panel;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing we have a direct reference

    // Enable the "next" button if the backup location and password is not empty
    boolean result = !Strings.isNullOrEmpty(selectFileMaV.getModel().getValue())
      && !Strings.isNullOrEmpty(passwordMaV.getModel().getValue());

    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      result
    );

  }

}
