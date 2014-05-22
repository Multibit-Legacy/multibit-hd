package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

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

  private static final String BLOCKCHAIN_INFO_PREFIX = "http://blockchain.info/tx-index/";

  private JLabel transactionHashValue;

  private JTextArea rawTransactionValue;

  private JLabel sizeValue;

  /**
   * @param wizard The wizard managing the states
   */
  public ShowTransactionDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TRANSACTION_DETAIL, AwesomeIcon.FILE_TEXT_O);

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
    rawTransactionValue = new JTextArea(5, 60);
    JScrollPane scrollPane = new JScrollPane(rawTransactionValue, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    JLabel sizeLabel = Labels.newValueLabel(Languages.safeText(MessageKey.SIZE));
    sizeValue = Labels.newValueLabel("");

    JButton blockchainInfoBrowserButton = Buttons.newLaunchBrowserButton(getBlockchainInfoBrowserAction());
    blockchainInfoBrowserButton.setText(Languages.safeText(MessageKey.VIEW_IN_BLOCKCHAIN_INFO));

    contentPanel.add(transactionHashLabel);
    contentPanel.add(transactionHashValue, "wrap");

    contentPanel.add( Labels.newValueLabel(""));
    contentPanel.add(blockchainInfoBrowserButton, "wrap");

    contentPanel.add(rawTransactionLabel);
    contentPanel.add(scrollPane, "grow, push, wrap");

    contentPanel.add(sizeLabel);
    contentPanel.add(sizeValue, "wrap");
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
      final TransactionData transactionData = (TransactionData) paymentData;

      transactionHashValue.setText(transactionData.getTransactionId());
      rawTransactionValue.setText(transactionData.getRawTransaction());
      int size = transactionData.getSize();
      sizeValue.setText(Languages.safeText(MessageKey.SIZE_VALUE, size));
    }
  }

  /**
   * @return The "blockchain info browser" action
   */
  private Action getBlockchainInfoBrowserAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        try {
          PaymentData paymentData = getWizardModel().getPaymentData();
          if (paymentData != null && paymentData instanceof TransactionData) {
            TransactionData transactionData = (TransactionData) paymentData;
            final URI blockchainInfoURL = URI.create(BLOCKCHAIN_INFO_PREFIX + transactionData.getTransactionId());
            Desktop.getDesktop().browse(blockchainInfoURL);
          }
        } catch (IOException ex) {
          ExceptionHandler.handleThrowable(ex);
        }
      }
    };
  }
}
