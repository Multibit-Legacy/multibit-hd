package org.multibit.hd.ui.views.screens.about;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the "About" display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class AboutScreenView extends AbstractScreenView<AboutScreenModel>  {

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public AboutScreenView(AboutScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {

    MigLayout layout = new MigLayout(
      Panels.migXYLayout(),
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );

    return Panels.newPanel(layout);

  }

  @Override
  public boolean beforeShow() {

    Panels.showLightBox(Wizards.newAboutWizard().getWizardScreenHolder());
    return true;

  }
}
