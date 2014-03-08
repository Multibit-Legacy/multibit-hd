package org.multibit.hd.ui.views.wizards.password;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
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

    super(wizard, panelName, MessageKey.PASSWORD_REPORT_TITLE, AwesomeIcon.MAGIC);

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
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    passwordRecoveryStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());

    contentPanel.add(passwordRecoveryStatus, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<PasswordWizardModel> wizard) {

    PanelDecorator.addExitCancelPreviousFinish(this, wizard);

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
