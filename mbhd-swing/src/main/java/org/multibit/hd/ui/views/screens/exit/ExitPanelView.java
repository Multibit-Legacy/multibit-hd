package org.multibit.hd.ui.views.screens.exit;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the exit confirmation display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ExitPanelView extends AbstractScreenView<ExitPanelModel>  {

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public ExitPanelView(ExitPanelModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel newScreenViewPanel() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill", // Layout constraints
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    Panels.showLightBox(Wizards.newExitWizard().getWizardPanel());

    return contentPanel;

  }

}
