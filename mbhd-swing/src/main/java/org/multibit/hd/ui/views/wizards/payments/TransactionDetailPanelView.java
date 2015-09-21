package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.blockexplorer.BlockExplorer;
import org.multibit.hd.core.blockexplorer.BlockExplorers;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.utils.SafeDesktop;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.text.MessageFormat;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show transaction detail</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class TransactionDetailPanelView extends AbstractWizardPanelView<PaymentsWizardModel, TransactionDetailPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(TransactionDetailPanelView.class);

  private static final int MAXIMUM_ERROR_LENGTH = 100;

  private static final String ELLIPSIS = "...";

  private JLabel transactionHashValue;

  private JTextArea rawTransactionTextArea;

  /**
   * @param wizard The wizard managing the states
   */
  public TransactionDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.FILE_TEXT_O, MessageKey.TRANSACTION_DETAIL);

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
    contentPanel.setLayout(
      new MigLayout(
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

    BlockExplorer blockExplorer = lookupBlockExplorer();
    JButton blockExplorerBrowserButton = Buttons.newLaunchBrowserButton(
      getBlockExplorerBrowserAction(),
      MessageKey.VIEW_IN_BLOCK_EXPLORER,
      MessageKey.VIEW_IN_BLOCK_EXPLORER_TOOLTIP,
      blockExplorer.getName());

    contentPanel.add(transactionHashLabel, "wrap");
    contentPanel.add(transactionHashValue, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(blockExplorerBrowserButton, "shrink,alignx left,span 2,wrap");

    contentPanel.add(rawTransactionLabel, "wrap");
    contentPanel.add(scrollPane, "grow,push,span 2," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {
    PanelDecorator.addExitCancelPreviousNext(this, wizard);
  }

  @Override
  public void afterShow() {

    getNextButton().requestFocusInWindow();
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

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
        + Languages.safeText(MessageKey.SIZE_VALUE, size);

      rawTransactionTextArea.setText(rawTransactionValue);

      // Ensure the raw transaction starts at the beginning
      rawTransactionTextArea.setCaretPosition(0);

    }

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  /**
   * @return The "block explorer browser" action
   */
  private Action getBlockExplorerBrowserAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        URI lookupUri = null;
        try {
          PaymentData paymentData = getWizardModel().getPaymentData();
          if (paymentData != null && paymentData instanceof TransactionData) {
            TransactionData transactionData = (TransactionData) paymentData;

            // Create block explorer lookup
            BlockExplorer blockExplorer = lookupBlockExplorer();

            MessageFormat formatter = blockExplorer.getTransactionLookupMessageFormat();
            String lookupString = formatter.format(new String[]{transactionData.getTransactionId()});
            lookupUri = URI.create(lookupString);
            // Attempt to open the URI
            if (!SafeDesktop.browse(lookupUri)) {
              Sounds.playBeep(Configurations.currentConfiguration.getSound());
            }
          }
        } catch (Exception ex) {
          // Log the error but carry on (no need to shut down for this type of error - just show an alert)
          log.error("Failed to open URL " + lookupUri, ex);
          String message = ex.toString();
          if (message.length() > MAXIMUM_ERROR_LENGTH) {
            message = message.substring(0, MAXIMUM_ERROR_LENGTH) + ELLIPSIS;
          }
          final AlertModel alertModel = new AlertModel(message, RAGStatus.AMBER);
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                ControllerEvents.fireAddAlertEvent(alertModel);
              }
            });
        }
      }
    };
  }

  private BlockExplorer lookupBlockExplorer() {
    Optional<BlockExplorer> blockExplorerOptional = BlockExplorers.getBlockExplorerById(Configurations.currentConfiguration.getAppearance().getBlockExplorerId());
    BlockExplorer blockExplorer;
    if (blockExplorerOptional.isPresent()) {
      blockExplorer = blockExplorerOptional.get();
    } else {
      // Use the default
      blockExplorer = BlockExplorers.getDefaultBlockExplorer();
      // Remember for next time
      Configurations.currentConfiguration.getAppearance().setBlockExplorerId(blockExplorer.getId());
    }
    return blockExplorer;
  }
}
