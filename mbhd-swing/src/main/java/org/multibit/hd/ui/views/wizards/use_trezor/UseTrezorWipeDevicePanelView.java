package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Panel telling the user to press the continue button to wipe their Trezor</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class UseTrezorWipeDevicePanelView extends AbstractWizardPanelView<UseTrezorWizardModel, UseTrezorWipeDevicePanelModel> {

  private boolean wipeHasBeenRequested = false;


  /**
   * @param wizard The wizard managing the states
   */
  public UseTrezorWipeDevicePanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.WIPE_DEVICE_TITLE, AwesomeIcon.MEDKIT);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    UseTrezorWipeDevicePanelModel panelModel = new UseTrezorWipeDevicePanelModel(
      getPanelName()
    );
    setPanelModel(panelModel);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[]10[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    JLabel wipeTrezorMessageLabel = Labels.newWipeTrezorLabel();


    contentPanel.add(wipeTrezorMessageLabel, "wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);
    wipeHasBeenRequested = false;

  }

  @Override
  public void afterShow() {

    // Ensure the Finish button is enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

    // Start the wipe Trezor in another thread
    if (!wipeHasBeenRequested) {
      getWizardModel().wipeTrezor();
      wipeHasBeenRequested = true;
    }

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

}
