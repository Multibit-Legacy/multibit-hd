package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Select which of the trezor related tools to run</li>
 * </ul>
 *
 * @since 0.0.1
 */

public class UseTrezorBuyTrezorPanelView extends AbstractWizardPanelView<UseTrezorWizardModel, UseTrezorState> implements ActionListener {

  // TODO replace with affiliate link

  private static String BUY_TREZOR_URL = "https://www.buytrezor.com";

  // View components
  private JLabel trezorConnectedStatusLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public UseTrezorBuyTrezorPanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.BUY_TREZOR_TITLE, AwesomeIcon.SHIELD);

  }

  @Override
  public void newPanelModel() {

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]" // Row constraints
      ));

    JButton launchBrowserButton = Buttons.newLaunchBrowserButton(getLaunchBrowserAction(), MessageKey.BUY_TREZOR, MessageKey.BUY_TREZOR_TOOLTIP);
       contentPanel.add(Labels.newBuyTrezorCommentNote(), "wrap");
       contentPanel.add(launchBrowserButton, "wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {
    PanelDecorator.addExitCancelPreviousFinish(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {

    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT,true);

  }

  @Override
  public boolean beforeShow() {

    return true;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Next has been clicked


  }

  /**
   * <p>Handle the "select tool" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

  }

  /**
    * @return The "launch browser" action
    */
   private Action getLaunchBrowserAction() {

     return new AbstractAction() {
       @Override
       public void actionPerformed(ActionEvent e) {

         try {
           Desktop.getDesktop().browse(new URI(BUY_TREZOR_URL));
         } catch (IOException | URISyntaxException e1) {
           ExceptionHandler.handleThrowable(e1);
         }

       }
     };
   }
}
