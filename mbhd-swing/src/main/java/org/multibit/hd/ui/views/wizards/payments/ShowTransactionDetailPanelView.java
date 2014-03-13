package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.ui.languages.Languages;
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
 * <li>Show transaction detail</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ShowTransactionDetailPanelView extends AbstractWizardPanelView<PaymentsWizardModel, ShowTransactionDetailPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(ShowTransactionDetailPanelView.class);

  private JLabel transactionHashValue;

  private JTextArea rawTransactionValue;

  /**
   * @param wizard The wizard managing the states
   */
  public ShowTransactionDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TRANSACTION_DETAIL, AwesomeIcon.FILE_TEXT_ALT);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    ShowTransactionDetailPanelModel panelModel = new ShowTransactionDetailPanelModel(
      getPanelName()
    );
    setPanelModel(panelModel);
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

    JLabel transactionHashLabel = Labels.newValueLabel(Languages.safeText(MessageKey.TRANSACTION_HASH));
    transactionHashValue = Labels.newValueLabel("");

    JLabel rawTransactionLabel = Labels.newValueLabel(Languages.safeText(MessageKey.RAW_TRANSACTION));
    rawTransactionValue = new JTextArea(5, 80);
    JScrollPane scrollPane = new JScrollPane(rawTransactionValue, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


    contentPanel.add(transactionHashLabel);
    contentPanel.add(transactionHashValue, "wrap");

    contentPanel.add(rawTransactionLabel);
    contentPanel.add(scrollPane, "grow, push, wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {
    PanelDecorator.addExitCancelPreviousNext(this, wizard);
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
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  public void update() {
    PaymentData paymentData = getWizardModel().getPaymentData();
    if (paymentData != null && paymentData instanceof TransactionData) {
      TransactionData transactionData = (TransactionData)paymentData;

      transactionHashValue.setText(transactionData.getTransactionId());
      rawTransactionValue.setText(transactionData.getRawTransaction());
    }
  }
}
