package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.Resources;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.SecuritySummary;
import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertModel;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * <p>Wizard panel to provide the following to UI:</p>
 * <ul>
 * <li>Welcome users to the application and ensure the licence agreement is agreed</li>
 * </ul>
 *
 * @since 0.0.3
 *
 */
public class WelcomeLicencePanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> implements ActionListener {

  private ModelAndView<DisplaySecurityAlertModel, DisplaySecurityAlertView> displaySecurityPopoverMaV;

  private boolean licenceAccepted = false;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public WelcomeLicencePanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.WELCOME_TITLE, AwesomeIcon.GLOBE);

  }

  @Override
  public void newPanelModel() {

    displaySecurityPopoverMaV = Popovers.newDisplaySecurityPopoverMaV(getPanelName());

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]" // Row constraints
    ));

    // Load the licence agreement from the classpath
    final String licence;
    try {
      licence = Resources.toString(
        WelcomeLicencePanelView.class.getResource("/licence.txt"),
        Charsets.UTF_8
      );
    } catch (IOException e) {
      throw new IllegalStateException("'licence.txt' must be present on the classpath");
    }

    // The licence is a wall of text so needs scroll bars
    JTextArea licenceTextArea = TextBoxes.newReadOnlyTextArea(10, 80);
    licenceTextArea.setBorder(null);
    licenceTextArea.setText(licence);
    licenceTextArea.setCaretPosition(0);

    // Raw transaction requires its own scroll pane
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setOpaque(true);
    scrollPane.setBackground(Themes.currentTheme.readOnlyBackground());
    scrollPane.setBorder(null);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // View port requires special handling
    scrollPane.setViewportView(licenceTextArea);
    scrollPane.getViewport().setBackground(Themes.currentTheme.readOnlyBackground());
    scrollPane.setViewportBorder(new TextBubbleBorder(Themes.currentTheme.readOnlyBorder()));

    // Ensure we maintain the overall theme (no vertical since we're using rounded border)
    ScrollBarUIDecorator.apply(scrollPane, false);

    contentPanel.add(scrollPane, "grow,push,span 2," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Panels.newLicenceSelector(
      this,
      MessageKey.ACCEPT_LICENCE.name(),
      MessageKey.REJECT_LICENCE.name()
    ), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Disable the "next" button (requires licence acceptance)
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Check for any security alerts
        Optional<SecurityEvent> securityEvent = CoreServices.getApplicationEventService().getLatestSecurityEvent();
        if (securityEvent.isPresent() && securityEvent.get().is(SecuritySummary.AlertType.DEBUGGER_ATTACHED)) {

          displaySecurityPopoverMaV.getModel().setValue(securityEvent.get());

          // Show the security alert as a popover
          Panels.showLightBoxPopover(displaySecurityPopoverMaV.getView().newComponentPanel());

        }

      }
    });
  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    // If the user exits on this panel we assume they rejected the licence
    // even if they selected "accept"

    log.info("User REJECTED the licence agreement due to clicking Exit on the licence screen");

    // Perform a direct update of the configuration
    Configurations.currentConfiguration.setLicenceAccepted(false);
    Configurations.persistCurrentConfiguration();

    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // The user wants to proceed so use the value from the licence

    if (licenceAccepted) {
      log.info("User ACCEPTED the licence agreement");
    } else {
      log.info("User REJECTED the licence agreement");
    }

    // Perform a direct update of the configuration
    Configurations.currentConfiguration.setLicenceAccepted(licenceAccepted);
    Configurations.persistCurrentConfiguration();

  }

  /**
   * <p>Handle the change locale action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(final ActionEvent e) {

    JRadioButton source = (JRadioButton) e.getSource();
    String command = source.getActionCommand();

    // Determine the selection
    licenceAccepted = MessageKey.ACCEPT_LICENCE.name().equalsIgnoreCase(command);

    // Update the "next" button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, licenceAccepted);

  }
}
