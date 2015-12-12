package org.multibit.hd.ui.views.screens.buy_sell;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the buy/sell regional wizard</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BuySellScreenView extends AbstractScreenView<BuySellScreenModel>  {

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public BuySellScreenView(BuySellScreenModel panelModel, Screen screen, MessageKey title) {
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
    Panels.showLightBox(Wizards.newGlideraWizard().getWizardScreenHolder());
    return true;

  }
}
