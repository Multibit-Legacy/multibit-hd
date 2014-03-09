package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentData;
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
 * <li>Show transaction overview</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ShowTransactionOverviewPanelView extends AbstractWizardPanelView<PaymentsWizardModel, ShowTransactionOverviewPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(ShowTransactionOverviewPanelView.class);

  private JLabel descriptionLabel;
  private JLabel descriptionValue;


  /**
   * @param wizard The wizard managing the states
   */
  public ShowTransactionOverviewPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TRANSACTION_OVERVIEW, AwesomeIcon.FILE_TEXT_ALT);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    ShowTransactionOverviewPanelModel panelModel = new ShowTransactionOverviewPanelModel(
            getPanelName()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    //getWizardModel().setReportPanelModel(panelModel);

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

    descriptionLabel = Labels.newValueLabel("Description");
    descriptionValue = Labels.newValueLabel("");

    update();

    contentPanel.add(descriptionLabel);
    contentPanel.add(descriptionValue, "wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {
    PanelDecorator.addExitCancelNext(this, wizard);
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getNextButton().requestFocusInWindow();
        getNextButton().setEnabled(true);
      }
    });

    update();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing
  }

  public void update() {
    PaymentData paymentData = getWizardModel().getPaymentData();
    if (paymentData != null) {
      descriptionValue.setText(paymentData.getDescription());
    }
  }
}
