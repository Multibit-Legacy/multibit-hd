package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Ask the user to press ok on their Trezor in response to an Encrypt Code message</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class UseTrezorPressOkForEncryptCodePanelView extends AbstractWizardPanelView<UseTrezorWizardModel, UseTrezorPressOkForEncryptCodePanelModel> {

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public UseTrezorPressOkForEncryptCodePanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PRESS_CONFIRM_ON_TREZOR_TITLE, AwesomeIcon.KEY);

  }

  @Override
  public void newPanelModel() {

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    contentPanel.add(Labels.newPressOkOnTrezorNote(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      true
    );

  }


}
