package org.multibit.hd.ui.views.wizards.buy_sell;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.SafeDesktop;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URI;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Show details and buy, Sell buttons</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class BuySellSelectPanelView extends AbstractWizardPanelView<BuySellWizardModel, String> {

  // TODO add bitcoin address
  private static final URI buyUri = URI.create("https://www.glidera.io/referral?client_id=700a89b3cf4ce97d08bb58ea4e550f38");

  private static final URI sellUri = URI.create("https://www.glidera.io/referral?client_id=700a89b3cf4ce97d08bb58ea4e550f38");

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public BuySellSelectPanelView(AbstractWizard<BuySellWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.SHOPPING_BASKET, MessageKey.BUY_SELL_TITLE);

  }

  @Override
  public void newPanelModel() {

    setPanelModel("");

    // No wizard model
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[][]10" // Row constraints
    ));

    contentPanel.add(Labels.newBuySellRegionNote(), "span 2, growx, wrap");
    contentPanel.add(Buttons.newLaunchBrowserButton(getBuyLaunchBrowserAction(),MessageKey.BUY_VISIT_GLIDERA, MessageKey.BUY_VISIT_GLIDERA_TOOLTIP), "align center, wrap");
    contentPanel.add(Buttons.newLaunchBrowserButton(getSellLaunchBrowserAction(),MessageKey.SELL_VISIT_GLIDERA, MessageKey.SELL_VISIT_GLIDERA_TOOLTIP), "align center, wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<BuySellWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    getFinishButton().requestFocusInWindow();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  /**
   * @return The "launch browser" action for the Buy button
   */
  private Action getBuyLaunchBrowserAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (!SafeDesktop.browse(buyUri)) {
          Sounds.playBeep(Configurations.currentConfiguration.getSound());
        }

      }
    };
  }

  /**
    * @return The "launch browser" action for the Sell button
    */
   private Action getSellLaunchBrowserAction() {

     return new AbstractAction() {
       @Override
       public void actionPerformed(ActionEvent e) {

         if (!SafeDesktop.browse(sellUri)) {
           Sounds.playBeep(Configurations.currentConfiguration.getSound());
         }

       }
     };
   }
}
