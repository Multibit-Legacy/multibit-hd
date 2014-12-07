package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
public class TransactionDetailPanelView extends AbstractWizardPanelView<PaymentsWizardModel, TransactionDetailPanelModel> {

  private static final String BLOCKCHAIN_INFO_PREFIX = "https://blockchain.info/tx-index/";

  private static final int  MAXIMUM_ERROR_LENGTH = 100;

  private static final String ELLIPSIS = "...";

  private JLabel transactionHashValue;

  private JTextArea rawTransactionTextArea;

  /**
   * @param wizard The wizard managing the states
   */
  public TransactionDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TRANSACTION_DETAIL, AwesomeIcon.FILE_TEXT_O);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    TransactionDetailPanelModel panelModel = new TransactionDetailPanelModel(
      getPanelName()
    );
    setPanelModel(panelModel);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {
    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[shrink][shrink][grow]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    JLabel transactionHashLabel = Labels.newTransactionHash();
    transactionHashValue = Labels.newValueLabel("");
    transactionHashValue.setFont(transactionHashValue.getFont().deriveFont(Font.BOLD));

    // The raw transaction is a wall of text so needs scroll bars in many cases
    JLabel rawTransactionLabel = Labels.newRawTransaction();
    rawTransactionTextArea = TextBoxes.newReadOnlyTextArea(10, 80);
    rawTransactionTextArea.setBorder(null);

    // Raw transaction requires its own scroll pane
    JScrollPane scrollPane = ScrollPanes.newReadOnlyScrollPane(rawTransactionTextArea);

    JButton blockchainInfoBrowserButton = Buttons.newLaunchBrowserButton(getBlockchainInfoBrowserAction(), MessageKey.VIEW_IN_BLOCKCHAIN_INFO, MessageKey.VIEW_IN_BLOCKCHAIN_INFO_TOOLTIP);

    contentPanel.add(transactionHashLabel, "wrap");
    contentPanel.add(transactionHashValue, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    // Consider adding more providers here (buttons break up the information overload)
    contentPanel.add(blockchainInfoBrowserButton, "shrink,alignx left,span 2,wrap");

    contentPanel.add(rawTransactionLabel, "wrap");
    contentPanel.add(scrollPane, "grow,push,span 2," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

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

        PaymentData paymentData = getWizardModel().getPaymentData();
        if (paymentData != null && paymentData instanceof TransactionData) {
          final TransactionData transactionData = (TransactionData) paymentData;

          transactionHashValue.setText(transactionData.getTransactionId());

          // Append the size information
          int size = transactionData.getSize();
          String rawTransactionValue = "TxID:\n"
            + transactionData.getRawTransaction()
            + "\n"
            + Languages.safeText(MessageKey.SIZE)
            + ": "
            + Languages.safeText(MessageKey.SIZE_VALUE,size);

          rawTransactionTextArea.setText(rawTransactionValue);

          // Ensure the raw transaction starts at the beginning
          rawTransactionTextArea.setCaretPosition(0);

        }

      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  /**
   * @return The "blockchain info browser" action
   */
  private Action getBlockchainInfoBrowserAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        URI blockchainInfoURL = null;
        try {
          PaymentData paymentData = getWizardModel().getPaymentData();
          if (paymentData != null && paymentData instanceof TransactionData) {
            TransactionData transactionData = (TransactionData) paymentData;
            blockchainInfoURL = URI.create(BLOCKCHAIN_INFO_PREFIX + transactionData.getTransactionId());
            Desktop.getDesktop().browse(blockchainInfoURL);
          }
        } catch (Exception ex) {
          // Log the error but carry on (no need to shut down for this type of error - just show an alert)
          log.error("Failed to open URL " + blockchainInfoURL, ex);
          String message = ex.toString();
          if (message.length() >MAXIMUM_ERROR_LENGTH) {
            message = message.substring(0, MAXIMUM_ERROR_LENGTH) + ELLIPSIS;
          }
          AlertModel alertModel = new AlertModel(message, RAGStatus.AMBER);
          ViewEvents.fireAlertAddedEvent(alertModel);
        }
      }
    };
  }
}
