package org.multibit.hd.ui.views.wizards.about;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>About: Show details</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class AboutPanelView extends AbstractWizardPanelView<AboutWizardModel, String> {

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public AboutPanelView(AbstractWizard<AboutWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.ABOUT_TITLE, AwesomeIcon.SMILE_O);

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
      "[][]", // Column constraints
      "[]20[]20[]" // Row constraints
    ));

    String version = Configurations.currentConfiguration.getCurrentVersion();

    contentPanel.add(Labels.newVersion(), "shrink");
    contentPanel.add(Labels.newValueLabel(version), "push,align left,wrap");

    contentPanel.add(Buttons.newLaunchBrowserButton(getLaunchBrowserAction(),MessageKey.VISIT_WEBSITE, MessageKey.VISIT_WEBSITE_TOOLTIP), "wrap");

    contentPanel.add(Labels.newAboutNote(), "grow,push,span 2,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<AboutWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getFinishButton().requestFocusInWindow();
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  /**
   * @return The "launch browser" action
   */
  private Action getLaunchBrowserAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        try {
          Desktop.getDesktop().browse(InstallationManager.MBHD_WEBSITE_URI);
        } catch (IOException e1) {
          ExceptionHandler.handleThrowable(e1);
        }
      }
    };
  }
}