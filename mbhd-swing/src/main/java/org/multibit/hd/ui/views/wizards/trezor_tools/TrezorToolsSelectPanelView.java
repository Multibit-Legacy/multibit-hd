package org.multibit.hd.ui.views.wizards.trezor_tools;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
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

public class TrezorToolsSelectPanelView extends AbstractWizardPanelView<TrezorToolsWizardModel, TrezorToolsState> implements ActionListener {

  // TODO replace with affiliate link

  private static String BUY_TREZOR_URL = "https://www.buytrezor.com";

  // Model
  private TrezorToolsState currentSelection;

  // View components
  private JLabel trezorConnectedStatusLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public TrezorToolsSelectPanelView(AbstractWizard<TrezorToolsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TREZOR_TOOLS_TITLE, AwesomeIcon.SHIELD);

  }

  @Override
  public void newPanelModel() {

    currentSelection = TrezorToolsState.VERIFY_DEVICE;
    setPanelModel(currentSelection);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]" // Row constraints
      ));

    MessageKey trezorStatusKey = isTrezorPresent() ? MessageKey.TREZOR_FOUND : MessageKey.NO_TREZOR_FOUND;
    trezorConnectedStatusLabel = Labels.newStatusLabel(
      Optional.of(trezorStatusKey),
      null,
      Optional.<Boolean>absent());
    AccessibilityDecorator.apply(trezorConnectedStatusLabel, trezorStatusKey);

    contentPanel.add(trezorConnectedStatusLabel, "span 2, wrap");

    contentPanel.add(
      Panels.newTrezorToolSelector(
        this,
        TrezorToolsState.VERIFY_DEVICE.name(),
        TrezorToolsState.WIPE_DEVICE.name()
      ), "span 2, wrap");

    JButton launchBrowserButton = Buttons.newLaunchBrowserButton(getLaunchBrowserAction(), MessageKey.BUY_TREZOR, MessageKey.BUY_TREZOR_TOOLTIP);
       contentPanel.add(Labels.newBuyTrezorCommentNote());
       contentPanel.add(launchBrowserButton, "align right, wrap");


  }

  @Override
  protected void initialiseButtons(AbstractWizard<TrezorToolsWizardModel> wizard) {
    PanelDecorator.addExitCancelNext(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {

    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, isTrezorPresent());

  }

  @Override
  public boolean beforeShow() {

    return true;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Next has been clicked

    setPanelModel(currentSelection);

    // Bind this to the wizard model
    getWizardModel().setCurrentSelection(currentSelection);

  }

  /**
   * <p>Handle the "select tool" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JRadioButton source = (JRadioButton) e.getSource();

    currentSelection = TrezorToolsState.valueOf(source.getActionCommand());

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

  /**
    * See if the Trezor wallet is present
    */
   public boolean isTrezorPresent() {

     Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();
     if (hardwareWalletService.isPresent()) {
       try {
         return hardwareWalletService.get().isWalletPresent();
       } catch (IllegalStateException ise) {
         // Device is not ready
         return false;
       }
     } else {
       return false;
     }
   }

   /**
    * <p>Downstream consumer applications should respond to hardware wallet events</p>
    *
    * @param event The hardware wallet event indicating a state change
    */
   @Subscribe
   public void onHardwareWalletEvent(HardwareWalletEvent event) {

     log.debug("Received hardware event: '{}'", event.getEventType().name());

     switch (event.getEventType()) {
       case SHOW_DEVICE_FAILED:
         // Treat as end of example
         System.exit(0);
         break;
       case SHOW_DEVICE_DETACHED:
         // Can simply wait for another device to be connected again
         break;
       case SHOW_DEVICE_READY:
         // Get some information about the device
         //Features features = hardwareWalletService.getContext().getFeatures().get();
         //log.info("Features: {}", features);

       default:
         // Ignore
     }

   }
}
