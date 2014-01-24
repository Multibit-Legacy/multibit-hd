package org.multibit.hd.ui.views.wizards.exit;

import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ExitPanelView extends AbstractWizardPanelView<ExitWizardModel, String> {

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public ExitPanelView(AbstractWizard<ExitWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.EXIT_TITLE);

    PanelDecorator.addExitCancel(this, wizard);

  }

  @Override
  public JPanel newWizardViewPanel() {

    setPanelModel("");

    return Panels.newPanel();
  }

  @Override
  public boolean updateFromComponentModels() {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
    return true;
  }

}