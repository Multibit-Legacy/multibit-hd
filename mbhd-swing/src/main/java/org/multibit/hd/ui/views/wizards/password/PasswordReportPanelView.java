package org.multibit.hd.ui.views.wizards.password;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show password recovery progress report</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PasswordReportPanelView extends AbstractWizardPanelView<PasswordWizardModel, PasswordReportPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(PasswordReportPanelView.class);

  private JLabel passwordRecoveryStatus;

  /**
   * @param wizard The wizard managing the states
   */
  public PasswordReportPanelView(AbstractWizard<PasswordWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.PASSWORD_REPORT_TITLE);

    PanelDecorator.addExitCancelPreviousFinish(this, wizard);

    CoreServices.uiEventBus.register(this);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    PasswordReportPanelModel panelModel = new PasswordReportPanelModel(
      getPanelName()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setReportPanelModel(panelModel);

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.MAGIC);

    panel.setLayout(new MigLayout(
      Panels.migLayout(0),
      "[][][]", // Column constraints
      "[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    passwordRecoveryStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());

    panel.add(passwordRecoveryStatus, "wrap");

    return panel;
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

}
