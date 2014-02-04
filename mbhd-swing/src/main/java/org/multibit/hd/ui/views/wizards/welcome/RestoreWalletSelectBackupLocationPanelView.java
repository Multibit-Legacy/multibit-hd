package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseSize;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseView;
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
public class RestoreWalletSelectBackupLocationPanelView extends AbstractWizardPanelView<WelcomeWizardModel, RestoreWalletSelectBackupLocationPanelModel> {

  private ModelAndView<SelectFileModel, SelectFileView> selectFileMaV;
  private ModelAndView<EnterSeedPhraseModel, EnterSeedPhraseView> enterSeedPhraseMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public RestoreWalletSelectBackupLocationPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.RESTORE_WALLET_BACKUP_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    // Component models
    selectFileMaV = Components.newSelectFileMaV(getPanelName());
    enterSeedPhraseMaV = Components.newEnterSeedPhraseMaV(getPanelName(),false);

    RestoreWalletSelectBackupLocationPanelModel panelModel = new RestoreWalletSelectBackupLocationPanelModel(
      getPanelName(),
      selectFileMaV.getModel(),
      enterSeedPhraseMaV.getModel()
    );
    setPanelModel(panelModel);

    getWizardModel().setRestoreLocationSelectFileModel(selectFileMaV.getModel());
    getWizardModel().setRestoreWalletBackupSeedPhraseModel(enterSeedPhraseMaV.getModel());

  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[][]", // Column constraints
      "[][][]" // Row constraints
    ));

    panel.add(Panels.newRestoreFromBackup(), "span 2,grow,wrap");
    panel.add(enterSeedPhraseMaV.getView().newComponentPanel(), "span 2,wrap");
    panel.add(Labels.newSelectFolder());
    panel.add(selectFileMaV.getView().newComponentPanel(), "grow,wrap");

    return panel;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing we have a direct reference

    // Enable the "next" button if the backup location is present and the seed phrase has a valid size
    boolean backupLocationPresent = !Strings.isNullOrEmpty(selectFileMaV.getModel().getValue());

    boolean seedPhraseSizeValid = SeedPhraseSize.isValid(enterSeedPhraseMaV.getModel().getValue().size());

    boolean result = backupLocationPresent && seedPhraseSizeValid;

    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      result
    );

  }

}
