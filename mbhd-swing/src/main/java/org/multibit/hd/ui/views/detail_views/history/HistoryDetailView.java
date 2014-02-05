package org.multibit.hd.ui.views.detail_views.history;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the tools detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class HistoryDetailView {

  private final JPanel contentPanel;

  private int count = 0;

  public HistoryDetailView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill", // Layout constraints
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );
    contentPanel = Panels.newPanel(layout);

    Action showWelcomeWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // TODO Consider most appropriate initial state
        Panels.showLightBox(Wizards.newClosingWelcomeWizard(WelcomeWizardState.WELCOME_SELECT_LANGUAGE).getWizardPanel());
      }
    };

    contentPanel.add(Buttons.newShowWelcomeWizardButton(showWelcomeWizardAction),"w 240,h 200,align center,push");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

}
